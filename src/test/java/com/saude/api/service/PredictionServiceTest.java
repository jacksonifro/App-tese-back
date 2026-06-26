package com.saude.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saude.api.config.WekaModelLoader;
import com.saude.api.config.WekaModelTrainer;
import com.saude.api.dto.GestanteRequest;
import com.saude.api.dto.LlmAnalysisResult;
import com.saude.api.dto.PredictionResponse;
import com.saude.api.enums.PatientGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class PredictionServiceTest {

    private PredictionService predictionService;

    @BeforeEach
    void setUp() {
        WekaModelTrainer trainer = new WekaModelTrainer();
        WekaModelLoader loader = new WekaModelLoader(trainer);
        loader.init();

        ObjectMapper mapper = new ObjectMapper();

        GeminiLlmService geminiLlmService = Mockito.mock(GeminiLlmService.class);
        LlmAnalysisResult mockGeminiResult = LlmAnalysisResult.builder()
                .parecer("Analise mockada de teste Gemini.")
                .veredito("CURA")
                .build();
        Mockito.when(geminiLlmService.analyzeCaseAsync(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mockGeminiResult));

        GroqLlmService groqLlmService = Mockito.mock(GroqLlmService.class);
        LlmAnalysisResult mockGroqResult = LlmAnalysisResult.builder()
                .parecer("Analise mockada de teste Groq.")
                .veredito("CURA")
                .build();
        Mockito.when(groqLlmService.analyzeCaseAsync(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mockGroqResult));

        com.saude.api.repository.PacienteRepository pacienteRepo = Mockito
                .mock(com.saude.api.repository.PacienteRepository.class);
        com.saude.api.repository.AtendimentoRepository atendimentoRepo = Mockito
                .mock(com.saude.api.repository.AtendimentoRepository.class);
        com.saude.api.repository.PredicaoRepository predicaoRepo = Mockito
                .mock(com.saude.api.repository.PredicaoRepository.class);

        predictionService = new PredictionService(loader, geminiLlmService, groqLlmService, mapper, pacienteRepo, atendimentoRepo,
                predicaoRepo);
    }

    @Test
    void shouldPredictGestanteSuccessfully() {
        GestanteRequest request = new GestanteRequest();

        PredictionResponse response = assertDoesNotThrow(
                () -> predictionService.predict(PatientGroup.GESTANTE, request));

        assertNotNull(response);
        assertNotNull(response.getRandomForest());
        assertNotNull(response.getRandomForest().getMetrics());
        assertNotNull(response.getKnn());
        assertNotNull(response.getKnn().getMetrics());
        assertNotNull(response.getLogisticRegression());
        assertNotNull(response.getLogisticRegression().getMetrics());
        assertNotNull(response.getSvm());
        assertNotNull(response.getSvm().getMetrics());
        assertNotNull(response.getGradientBoosting());
        assertNotNull(response.getGradientBoosting().getMetrics());
        assertNotNull(response.getLlmAnalysis());
        assertNotNull(response.getVerdictBoard());
        assertNotNull(response.getVerdictBoard().getFinalVerdict());
        assertEquals(7, response.getVerdictBoard().getVotes().size());
        assertTrue(response.getProcessingTimeMs() >= 0);
    }
}
