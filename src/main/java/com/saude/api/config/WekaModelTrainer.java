package com.saude.api.config;

import com.saude.api.enums.PatientGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.LogitBoost;
import weka.core.Instances;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class WekaModelTrainer {

    /**
     * Verifica e retorna o modelo KNN treinado. Se não existir no disco, treina usando o ARFF.
     */
    public Classifier getOrTrainKNN(PatientGroup group, String datasetClasspath) throws Exception {
        Path modelPath = Paths.get("src", "main", "resources", "model", "Modelo_KNN_Weka_" + group + ".model");
        return getOrTrainModel(group, modelPath, datasetClasspath, new IBk(3), "KNN"); // 3 vizinhos por padrao
    }

    /**
     * Verifica e retorna o modelo Logistic Regression treinado. Se nao existir, treina.
     */
    public Classifier getOrTrainLogisticRegression(PatientGroup group, String datasetClasspath) throws Exception {
        Path modelPath = Paths.get("src", "main", "resources", "model", "Modelo_LR_Weka_" + group + ".model");
        return getOrTrainModel(group, modelPath, datasetClasspath, new Logistic(), "Logistic Regression");
    }

    /**
     * Verifica e retorna o modelo SVM (SMO) treinado. Se nao existir, treina.
     */
    public Classifier getOrTrainSVM(PatientGroup group, String datasetClasspath) throws Exception {
        Path modelPath = Paths.get("src", "main", "resources", "model", "Modelo_SVM_Weka_" + group + ".model");
        
        SMO smo = new SMO();
        // Evita usar smo.setOptions(new String[]{"-M"}) pois ele usa reflection (Utils.forName)
        // que falha em Fat JARs do Spring Boot devido ao PluginManager do Weka.
        smo.setBuildCalibrationModels(true);
        
        return getOrTrainModel(group, modelPath, datasetClasspath, smo, "SVM (SMO)");
    }

    /**
     * Verifica e retorna o modelo Gradient Boosting (LogitBoost) treinado. Se nao existir, treina.
     */
    public Classifier getOrTrainLogitBoost(PatientGroup group, String datasetClasspath) throws Exception {
        Path modelPath = Paths.get("src", "main", "resources", "model", "Modelo_BOOST_Weka_" + group + ".model");
        return getOrTrainModel(group, modelPath, datasetClasspath, new LogitBoost(), "Gradient Boosting (LogitBoost)");
    }

    private Classifier getOrTrainModel(PatientGroup group, Path targetModelPath, String datasetClasspath, Classifier emptyClassifier, String algoName) throws Exception {
        if (Files.exists(targetModelPath)) {
            log.info("[{}] Modelo '{}' encontrado no disco. Carregando-o...", group, algoName);
            return (Classifier) weka.core.SerializationHelper.read(targetModelPath.toString());
        }

        log.info("[{}] Modelo '{}' nao encontrado. Iniciando treinamento a partir do arquivo ARFF...", group, algoName);
        long start = System.currentTimeMillis();

        // Carregar a base de dados
        InputStream datasetStream = getClass().getClassLoader().getResourceAsStream(datasetClasspath);
        if (datasetStream == null) {
            throw new IllegalArgumentException("Arquivo de dataset nao encontrado para treino: " + datasetClasspath);
        }

        Instances data;
        try (InputStreamReader reader = new InputStreamReader(datasetStream)) {
            data = new Instances(reader);
            data.setClassIndex(data.numAttributes() - 1);
        }

        // Treinar o modelo
        emptyClassifier.buildClassifier(data);

        // Garantir que a pasta exista e salvar o modelo treinado
        Files.createDirectories(targetModelPath.getParent());
        weka.core.SerializationHelper.write(targetModelPath.toString(), emptyClassifier);

        long time = System.currentTimeMillis() - start;
        log.info("[{}] Treinamento e salvamento do '{}' concluidos com sucesso em {} ms. Arquivo gerado: {}", group, algoName, time, targetModelPath.getFileName());

        return emptyClassifier;
    }
}
