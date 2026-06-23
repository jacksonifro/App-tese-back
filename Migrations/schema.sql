-- DDL Script para PostgreSQL (Neon DB)

CREATE TABLE endereco (
    id BIGSERIAL PRIMARY KEY,
    cep VARCHAR(255),
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    municipio VARCHAR(255),
    zona VARCHAR(255),
    uf VARCHAR(2)
);

CREATE TABLE contato (
    id BIGSERIAL PRIMARY KEY,
    telefone VARCHAR(255),
    celular VARCHAR(255),
    email VARCHAR(255),
    contato_emergencia VARCHAR(255),
    telefone_emergencia VARCHAR(255)
);

CREATE TABLE comorbidade (
    id BIGSERIAL PRIMARY KEY,
    fator_risco VARCHAR(255) NOT NULL,
    diabetes VARCHAR(255),
    cardiopatia VARCHAR(255),
    asma VARCHAR(255),
    obesidade VARCHAR(255),
    renal VARCHAR(255),
    hepatica VARCHAR(255),
    pneumopatia VARCHAR(255),
    hematologica VARCHAR(255),
    neurologica VARCHAR(255),
    imunodepressao VARCHAR(255),
    sindrome_down VARCHAR(255)
);

CREATE TABLE vacinacao (
    id BIGSERIAL PRIMARY KEY,
    vacina_covid VARCHAR(255) NOT NULL,
    primeira_dose VARCHAR(255),
    segunda_dose VARCHAR(255),
    terceira_dose VARCHAR(255),
    vacina_influenza VARCHAR(255) NOT NULL
);

CREATE TABLE paciente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    cns VARCHAR(255),
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(255) NOT NULL,
    gestante VARCHAR(255) NOT NULL,
    puerpera VARCHAR(255) NOT NULL,
    raca VARCHAR(255),
    estado_civil VARCHAR(255),
    escolaridade VARCHAR(255),
    profissao VARCHAR(255),
    endereco_id BIGINT UNIQUE,
    contato_id BIGINT UNIQUE,
    vacinacao_id BIGINT UNIQUE,
    comorbidade_id BIGINT UNIQUE,
    CONSTRAINT fk_paciente_endereco FOREIGN KEY (endereco_id) REFERENCES endereco (id),
    CONSTRAINT fk_paciente_contato FOREIGN KEY (contato_id) REFERENCES contato (id),
    CONSTRAINT fk_paciente_vacinacao FOREIGN KEY (vacinacao_id) REFERENCES vacinacao (id),
    CONSTRAINT fk_paciente_comorbidade FOREIGN KEY (comorbidade_id) REFERENCES comorbidade (id)
);

CREATE TABLE atendimento (
    id BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    febre VARCHAR(255),
    tosse VARCHAR(255),
    dispneia VARCHAR(255),
    fadiga VARCHAR(255),
    dor_abdominal VARCHAR(255),
    dor_garganta VARCHAR(255),
    saturacao VARCHAR(255),
    desconforto_respiratorio VARCHAR(255),
    diarreia VARCHAR(255),
    vomito VARCHAR(255),
    perda_olfato VARCHAR(255),
    perda_paladar VARCHAR(255),
    internacao VARCHAR(255) NOT NULL,
    uti VARCHAR(255) NOT NULL,
    diasuti INTEGER,
    suporte_ventilatorio VARCHAR(255) NOT NULL,
    paciente_id BIGINT NOT NULL,
    CONSTRAINT fk_atendimento_paciente FOREIGN KEY (paciente_id) REFERENCES paciente (id)
);

CREATE TABLE predicao (
    id BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    payload_enviado TEXT,
    resposta_completa TEXT,
    resultado VARCHAR(255),
    tempo_processamento_ms BIGINT,
    atendimento_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_predicao_atendimento FOREIGN KEY (atendimento_id) REFERENCES atendimento (id)
);
