package com.saude.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saude.api.config.WekaModelLoader;
import com.saude.api.config.WekaModelWrapper;
import com.saude.api.dto.BasePredictionRequest;
import com.saude.api.dto.ModelPredictionResult;
import com.saude.api.dto.PredictionResponse;
import com.saude.api.dto.VerdictBoard;
import com.saude.api.dto.VoteRecord;
import com.saude.api.enums.PatientGroup;
import com.saude.api.util.InstanceBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final WekaModelLoader wekaModelLoader;
    private final GeminiLlmService geminiLlmService;
    private final GroqLlmService groqLlmService;
    private final ObjectMapper objectMapper;
    private final com.saude.api.repository.PacienteRepository pacienteRepository;
    private final com.saude.api.repository.AtendimentoRepository atendimentoRepository;
    private final com.saude.api.repository.PredicaoRepository predicaoRepository;

    @org.springframework.transaction.annotation.Transactional
    public PredictionResponse predict(com.saude.api.dto.NovaPredicaoRequest request) {
        com.saude.api.entity.Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Paciente não encontrado: " + request.getPacienteId()));

        PatientGroup group = PatientGroup.CRIANCA;
        if ("Sim".equalsIgnoreCase(paciente.getGestante())) group = PatientGroup.GESTANTE;
        else if ("Sim".equalsIgnoreCase(paciente.getPuerpera())) group = PatientGroup.PUERPERA;

        com.saude.api.entity.Atendimento atendimento = com.saude.api.entity.Atendimento.builder()
                .dataHora(java.time.LocalDateTime.now())
                .paciente(paciente)
                .febre(request.getEpisodio().getFebre())
                .tosse(request.getEpisodio().getTosse())
                .dispneia(request.getEpisodio().getDispneia())
                .fadiga(request.getEpisodio().getFadiga())
                .dorAbdominal(request.getEpisodio().getDorAbdominal())
                .dorGarganta(request.getEpisodio().getDorGarganta())
                .saturacao(request.getEpisodio().getSaturacao())
                .desconfortoRespiratorio(request.getEpisodio().getDesconfortoRespiratorio())
                .diarreia(request.getEpisodio().getDiarreia())
                .vomito(request.getEpisodio().getVomito())
                .perdaOlfato(request.getEpisodio().getPerdaOlfato())
                .perdaPaladar(request.getEpisodio().getPerdaPaladar())
                .internacao(request.getEpisodio().getInternacao())
                .uti(request.getEpisodio().getUti())
                .diasUTI(request.getEpisodio().getDiasUTI())
                .suporteVentilatorio(request.getEpisodio().getSuporteVentilatorio())
                .build();
        atendimento = atendimentoRepository.save(atendimento);

        BasePredictionRequest req = group == PatientGroup.GESTANTE ? new com.saude.api.dto.GestanteRequest() :
                                   group == PatientGroup.PUERPERA ? new com.saude.api.dto.PuerperaRequest() :
                                   new com.saude.api.dto.CriancaRequest();

        java.util.function.Function<String, String> mapYN = (val) -> {
            if (val == null) return "Nao";
            if (val.equalsIgnoreCase("Sim")) return "Sim";
            if (val.toLowerCase().startsWith("sim")) return val;
            return "Nao";
        };
        java.util.function.BiFunction<String, String, String> mapEnum = (val, def) -> {
            if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("Ignorado")) return def;
            return val;
        };

        req.setSG_UF_NOT(mapEnum.apply(paciente.getEndereco() != null ? paciente.getEndereco().getUf() : null, "RO"));
        req.setCS_SEXO(mapEnum.apply(paciente.getSexo(), "Feminino"));
        req.setNU_IDADE_N(java.time.Period.between(paciente.getDataNascimento(), java.time.LocalDate.now()).getYears());
        req.setCS_RACA(mapEnum.apply(paciente.getRaca(), "Parda"));
        req.setCS_ZONA(mapEnum.apply(paciente.getEndereco() != null ? paciente.getEndereco().getZona() : null, "Urbana"));
        req.setNOSOCOMIAL(mapYN.apply(request.getEpisodio().getNosocomial()));

        req.setFEBRE(mapYN.apply(request.getEpisodio().getFebre()));
        req.setTOSSE(mapYN.apply(request.getEpisodio().getTosse()));
        req.setGARGANTA(mapYN.apply(request.getEpisodio().getDorGarganta()));
        req.setDISPNEIA(mapYN.apply(request.getEpisodio().getDispneia()));
        req.setDESC_RESP(mapYN.apply(request.getEpisodio().getDesconfortoRespiratorio()));
        req.setSATURACAO(mapYN.apply(request.getEpisodio().getSaturacao()));
        req.setDIARREIA(mapYN.apply(request.getEpisodio().getDiarreia()));
        req.setVOMITO(mapYN.apply(request.getEpisodio().getVomito()));
        req.setDOR_ABD(mapYN.apply(request.getEpisodio().getDorAbdominal()));
        req.setFADIGA(mapYN.apply(request.getEpisodio().getFadiga()));
        req.setPERD_OLFT(mapYN.apply(request.getEpisodio().getPerdaOlfato()));
        req.setPERD_PALA(mapYN.apply(request.getEpisodio().getPerdaPaladar()));

        com.saude.api.entity.Comorbidade c = paciente.getComorbidade();
        req.setFATOR_RISC(mapYN.apply(c != null ? c.getFatorRisco() : null));
        req.setCARDIOPATI(mapYN.apply(c != null ? c.getCardiopatia() : null));
        req.setHEMATOLOGI(mapYN.apply(c != null ? c.getHematologica() : null));
        req.setSIND_DOWN(mapYN.apply(c != null ? c.getSindromeDown() : null));
        req.setHEPATICA(mapYN.apply(c != null ? c.getHepatica() : null));
        req.setASMA(mapYN.apply(c != null ? c.getAsma() : null));
        req.setDIABETES(mapYN.apply(c != null ? c.getDiabetes() : null));
        req.setNEUROLOGIC(mapYN.apply(c != null ? c.getNeurologica() : null));
        req.setPNEUMOPATI(mapYN.apply(c != null ? c.getPneumopatia() : null));
        req.setIMUNODEPRE(mapYN.apply(c != null ? c.getImunodepressao() : null));
        req.setRENAL(mapYN.apply(c != null ? c.getRenal() : null));
        req.setOBESIDADE(mapYN.apply(c != null ? c.getObesidade() : null));

        com.saude.api.entity.Vacinacao v = paciente.getVacinacao();
        req.setVACINA_GRIPE(mapYN.apply(v != null ? v.getVacinaInfluenza() : null));
        req.setVACINA_COV(mapYN.apply(v != null ? v.getVacinaCovid() : null));
        req.setVACINA_COV_1_DOSE(mapYN.apply(v != null ? v.getPrimeiraDose() : null));
        req.setVACINA_COV_2_DOSE(mapYN.apply(v != null ? v.getSegundaDose() : null));
        req.setVACINA_COV_3_DOSE(mapYN.apply(v != null ? v.getTerceiraDose() : null));

        req.setINTERNACAO(mapYN.apply(request.getEpisodio().getInternacao()));
        req.setUTI(mapYN.apply(request.getEpisodio().getUti()));
        req.setDIAS_UTI(request.getEpisodio().getDiasUTI() != null ? request.getEpisodio().getDiasUTI() : 0);
        req.setSUPORT_VEN(mapYN.apply(request.getEpisodio().getSuporteVentilatorio()));

        PredictionResponse response = predict(group, req);

        try {
            com.saude.api.entity.Predicao predicao = com.saude.api.entity.Predicao.builder()
                    .dataHora(java.time.LocalDateTime.now())
                    .atendimento(atendimento)
                    .payloadEnviado(objectMapper.writeValueAsString(req))
                    .respostaCompleta(objectMapper.writeValueAsString(response))
                    .resultado(response.getVerdictBoard().getFinalVerdict())
                    .tempoProcessamentoMs(response.getProcessingTimeMs())
                    .build();
            predicaoRepository.save(predicao);
        } catch (Exception e) {
            log.error("Erro ao salvar predição no banco de dados", e);
        }

        return response;
    }

    public PredictionResponse predict(PatientGroup group, BasePredictionRequest requestDto) {
        log.info("Iniciando predicao multipla (Ensemble 7 vias) para o grupo: {}", group);
        long startTime = System.currentTimeMillis();

        WekaModelWrapper wrapper = wekaModelLoader.getModels().get(group);
        if (wrapper == null) {
            throw new IllegalArgumentException("Modelos nao encontrados para o grupo: " + group);
        }

        // 1. Disparar chamadas LLM Assincronas (Paralelas)
        CompletableFuture<String> geminiFuture = geminiLlmService.analyzeCaseAsync(requestDto, group.name());
        CompletableFuture<String> groqFuture = groqLlmService.analyzeCaseAsync(requestDto, group.name());

        // 2. Preparar Instancia do Weka
        Map<String, Object> attributes = objectMapper.convertValue(requestDto, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> upperAttributes = new HashMap<>();
        attributes.forEach((k, v) -> upperAttributes.put(k.toUpperCase(), v));

        Instance instance = InstanceBuilder.buildInstance(wrapper.getHeader(), upperAttributes);

        try {
            // 3. Executar Modelos Weka (Matematicos)
            ModelPredictionResult rfResult = runAlgorithm(wrapper.getRandomForest(), wrapper.getHeader(), instance, wrapper.getMetrics().get("RandomForest"));
            ModelPredictionResult knnResult = runAlgorithm(wrapper.getKnn(), wrapper.getHeader(), instance, wrapper.getMetrics().get("KNN"));
            ModelPredictionResult lrResult = runAlgorithm(wrapper.getLogisticRegression(), wrapper.getHeader(), instance, wrapper.getMetrics().get("LogisticRegression"));
            ModelPredictionResult svmResult = runAlgorithm(wrapper.getSvm(), wrapper.getHeader(), instance, wrapper.getMetrics().get("SVM"));
            ModelPredictionResult gbResult = runAlgorithm(wrapper.getGradientBoosting(), wrapper.getHeader(), instance, wrapper.getMetrics().get("GradientBoosting"));

            // 4. Aguardar o resultado das LLMs (TimeOut de 15s para nao travar a API)
            String geminiAnalysis = "Indisponivel";
            String groqAnalysis = "Indisponivel";
            try {
                CompletableFuture.allOf(geminiFuture, groqFuture).get(15, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Timeout ou Erro aguardando LLMs.");
            }
            geminiAnalysis = geminiFuture.isDone() && !geminiFuture.isCompletedExceptionally() ? geminiFuture.join() : "Erro de integracao ou Timeout (Gemini).";
            groqAnalysis = groqFuture.isDone() && !groqFuture.isCompletedExceptionally() ? groqFuture.join() : "Erro de integracao ou Timeout (Groq).";

            // 5. Consolidar os resultados via Votacao Ponderada (Weighted Majority Voting)
            VerdictBoard board = calculateConsolidated(rfResult, knnResult, lrResult, svmResult, gbResult, geminiAnalysis, groqAnalysis);

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Predicao consolidada concluida em {} ms. Veredito final: {}", processingTime, board.getFinalVerdict());

            return PredictionResponse.builder()
                    .randomForest(rfResult)
                    .knn(knnResult)
                    .logisticRegression(lrResult)
                    .svm(svmResult)
                    .gradientBoosting(gbResult)
                    .llmAnalysis(geminiAnalysis)
                    .groqAnalysis(groqAnalysis)
                    .verdictBoard(board)
                    .processingTimeMs(processingTime)
                    .build();

        } catch (Exception e) {
            log.error("Erro ao executar predicao pelo Weka no grupo {}", group, e);
            throw new RuntimeException("Erro interno durante a classificacao.", e);
        }
    }

    private ModelPredictionResult runAlgorithm(Classifier classifier, Instances header, Instance instance, com.saude.api.dto.ModelMetrics metrics) throws Exception {
        double[] distribution = classifier.distributionForInstance(instance);
        int classIndex = (int) classifier.classifyInstance(instance);
        String predictedClass = header.classAttribute().value(classIndex);

        Map<String, Double> probabilities = new HashMap<>();
        for (int i = 0; i < distribution.length; i++) {
            String className = header.classAttribute().value(i);
            probabilities.put(className, Math.round(distribution[i] * 10000.0) / 10000.0);
        }

        return ModelPredictionResult.builder()
                .predictedClass(predictedClass)
                .probabilities(probabilities)
                .metrics(metrics)
                .build();
    }

    private VerdictBoard calculateConsolidated(
            ModelPredictionResult rf, ModelPredictionResult knn, ModelPredictionResult lr, 
            ModelPredictionResult svm, ModelPredictionResult gb, 
            String geminiTxt, String groqTxt) {

        List<VoteRecord> votes = new ArrayList<>();
        Map<String, Double> scorePanel = new HashMap<>();
        scorePanel.put("Obito", 0.0);
        scorePanel.put("Cura", 0.0);

        registerWekaVote(votes, scorePanel, "Random Forest", rf);
        registerWekaVote(votes, scorePanel, "KNN", knn);
        registerWekaVote(votes, scorePanel, "Logistic Regression", lr);
        registerWekaVote(votes, scorePanel, "SVM", svm);
        registerWekaVote(votes, scorePanel, "Gradient Boosting", gb);

        registerLlmVote(votes, scorePanel, "Gemini", geminiTxt);
        registerLlmVote(votes, scorePanel, "Groq (Llama3.1)", groqTxt);

        String finalVerdict = scorePanel.get("Obito") > scorePanel.get("Cura") ? "Obito" : "Cura";

        // Formatar valores para 2 casas decimais no painel final
        scorePanel.put("Obito", Math.round(scorePanel.get("Obito") * 100.0) / 100.0);
        scorePanel.put("Cura", Math.round(scorePanel.get("Cura") * 100.0) / 100.0);

        return VerdictBoard.builder()
                .votes(votes)
                .scorePanel(scorePanel)
                .finalVerdict(finalVerdict)
                .build();
    }

    private void registerWekaVote(List<VoteRecord> votes, Map<String, Double> scorePanel, String judge, ModelPredictionResult result) {
        String vote = result.getPredictedClass();
        // Peso do voto e a AUC-ROC. Se nao tiver, peso neutro de 0.5
        double weight = (result.getMetrics() != null && result.getMetrics().getAucRoc() > 0) ? result.getMetrics().getAucRoc() : 0.5;
        votes.add(VoteRecord.builder().judge(judge).vote(vote).weight(weight).build());
        
        // Atualiza a soma total com o peso deste voto
        scorePanel.put(vote, scorePanel.getOrDefault(vote, 0.0) + weight);
    }

    private void registerLlmVote(List<VoteRecord> votes, Map<String, Double> scorePanel, String judge, String text) {
        String textUpper = text.toUpperCase();
        
        // Verifica se a LLM de fato retornou uma predição válida
        if (textUpper.contains("[PREDICAO: OBITO]") || textUpper.contains("[PREDICAO: CURA]")) {
            String vote = textUpper.contains("[PREDICAO: OBITO]") ? "Obito" : "Cura";
            // Peso cientifico fixo (estimado) para LLMs em Zero-Shot
            double weight = 0.85; 
            votes.add(VoteRecord.builder().judge(judge).vote(vote).weight(weight).build());
            scorePanel.put(vote, scorePanel.getOrDefault(vote, 0.0) + weight);
        } else {
            // Em caso de erro de cota (429) ou indisponibilidade (503), o voto é anulado
            votes.add(VoteRecord.builder().judge(judge).vote("Abstencao (Erro)").weight(0.0).build());
        }
    }
}
