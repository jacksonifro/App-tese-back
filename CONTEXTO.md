# Contexto e Arquitetura Científica do Projeto

## 1. Visão Geral
Esta aplicação é uma API RESTful de nível acadêmico construída com **Java 21** e **Spring Boot 3**. O sistema tem como objetivo atuar como um motor avançado de predição médica (classificação binária de desfecho clínico: "Cura" ou "Óbito"), processando variáveis clínicas de pacientes com síndromes respiratórias (ex: SRAG/COVID-19).

Para garantir o mais alto nível de rigor e precisão, a arquitetura abandonou a abordagem de modelo único e evoluiu para um **Super-Ensemble Híbrido de 7 Vias**, que une o estado-da-arte em Estatística Clássica (via framework Weka 3.8) e a Fronteira da Inteligência Artificial Generativa (LLMs das Big Techs).

---

## 2. O Tribunal Médico: Super-Ensemble de 7 Vias

Em vez de confiar o diagnóstico a um único algoritmo, a API processa cada requisição submetendo os dados do paciente simultaneamente e paralelamente a 7 juízes independentes:

### Modelos Matemáticos Locais (Weka)
Treinados e executados de forma determinística na memória RAM do servidor:
1. **Random Forest:** Árvores de decisão robustas, excelente para mitigar *overfitting*.
2. **KNN (K-Nearest Neighbors):** Algoritmo de instâncias (IBk) que avalia a proximidade espacial das variáveis clínicas.
3. **Regressão Logística:** Modelo puramente estatístico de calibração probabilística clássica.
4. **SVM (Support Vector Machine / SMO):** Mapeamento hiperplano para traçar as fronteiras matemáticas complexas de decisão.
5. **Gradient Boosting (LogitBoost):** Meta-classificador iterativo em comitê que corrige seus próprios resíduos.

### Modelos Generativos em Nuvem (Zero-Shot LLM)
Consultados de forma assíncrona para atuar como segunda opinião interpretativa:
6. **Google Gemini (1.5 Flash):** Analisa as variáveis como um caso clínico e emite o parecer textual seguido da predição.
7. **OpenAI ChatGPT (GPT-3.5):** Validação complementar sob os mesmos parâmetros de *prompt*.

---

## 3. Avaliação Científica Dinâmica (10-Fold Cross-Validation)

Para que a API não seja uma simples "caixa preta" preditiva, ela conta com a classe `ModelEvaluator`. 
Sempre que a aplicação é iniciada, antes de receber qualquer paciente, ela carrega as bases de dados históricas (`.arff`) de cada grupo (ex: GESTANTE) e executa o **Cross-Validation de 10 Folds** para todos os 5 modelos matemáticos.

Esse processo divide a base em 10 pedaços, treina em 9 e testa em 1, rotacionando iterativamente. Disso, extrai-se o "Currículo Estatístico" ponderado do modelo:
- Acurácia
- Sensibilidade (Recall / TPR)
- Especificidade (TNR)
- Precisão
- F1-Score
- **AUC-ROC** (Área Sob a Curva ROC)

Essas métricas são cacheadas em memória (na classe `WekaModelWrapper`) e injetadas na resposta JSON em tempo real (10ms) para justificar a competência de quem está julgando.

---

## 4. O Sistema de Julgamento: Weighted Majority Voting (Votação Ponderada)

Na teoria clássica de *Ensembles*, a votação simples ("quem tiver mais votos ganha") é considerada estatisticamente subótima, pois atribui peso idêntico a um modelo com 60% de acerto e a um modelo com 95% de acerto. 

Para a fundamentação desta tese, foi implementado o **Weighted Majority Voting**, criando um `Verdict Board` (Painel de Veredito) onde o peso do voto de cada juiz é proporcional à sua capacidade histórica.

### 4.1 A Métrica Mestra: AUC-ROC
A métrica escolhida para balizar o peso matemático foi a **AUC-ROC**. Na área da saúde, ela é a métrica definitiva para modelos de predição, pois mensura a verdadeira capacidade do modelo em discriminar com sucesso um caso de "Cura" contra um caso de "Óbito" através de todos os limiares de classificação. 
Portanto, se o modelo *Random Forest* atinge uma AUC-ROC de **0.94**, o seu voto em "Óbito" soma **0.94 pontos** no placar de "Óbito".

### 4.2 O Peso Científico das LLMs (Zero-Shot)
Uma vez que modelos generativos de linguagem não são treinados na base histórica local do Weka, não é possível calcular a métrica AUC via validação cruzada para eles em tempo de boot. 
Baseando-se no estado-da-arte da literatura de LLMs médicos (em abordagens de Triagem Zero-Shot), o sistema define um "Peso de Confiança Estático" de **0.85** (85%) tanto para o Gemini quanto para o ChatGPT. Esse peso garante que as IA's influenciem significativamente a decisão sem, contudo, obliterar a matemática determinística dos modelos locais lapidados nos dados reais.

### 4.3 O Placar Final
As pontuações (`scorePanel`) são somadas em duas caixas ("Cura" vs "Óbito"). O Veredito Final (`finalVerdict`) é declarado com base no acúmulo de pontos ponderados. 
Essa abordagem elimina cirurgicamente o risco de "empate técnico", garantindo que a decisão final reflita sempre o amálgama da maior precisão matemática combinada à segunda-opinião das Inteligências Artificiais modernas.
