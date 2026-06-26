package com.saude.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saude.api.dto.BasePredictionRequest;
import com.saude.api.dto.LlmAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqLlmService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.model}")
    private String model;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public CompletableFuture<LlmAnalysisResult> analyzeCaseAsync(BasePredictionRequest request, String patientGroup) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildPrompt(request, patientGroup);
                String responseText = callGroq(prompt);
                
                if (responseText.contains("```json")) {
                    responseText = responseText.substring(responseText.indexOf("```json") + 7);
                    if (responseText.contains("```")) {
                        responseText = responseText.substring(0, responseText.lastIndexOf("```"));
                    }
                }
                
                try {
                    return objectMapper.readValue(responseText.trim(), LlmAnalysisResult.class);
                } catch (Exception e) {
                    log.warn("Erro ao fazer parse do JSON da Groq: {}. Fallback para texto plano.", responseText);
                    return LlmAnalysisResult.builder()
                            .parecer(responseText)
                            .veredito("INDEFINIDO")
                            .build();
                }
                
            } catch (Exception e) {
                log.error("Erro ao chamar a LLM Groq", e);
                return LlmAnalysisResult.builder().parecer("Erro na analise da Groq: " + e.getMessage()).build();
            }
        });
    }

    private String buildPrompt(BasePredictionRequest request, String patientGroup) {
        StringBuilder sb = new StringBuilder();
        sb.append("Atue como um especialista em infectologia. ");
        sb.append("Analise o caso de um paciente do grupo ").append(patientGroup).append(" com as seguintes caracteristicas (SRAG/COVID):\n");
        
        Map<String, Object> attrs = objectMapper.convertValue(request, Map.class);
        attrs.forEach((k, v) -> {
            if (v != null) {
                sb.append("- ").append(k).append(": ").append(v).append("\n");
            }
        });
        
        sb.append("\nCom base nestes dados, forneca a sua analise retornando ESTRITAMENTE o seguinte formato JSON:\n");
        sb.append("{\n");
        sb.append("  \"parecer\": \"seu breve parecer de no max 3 linhas justificando o desfecho\",\n");
        sb.append("  \"veredito\": \"CURA ou OBITO\",\n");
        sb.append("  \"topFeatures\": [\n");
        sb.append("    { \"feature\": \"Nome da Variavel (ex: Idade, UTI, Saturacao)\", \"impact\": \"Alto, Medio ou Critico\" }\n");
        sb.append("  ]\n");
        sb.append("}\n");
        sb.append("Retorne as 3 ou 4 variaveis com maior peso na sua decisao.");
        return sb.toString();
    }

    private String callGroq(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            } catch (Exception e) {
                log.warn("Erro ao fazer parse da resposta do Groq: {}", response.getBody());
                return "Parecer gerado pela Groq, mas formato de resposta inesperado.";
            }
        }

        throw new RuntimeException("Falha na chamada da API da Groq: " + response.getStatusCode());
    }
}
