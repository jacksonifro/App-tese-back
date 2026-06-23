package com.saude.api.config;

import com.saude.api.enums.PatientGroup;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WekaModelLoader {

    private final WekaModelTrainer wekaModelTrainer;

    @Getter
    private final Map<PatientGroup, WekaModelWrapper> models = new EnumMap<>(PatientGroup.class);

    @Getter
    private final java.time.LocalDateTime initializationTime = java.time.LocalDateTime.now();

    @PostConstruct
    public void init() {
        log.info("Inicializando carregamento/treinamento dos modelos Weka...");
        
        try {
            // Registra classes padrao do Weka manualmente para evitar erro "Não foi possível encontrar uma classe permitida"
            // Isso ocorre em executaveis do Spring Boot (Fat JAR) porque o Weka não consegue ler seus proprios arquivos .props
            weka.core.PluginManager.addPlugin(weka.classifiers.Classifier.class.getName(), "weka.classifiers.functions.Logistic", "weka.classifiers.functions.Logistic");
            weka.core.PluginManager.addPlugin(weka.classifiers.Classifier.class.getName(), "weka.classifiers.functions.SMO", "weka.classifiers.functions.SMO");
            weka.core.PluginManager.addPlugin(weka.classifiers.Classifier.class.getName(), "weka.classifiers.lazy.IBk", "weka.classifiers.lazy.IBk");
            weka.core.PluginManager.addPlugin(weka.classifiers.Classifier.class.getName(), "weka.classifiers.meta.LogitBoost", "weka.classifiers.meta.LogitBoost");
            weka.core.PluginManager.addPlugin(weka.classifiers.Classifier.class.getName(), "weka.classifiers.trees.RandomForest", "weka.classifiers.trees.RandomForest");
            weka.core.PluginManager.addPlugin(weka.classifiers.Classifier.class.getName(), "weka.classifiers.trees.RandomTree", "weka.classifiers.trees.RandomTree");
            weka.core.PluginManager.addPlugin(weka.classifiers.Classifier.class.getName(), "weka.classifiers.trees.DecisionStump", "weka.classifiers.trees.DecisionStump");
            weka.core.PluginManager.addPlugin(weka.classifiers.functions.supportVector.Kernel.class.getName(), "weka.classifiers.functions.supportVector.PolyKernel", "weka.classifiers.functions.supportVector.PolyKernel");
            weka.core.PluginManager.addPlugin(weka.core.neighboursearch.NearestNeighbourSearch.class.getName(), "weka.core.neighboursearch.LinearNNSearch", "weka.core.neighboursearch.LinearNNSearch");
        } catch (Exception e) {
            log.warn("Nao foi possivel registrar os plugins Weka: " + e.getMessage());
        }

        long startTime = System.currentTimeMillis();

        try {
            loadGroupModels(PatientGroup.GESTANTE, "model/Modelo_RF_Weka_Gestante.model", "dataset/Base_20_21_gestantes.arff");
            loadGroupModels(PatientGroup.PUERPERA, "model/Modelo_RF_Weka_Puerpera.model", "dataset/Base_20_21_puerpera.arff");
            loadGroupModels(PatientGroup.CRIANCA, "model/Modelo_RF_Weka_criancas.model", "dataset/Base_20_21_criancas0a3.arff");

            log.info("Todos os modelos Weka foram carregados/treinados com sucesso em {} ms.", (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            log.error("Erro critico ao carregar modelos Weka. A aplicacao nao funcionara corretamente.", e);
            throw new RuntimeException("Falha ao inicializar modelos Weka", e);
        }
    }

    private void loadGroupModels(PatientGroup group, String rfPath, String datasetClasspath) throws Exception {
        log.info("Configurando modelos para o grupo: {}", group);

        // 1. Carregar Random Forest pré-existente
        InputStream rfStream = getClass().getClassLoader().getResourceAsStream(rfPath);
        if (rfStream == null) {
            throw new IllegalArgumentException("Arquivo do modelo Random Forest nao encontrado: " + rfPath);
        }
        Classifier randomForest = (Classifier) weka.core.SerializationHelper.read(rfStream);

        // 2. Treinar ou Carregar KNN
        Classifier knn = wekaModelTrainer.getOrTrainKNN(group, datasetClasspath);

        // 3. Treinar ou Carregar Logistic Regression
        Classifier logisticRegression = wekaModelTrainer.getOrTrainLogisticRegression(group, datasetClasspath);

        // 4. Treinar ou Carregar SVM (SMO)
        Classifier svm = wekaModelTrainer.getOrTrainSVM(group, datasetClasspath);

        // 5. Treinar ou Carregar Gradient Boosting (LogitBoost)
        Classifier gradientBoosting = wekaModelTrainer.getOrTrainLogitBoost(group, datasetClasspath);

        // 6. Carregar dataset (apenas o cabecalho para referencia de campos e uso na validacao cruzada)
        InputStream datasetStream = getClass().getClassLoader().getResourceAsStream(datasetClasspath);
        if (datasetStream == null) {
            throw new IllegalArgumentException("Arquivo de dataset nao encontrado: " + datasetClasspath);
        }
        Instances instances;
        try (InputStreamReader reader = new InputStreamReader(datasetStream)) {
            instances = new Instances(reader);
            instances.setClassIndex(instances.numAttributes() - 1); 
        }
        Instances header = new Instances(instances, 0);

        // 7. Avaliar os modelos via Cross-Validation (10-fold) ou Carregar do Cache
        Map<String, com.saude.api.dto.ModelMetrics> metricsMap = new java.util.HashMap<>();
        metricsMap.put("RandomForest", com.saude.api.util.ModelEvaluator.evaluateModelAndCache(randomForest, instances, "Random Forest", "Modelo_RF_Weka_" + group + ".metrics.json"));
        metricsMap.put("KNN", com.saude.api.util.ModelEvaluator.evaluateModelAndCache(knn, instances, "KNN", "Modelo_KNN_Weka_" + group + ".metrics.json"));
        metricsMap.put("LogisticRegression", com.saude.api.util.ModelEvaluator.evaluateModelAndCache(logisticRegression, instances, "Logistic Regression", "Modelo_LR_Weka_" + group + ".metrics.json"));
        metricsMap.put("SVM", com.saude.api.util.ModelEvaluator.evaluateModelAndCache(svm, instances, "SVM", "Modelo_SVM_Weka_" + group + ".metrics.json"));
        metricsMap.put("GradientBoosting", com.saude.api.util.ModelEvaluator.evaluateModelAndCache(gradientBoosting, instances, "Gradient Boosting", "Modelo_BOOST_Weka_" + group + ".metrics.json"));

        models.put(group, new WekaModelWrapper(randomForest, knn, logisticRegression, svm, gradientBoosting, header, metricsMap));
        log.info("Todos os modelos matematicos do grupo {} subiram perfeitamente.", group);
    }
}
