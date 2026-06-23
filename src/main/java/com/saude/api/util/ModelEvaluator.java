package com.saude.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saude.api.dto.ModelMetrics;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Slf4j
public class ModelEvaluator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ModelMetrics evaluateModelAndCache(Classifier classifier, Instances dataset, String modelName, String cacheFileName) {
        try {
            Path cachePath = Paths.get("src", "main", "resources", "model", cacheFileName);
            
            if (Files.exists(cachePath)) {
                log.info("Lendo metricas de cache para o modelo {} no arquivo {}", modelName, cacheFileName);
                return objectMapper.readValue(cachePath.toFile(), ModelMetrics.class);
            }

            log.info("Cache de metricas nao encontrado para o modelo {}. Executando 10-fold Cross-Validation...", modelName);
            long start = System.currentTimeMillis();

            Evaluation eval = new Evaluation(dataset);
            
            // 10-fold cross validation
            eval.crossValidateModel(classifier, dataset, 10, new Random(1));

            double accuracy = eval.pctCorrect() / 100.0;
            double precision = eval.weightedPrecision();
            double sensitivity = eval.weightedTruePositiveRate(); // Recall
            double specificity = 1.0 - eval.weightedFalsePositiveRate();
            double f1Score = eval.weightedFMeasure();
            double aucRoc = eval.weightedAreaUnderROC();

            long time = System.currentTimeMillis() - start;
            log.info("Cross-Validation do modelo {} concluida em {} ms. Acuracia: {}%", modelName, time, String.format("%.2f", accuracy * 100));

            ModelMetrics metrics = ModelMetrics.builder()
                    .accuracy(round(accuracy))
                    .precision(round(precision))
                    .sensitivity(round(sensitivity))
                    .specificity(round(specificity))
                    .f1Score(round(f1Score))
                    .aucRoc(round(aucRoc))
                    .build();

            // Salvar no disco para as proximas inicializacoes
            Files.createDirectories(cachePath.getParent());
            objectMapper.writeValue(cachePath.toFile(), metrics);
            log.info("Metricas salvas no cache: {}", cachePath.toString());

            return metrics;

        } catch (Exception e) {
            log.error("Erro ao avaliar ou ler cache do modelo: " + modelName, e);
            // Retorna zerado em caso de erro para nao travar a API
            return ModelMetrics.builder().build(); 
        }
    }

    private static double round(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0.0;
        return Math.round(value * 10000.0) / 10000.0;
    }
}
