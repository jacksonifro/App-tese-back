# Contexto e Arquitetura Científica do Projeto

## 1. Visão Geral
Esta aplicação é uma API RESTful de nível acadêmico construída com **Java 21** e **Spring Boot 3.3.0**. O sistema tem como objetivo atuar como um motor avançado de predição médica (classificação binária de desfecho clínico: "Cura" ou "Óbito"), processando variáveis clínicas de pacientes com síndromes respiratórias (ex: SRAG/COVID-19).

Para garantir o mais alto nível de rigor e precisão, a arquitetura abandonou a abordagem de modelo único e evoluiu para um **Super-Ensemble Híbrido de 7 Vias**, que une o estado-da-arte em Estatística Clássica (via framework Weka 3.8.6) e a Fronteira da Inteligência Artificial Generativa (LLMs das Big Techs via API).

---

## 2. Tecnologias e Infraestrutura
- **Linguagem & Framework:** Java 21, Spring Boot 3.3.0 (Web, Validation).
- **Persistência de Dados:** Spring Data JPA, PostgreSQL (Neon DB).
- **Machine Learning:** Weka 3.8.6 (estatística clássica e meta-heurísticas).
- **Integração de IA:** Google Gemini API e OpenAI ChatGPT API (via REST).
- **Documentação:** OpenAPI 3 / Swagger.
- **Utilitários:** Lombok para redução de *boilerplate*.

---

## 3. O Tribunal Médico: Super-Ensemble de 7 Vias

Em vez de confiar o diagnóstico a um único algoritmo, a API processa cada requisição submetendo os dados do paciente simultaneamente a múltiplos juízes independentes:

### Modelos Matemáticos Locais (Weka)
Treinados e executados de forma determinística na memória RAM do servidor:
1. **Random Forest:** Árvores de decisão robustas, excelente para mitigar *overfitting*.
2. **KNN (K-Nearest Neighbors):** Algoritmo de instâncias (IBk) que avalia a proximidade espacial das variáveis clínicas.
3. **Regressão Logística:** Modelo puramente estatístico de calibração probabilística clássica.
4. **SVM (Support Vector Machine / SMO):** Mapeamento hiperplano para traçar as fronteiras matemáticas complexas de decisão.
5. **Gradient Boosting (LogitBoost):** Meta-classificador iterativo em comitê que corrige seus próprios resíduos.

### Modelos Generativos em Nuvem (Zero-Shot LLM)
Consultados de forma assíncrona para atuar como segunda opinião interpretativa:
6. **Google Gemini (1.5 Flash):** Analisa as variáveis como um caso clínico e emite o parecer textual seguido da predição. (Atualmente em produção no fluxo principal).
7. **OpenAI ChatGPT (GPT-3.5):** Serviço provisionado (`ChatGptLlmService`) para validação complementar sob os mesmos parâmetros de *prompt*.

---

## 4. Avaliação Científica Dinâmica (10-Fold Cross-Validation)

Para que a API não seja uma simples "caixa preta" preditiva, ela conta com a classe `ModelEvaluator`. 
Sempre que a aplicação é iniciada, antes de receber qualquer paciente, ela carrega as bases de dados históricas (`.arff`) de cada grupo e executa o **Cross-Validation de 10 Folds** para todos os 5 modelos matemáticos locais.

Esse processo divide a base em 10 pedaços, treina em 9 e testa em 1, rotacionando iterativamente. Disso, extrai-se o "Currículo Estatístico" ponderado do modelo:
- Acurácia, Sensibilidade (Recall / TPR), Especificidade (TNR), Precisão, F1-Score e, o mais importante, a **AUC-ROC** (Área Sob a Curva ROC).

Essas métricas são cacheadas em memória (`WekaModelWrapper`) e injetadas na resposta JSON para justificar a competência de cada modelo em tempo de execução.

---

## 5. Estrutura Especializada de Pacientes

A doença apresenta comportamentos drasticamente diferentes a depender do perfil fisiológico do paciente. Para mitigar vieses de generalização, a API faz o roteamento inteligente da predição baseando-se em três grupos clínicos distintos (`PatientGroup`):
- **CRIANÇA:** Foco nas particularidades pediátricas.
- **GESTANTE:** Foco nas alterações sistêmicas e imunológicas da gravidez.
- **PUÉRPERA:** Foco nas complicações pós-parto.

Cada grupo possui seu próprio DTO de requisição e seu próprio conjunto de 5 modelos Weka treinados exclusivamente em dados de pacientes de seu respectivo grupo.

---

## 6. O Sistema de Julgamento: Weighted Majority Voting

Na teoria clássica de *Ensembles*, a votação simples é subótima. Foi implementado o **Weighted Majority Voting**, criando um `Verdict Board` (Painel de Veredito) onde o peso do voto de cada juiz é proporcional à sua capacidade histórica.

### 6.1 A Métrica Mestra: AUC-ROC
A métrica escolhida para balizar o peso matemático dos modelos locais foi a **AUC-ROC**, que mensura a capacidade do modelo em discriminar "Cura" de "Óbito". Se o *Random Forest* atinge uma AUC-ROC de **0.94**, seu voto soma **0.94 pontos** no placar respectivo.

### 6.2 O Peso Científico das LLMs (Zero-Shot)
Baseando-se no estado-da-arte da literatura de LLMs médicos, o sistema define um "Peso de Confiança Estático" de **0.85** (85%) para as predições de texto. Esse peso permite que a IA tenha influência significativa sem obliterar a matemática determinística. O LLM é instruído por *prompt* a retornar uma *tag* explícita (`[PREDICAO: OBITO]` ou `[PREDICAO: CURA]`) que é parseada pelo sistema.

### 6.3 O Placar Final
As pontuações (`scorePanel`) são somadas em duas caixas ("Cura" vs "Óbito"). O Veredito Final (`finalVerdict`) é declarado com base no acúmulo de pontos ponderados. Essa abordagem evita "empates técnicos" e reflete a intersecção ideal entre precisão algorítmica local e heurística generativa.

---

## 7. Modelagem de Domínio e Persistência (JPA)

A API não apenas infere o resultado, mas atua como um prontuário eletrônico unificado:
- **`Paciente`**: Raiz da entidade, mapeia os dados vitais e demográficos. Está ligado a `Endereco`, `Contato`, `Vacinacao` e `Comorbidade` via relações One-To-One (`CascadeType.ALL`).
- **`Atendimento`**: Registra o episódio clínico específico da infecção, descrevendo os sintomas (febre, tosse, saturação) e métricas hospitalares (dias em UTI, suporte ventilatório).
- **`Predicao`**: Grava auditoria completa de cada predição disparada, incluindo a *DataHora*, o payload exato que foi enviado para inferência, a resposta completa do Ensemble e o *Tempo de Processamento (ms)*. 

Todo esse ecossistema é salvo em um banco de dados relacional (PostgreSQL), garantindo rastreabilidade acadêmica para a tese e possibilitando futuras reavaliações do banco para calibração contínua dos modelos.
