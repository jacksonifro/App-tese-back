package com.saude.api.config;

import com.saude.api.enums.PatientGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WekaModelLoaderTest {

    @Test
    void shouldLoadModelsSuccessfully() {
        WekaModelTrainer trainer = new WekaModelTrainer();
        WekaModelLoader loader = new WekaModelLoader(trainer);
        
        // Verifica se o metodo init() executa sem lancar excecoes (arquivos encontrados e validos)
        assertDoesNotThrow(loader::init);
        
        // Verifica se carregou exatamente os 3 modelos
        assertEquals(3, loader.getModels().size());
        
        // Verifica se a estrutura do wrapper foi populada
        WekaModelWrapper gestanteWrapper = loader.getModels().get(PatientGroup.GESTANTE);
        assertNotNull(gestanteWrapper);
        assertNotNull(gestanteWrapper.getRandomForest());
        assertNotNull(gestanteWrapper.getKnn());
        assertNotNull(gestanteWrapper.getLogisticRegression());
        assertNotNull(gestanteWrapper.getSvm());
        assertNotNull(gestanteWrapper.getGradientBoosting());
        assertNotNull(gestanteWrapper.getHeader());
        
        // Verifica se as metricas estatisticas foram geradas
        assertNotNull(gestanteWrapper.getMetrics());
        assertEquals(5, gestanteWrapper.getMetrics().size());
        
        // Verifica se o cabecalho ARFF tem atributos definidos
        assertTrue(gestanteWrapper.getHeader().numAttributes() > 0);
    }
}
