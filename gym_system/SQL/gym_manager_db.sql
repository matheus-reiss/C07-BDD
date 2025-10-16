CREATE SCHEMA IF NOT EXISTS gym_manager DEFAULT CHARACTER SET utf8;
USE gym_manager;

CREATE USER IF NOT EXISTS 'gm_app'@'localhost' IDENTIFIED BY '12345';
GRANT SELECT, INSERT ON gym_manager.* TO 'gm_app'@'localhost';

-- ----------------------
-- Table Aluno
-- ----------------------

CREATE TABLE IF NOT EXISTS Aluno (
	idAluno INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(30) NOT NULL,
    nascimento DATE,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    telefone VARCHAR(30),
    PRIMARY KEY (idAluno)
);

-- -----------------
-- Table Frequencia
-- -----------------
CREATE TABLE IF NOT EXISTS Frequencia (
	idFrequencia INT NOT NULL,
    Aluno_idAluno INT NOT NULL,
    data_checkin DATE,
    PRIMARY KEY (idFrequencia),
    FOREIGN KEY (Aluno_idAluno)
    REFERENCES Aluno (idAluno)
);

-- -----------------
-- Table Assinatura
-- -----------------
CREATE TABLE IF NOT EXISTS Assinatura (
	idAssinatura INT NOT NULL,
    data_inicio  DATE NOT NULL,
    data_fim DATE NOT NULL,
    status ENUM('ATIVA','INATIVA','CANCELADA','ATRASADA') NOT NULL DEFAULT 'ATIVA',
    Aluno_idAluno INT NOT NULL,
    Plano_idPlano INT NOT NULL,
	PRIMARY KEY (idAssinatura),
    FOREIGN KEY (Aluno_idAluno)
	REFERENCES Aluno (idAluno),
    FOREIGN KEY (Plano_idPlano)
    REFERENCES Plano (idPlano)
);

-- -----------------
-- Table Plano
-- -----------------
CREATE TABLE IF NOT EXISTS Plano (
	idPlano INT NOT NULL,
    nome VARCHAR(20) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    duracao INT NOT NULL,
    PRIMARY KEY (idPlano)
);

-- -----------------
-- Table Pagamento
-- -----------------
CREATE TABLE IF NOT EXISTS Pagamento (
	idPagamento INT NOT NULL,
    competencia DATE NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    data_pagamento DATE,
    Assinatura_idAssinatura INT NOT NULL,
    PRIMARY KEY (idPagamento),
    FOREIGN KEY (Assinatura_idAssinatura)
    REFERENCES Assinatura (idAssinatura)
);

-- -----------------
-- Table Treino
-- -----------------
CREATE TABLE IF NOT EXISTS Treino (
	idTreino INT NOT NULL,
    titulo VARCHAR(30) NOT NULL,
    data_criacao DATE,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    Instrutor_idInstrutor INT NOT NULL,
    Aluno_idAluno INT NOT NULL,
    PRIMARY KEY (idTreino),
    FOREIGN KEY (Instrutor_idInstrutor)
    REFERENCES Instrutor (idInstrutor),
    FOREIGN KEY (Aluno_idAluno)
    REFERENCES Aluno (idAluno)
);

-- ---------------------
-- Table TreinoExercicio
-- ---------------------
CREATE TABLE IF NOT EXISTS TreinoExercicio (
	Treino_idTreino INT NOT NULL,
    Exercicio_idExercicio INT NOT NULL,
    ordem INT NOT NULL,
    series INT NOT NULL,
    repeticoes INT NOT NULL,
    carga_kg INT,
    descanso_seg SMALLINT UNSIGNED NOT NULL DEFAULT 60,
    PRIMARY KEY (Treino_idTreino, Exercicio_idExercicio),
    FOREIGN KEY (Treino_idTreino)
    REFERENCES Treino (idTreino),
    FOREIGN KEY (Exercicio_idExercicio)
    REFERENCES Exercicio (idExercicio)
);

-- ---------------------
-- Table Exercicio
-- ---------------------
CREATE TABLE IF NOT EXISTS Exercicio (
	idExercicio INT NOT NULL,
    nome VARCHAR(30) NOT NULL,
    grupo_muscular VARCHAR(30) NOT NULL,
    PRIMARY KEY (idExercicio)
);

-- ---------------------
-- Table Instrutor
-- ---------------------
CREATE TABLE IF NOT EXISTS Instrutor (
	idInstrutor INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(30) NOT NULL,
    cref VARCHAR(20) NOT NULL,
    PRIMARY KEY (idInstrutor)
);