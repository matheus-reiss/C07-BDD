-- ======================================================================
-- GYM_MANAGER 
-- ======================================================================

CREATE SCHEMA IF NOT EXISTS gym_manager DEFAULT CHARACTER SET utf8;
USE gym_manager;

-- usuário da aplicação 
CREATE USER IF NOT EXISTS 'gm_app'@'localhost' IDENTIFIED BY '12345';
GRANT SELECT, INSERT, UPDATE, DELETE ON gym_manager.* TO 'gm_app'@'localhost';

-- ----------------------
-- Tabelas
-- ----------------------

-- Aluno
CREATE TABLE IF NOT EXISTS Aluno (
  idAluno INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nome VARCHAR(30) NOT NULL,
  nascimento DATE,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  telefone VARCHAR(30),
  PRIMARY KEY (idAluno)
) ENGINE=InnoDB;

-- Instrutor
CREATE TABLE IF NOT EXISTS Instrutor (
  idInstrutor INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nome VARCHAR(30) NOT NULL,
  cref VARCHAR(20) NOT NULL,
  PRIMARY KEY (idInstrutor),
  UNIQUE KEY uk_instrutor_cref (cref)
) ENGINE=InnoDB;

-- Plano
CREATE TABLE IF NOT EXISTS Plano (
  idPlano INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nome VARCHAR(20) NOT NULL,
  valor DECIMAL(10,2) NOT NULL,
  duracao INT UNSIGNED NOT NULL,
  PRIMARY KEY (idPlano),
  CHECK (valor > 0),
  CHECK (duracao > 0)
) ENGINE=InnoDB;

-- Assinatura
CREATE TABLE IF NOT EXISTS Assinatura (
  idAssinatura INT UNSIGNED NOT NULL AUTO_INCREMENT,
  data_inicio  DATE NOT NULL,
  data_fim DATE NOT NULL,
  status ENUM('ATIVA','INATIVA','CANCELADA','ATRASADA') NOT NULL DEFAULT 'ATIVA',
  Aluno_idAluno INT UNSIGNED NOT NULL,
  Plano_idPlano INT UNSIGNED NOT NULL,
  PRIMARY KEY (idAssinatura),
  CONSTRAINT fk_assinatura_aluno FOREIGN KEY (Aluno_idAluno)
    REFERENCES Aluno (idAluno)
    ON DELETE CASCADE,
  CONSTRAINT fk_assinatura_plano FOREIGN KEY (Plano_idPlano)
    REFERENCES Plano (idPlano),
  CHECK (data_fim >= data_inicio)
) ENGINE=InnoDB;

-- Pagamento
CREATE TABLE IF NOT EXISTS Pagamento (
  idPagamento INT UNSIGNED NOT NULL AUTO_INCREMENT,
  competencia DATE NOT NULL,
  valor DECIMAL(10,2) NOT NULL,
  data_vencimento DATE NOT NULL,
  status ENUM('PENDENTE','PAGO','ATRASADO','ESTORNADO') NOT NULL DEFAULT 'PENDENTE',
  data_pagamento DATE,
  Assinatura_idAssinatura INT UNSIGNED NOT NULL,
  PRIMARY KEY (idPagamento),
  CONSTRAINT fk_pag_assinatura FOREIGN KEY (Assinatura_idAssinatura)
    REFERENCES Assinatura (idAssinatura)
    ON DELETE CASCADE,
  CHECK (valor > 0)
) ENGINE=InnoDB;

-- Frequencia
CREATE TABLE IF NOT EXISTS Frequencia (
  idFrequencia INT UNSIGNED NOT NULL AUTO_INCREMENT,
  Aluno_idAluno INT UNSIGNED NOT NULL,
  data_checkin DATE NOT NULL,
  PRIMARY KEY (idFrequencia),
  CONSTRAINT fk_freq_aluno FOREIGN KEY (Aluno_idAluno)
    REFERENCES Aluno (idAluno)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Treino
CREATE TABLE IF NOT EXISTS Treino (
  idTreino INT UNSIGNED NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(30) NOT NULL,
  data_criacao DATE,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  Instrutor_idInstrutor INT UNSIGNED NOT NULL,
  Aluno_idAluno INT UNSIGNED NOT NULL,
  PRIMARY KEY (idTreino),
  CONSTRAINT fk_treino_instrutor FOREIGN KEY (Instrutor_idInstrutor)
    REFERENCES Instrutor (idInstrutor),
  CONSTRAINT fk_treino_aluno FOREIGN KEY (Aluno_idAluno)
    REFERENCES Aluno (idAluno)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Exercicio
CREATE TABLE IF NOT EXISTS Exercicio (
  idExercicio INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nome VARCHAR(30) NOT NULL,
  grupo_muscular VARCHAR(30) NOT NULL,
  PRIMARY KEY (idExercicio)
) ENGINE=InnoDB;

-- TreinoExercicio
CREATE TABLE IF NOT EXISTS TreinoExercicio (
  Treino_idTreino INT UNSIGNED NOT NULL,
  Exercicio_idExercicio INT UNSIGNED NOT NULL,
  ordem TINYINT UNSIGNED NOT NULL,
  series TINYINT UNSIGNED NOT NULL,
  repeticoes TINYINT UNSIGNED NOT NULL,
  carga_kg SMALLINT UNSIGNED,
  descanso_seg SMALLINT UNSIGNED NOT NULL DEFAULT 60,
  PRIMARY KEY (Treino_idTreino, Exercicio_idExercicio),
  CONSTRAINT fk_tx_treino FOREIGN KEY (Treino_idTreino)
    REFERENCES Treino (idTreino)
    ON DELETE CASCADE,
  CONSTRAINT fk_tx_exercicio FOREIGN KEY (Exercicio_idExercicio)
    REFERENCES Exercicio (idExercicio),
  CHECK (descanso_seg BETWEEN 0 AND 300)
) ENGINE=InnoDB;

-- ----------------------
-- Restrições e Índices
-- ----------------------
ALTER TABLE Frequencia 
  ADD UNIQUE KEY uk_freq_aluno_dia (Aluno_idAluno, data_checkin);

ALTER TABLE Pagamento 
  ADD UNIQUE KEY uk_pag_ass_comp (Assinatura_idAssinatura, competencia);

ALTER TABLE TreinoExercicio 
  ADD UNIQUE KEY uk_tx_treino_ordem (Treino_idTreino, ordem);

ALTER TABLE TreinoExercicio 
  ADD CONSTRAINT chk_tx_ordem_pos  CHECK (ordem >= 1),
  ADD CONSTRAINT chk_tx_series_pos CHECK (series >= 1),
  ADD CONSTRAINT chk_tx_reps_pos   CHECK (repeticoes >= 1);

-- Assinatura
CREATE INDEX idx_ass_aluno   ON Assinatura (Aluno_idAluno);
CREATE INDEX idx_ass_plano   ON Assinatura (Plano_idPlano);
CREATE INDEX idx_ass_status  ON Assinatura (status);
CREATE INDEX idx_ass_inicio  ON Assinatura (data_inicio);

-- Pagamento
CREATE INDEX idx_pag_assin   ON Pagamento (Assinatura_idAssinatura);
CREATE INDEX idx_pag_status  ON Pagamento (status);
CREATE INDEX idx_pag_venc    ON Pagamento (data_vencimento);
CREATE INDEX idx_pag_comp    ON Pagamento (competencia);

-- Frequencia
CREATE INDEX idx_freq_aluno  ON Frequencia (Aluno_idAluno);
CREATE INDEX idx_freq_data   ON Frequencia (data_checkin);

-- Treino
CREATE INDEX idx_treino_ativo     ON Treino (ativo);
CREATE INDEX idx_treino_instrutor ON Treino (Instrutor_idInstrutor);
CREATE INDEX idx_treino_aluno     ON Treino (Aluno_idAluno);
CREATE INDEX idx_treino_data      ON Treino (data_criacao);

-- Buscas por nome
CREATE INDEX idx_exercicio_nome   ON Exercicio (nome);
CREATE INDEX idx_exercicio_grupo  ON Exercicio (grupo_muscular);
CREATE INDEX idx_instrutor_nome   ON Instrutor (nome);
CREATE INDEX idx_plano_nome       ON Plano (nome);

-- ===============
-- SEED DE DADOS 
-- ===============
START TRANSACTION; -- Inicia bloco de operações

-- Instrutores
INSERT INTO Instrutor (nome, cref) VALUES
  ('Carla Ramos', 'CREF12345/MG'),
  ('Diego Alves', 'CREF67890/MG'),
  ('Bianca Melo', 'CREF11223/MG');

SET @idInstr_Carla = (SELECT idInstrutor FROM Instrutor WHERE cref = 'CREF12345/MG');
SET @idInstr_Diego = (SELECT idInstrutor FROM Instrutor WHERE cref = 'CREF67890/MG');
SET @idInstr_Bianca = (SELECT idInstrutor FROM Instrutor WHERE cref = 'CREF11223/MG');

-- Planos
INSERT INTO Plano (nome, valor, duracao) VALUES
  ('Básico',      99.90,  30),
  ('Mensal Plus', 129.90, 30),
  ('Trimestral',  299.90, 90),
  ('Anual',       999.00, 365);

SET @idPlano_Basico     = (SELECT idPlano FROM Plano WHERE nome = 'Básico');
SET @idPlano_MensalPlus = (SELECT idPlano FROM Plano WHERE nome = 'Mensal Plus');
SET @idPlano_Trim       = (SELECT idPlano FROM Plano WHERE nome = 'Trimestral');
SET @idPlano_Anual      = (SELECT idPlano FROM Plano WHERE nome = 'Anual');

-- Alunos
INSERT INTO Aluno (nome, nascimento, ativo, telefone) VALUES
  ('João Silva',  '2002-03-11', 1, '(35) 99999-1111'),
  ('Maria Souza', '1998-07-22', 1, '(35) 98888-2222'),
  ('Pedro Lima',  '2005-12-05', 1, '(35) 97777-3333');

SET @idAluno_Joao  = (SELECT idAluno FROM Aluno WHERE nome = 'João Silva');
SET @idAluno_Maria = (SELECT idAluno FROM Aluno WHERE nome = 'Maria Souza');
SET @idAluno_Pedro = (SELECT idAluno FROM Aluno WHERE nome = 'Pedro Lima');

-- Assinaturas
INSERT INTO Assinatura (data_inicio, data_fim, status, Aluno_idAluno, Plano_idPlano)
VALUES ('2025-10-01', '2025-10-31', 'ATIVA', @idAluno_Joao, @idPlano_MensalPlus);
SET @idAss_Joao = LAST_INSERT_ID();

INSERT INTO Assinatura (data_inicio, data_fim, status, Aluno_idAluno, Plano_idPlano)
VALUES ('2025-08-15', '2025-11-12', 'ATRASADA', @idAluno_Maria, @idPlano_Trim);
SET @idAss_Maria = LAST_INSERT_ID();

INSERT INTO Assinatura (data_inicio, data_fim, status, Aluno_idAluno, Plano_idPlano)
VALUES ('2025-06-01', '2025-06-30', 'INATIVA', @idAluno_Pedro, @idPlano_Basico);
SET @idAss_Pedro = LAST_INSERT_ID();

-- Pagamentos
INSERT INTO Pagamento (competencia, valor, data_vencimento, status, data_pagamento, Assinatura_idAssinatura)
VALUES ('2025-10-01', 129.90, '2025-10-10', 'PENDENTE', NULL, @idAss_Joao);

INSERT INTO Pagamento (competencia, valor, data_vencimento, status, data_pagamento, Assinatura_idAssinatura) VALUES
  ('2025-08-01', 299.90, '2025-08-10', 'PAGO',     '2025-08-16', @idAss_Maria),
  ('2025-09-01', 299.90, '2025-09-10', 'ATRASADO', NULL,         @idAss_Maria),
  ('2025-10-01', 299.90, '2025-10-10', 'PENDENTE', NULL,         @idAss_Maria);

INSERT INTO Pagamento (competencia, valor, data_vencimento, status, data_pagamento, Assinatura_idAssinatura)
VALUES ('2025-06-01', 99.90, '2025-06-10', 'ESTORNADO', NULL, @idAss_Pedro);

-- Frequência (últimos dias)
INSERT INTO Frequencia (Aluno_idAluno, data_checkin) VALUES
  (@idAluno_Joao,  '2025-10-18'),
  (@idAluno_Joao,  '2025-10-17'),
  (@idAluno_Joao,  '2025-10-15'),
  (@idAluno_Maria, '2025-10-16'),
  (@idAluno_Maria, '2025-10-14'),
  (@idAluno_Pedro, '2025-06-05');  -- histórico

-- Exercícios
INSERT INTO Exercicio (nome, grupo_muscular) VALUES
  ('Supino Reto',        'Peito'),
  ('Agachamento Livre',  'Pernas'),
  ('Levantamento Terra', 'Posterior/Costas'),
  ('Barra Fixa',         'Costas'),
  ('Prancha',            'Core');

SET @ex_Supino  = (SELECT idExercicio FROM Exercicio WHERE nome = 'Supino Reto');
SET @ex_Agacha  = (SELECT idExercicio FROM Exercicio WHERE nome = 'Agachamento Livre');
SET @ex_Terra   = (SELECT idExercicio FROM Exercicio WHERE nome = 'Levantamento Terra');
SET @ex_Barra   = (SELECT idExercicio FROM Exercicio WHERE nome = 'Barra Fixa');
SET @ex_Prancha = (SELECT idExercicio FROM Exercicio WHERE nome = 'Prancha');

-- Treinos
INSERT INTO Treino (titulo, data_criacao, ativo, Instrutor_idInstrutor, Aluno_idAluno)
VALUES ('Full Body A', '2025-10-05', 1, @idInstr_Carla, @idAluno_Joao);
SET @idTreino_Joao = LAST_INSERT_ID();

INSERT INTO Treino (titulo, data_criacao, ativo, Instrutor_idInstrutor, Aluno_idAluno)
VALUES ('Costas/Core', '2025-08-20', 1, @idInstr_Diego, @idAluno_Maria);
SET @idTreino_Maria = LAST_INSERT_ID();

INSERT INTO Treino (titulo, data_criacao, ativo, Instrutor_idInstrutor, Aluno_idAluno)
VALUES ('Básico Início', '2025-06-03', 0, @idInstr_Carla, @idAluno_Pedro);
SET @idTreino_Pedro = LAST_INSERT_ID();

-- Itens do treino 
INSERT INTO TreinoExercicio (Treino_idTreino, Exercicio_idExercicio, ordem, series, repeticoes, carga_kg, descanso_seg) VALUES
  (@idTreino_Joao, @ex_Supino,  1, 4,  8,  60,  90),
  (@idTreino_Joao, @ex_Agacha,  2, 4,  6,  80, 120),
  (@idTreino_Joao, @ex_Terra,   3, 3,  5, 100, 120),
  (@idTreino_Joao, @ex_Prancha, 4, 3, 45, NULL,  60); -- prancha: reps=segundos

INSERT INTO TreinoExercicio (Treino_idTreino, Exercicio_idExercicio, ordem, series, repeticoes, carga_kg, descanso_seg) VALUES
  (@idTreino_Maria, @ex_Barra,   1, 4,  6, NULL,  90),
  (@idTreino_Maria, @ex_Terra,   2, 3,  5,   70, 120),
  (@idTreino_Maria, @ex_Prancha, 3, 3, 60, NULL,  60);

COMMIT; -- Confirma as operações


-- UPDATE: marcar pagamentos pendentes e vencidos como atrasados
UPDATE Pagamento
   SET status = 'ATRASADO'
 WHERE status = 'PENDENTE'
   AND data_vencimento < CURRENT_DATE;

-- DELETE: remover treinos inativos
DELETE FROM Treino
 WHERE ativo = 0;


CREATE OR REPLACE VIEW vw_pagamentos_em_aberto AS
SELECT 
    al.idAluno,
    al.nome         AS aluno,
    a.idAssinatura  AS id_assinatura,
    pg.idPagamento  AS id_pagamento,
    pg.competencia,
    pg.valor,
    pg.data_vencimento,
    pg.status
FROM Pagamento pg
JOIN Assinatura a ON a.idAssinatura = pg.Assinatura_idAssinatura
JOIN Aluno al      ON al.idAluno = a.Aluno_idAluno
WHERE pg.status IN ('PENDENTE', 'ATRASADO')
   OR (pg.status <> 'PAGO' AND pg.data_vencimento < CURRENT_DATE);
   
   
-- FUNCTION
DROP FUNCTION IF EXISTS gym_manager.fn_status_pagamento;
DELIMITER $$
CREATE FUNCTION gym_manager.fn_status_pagamento(p_vencimento DATE, p_pago_em DATE)
RETURNS VARCHAR(10)
DETERMINISTIC
NO SQL
BEGIN
    DECLARE v_status VARCHAR(10);
    IF p_pago_em IS NOT NULL THEN
        SET v_status = 'PAGO';
    ELSEIF p_vencimento < CURRENT_DATE THEN
        SET v_status = 'ATRASADO';
    ELSE
        SET v_status = 'PENDENTE';
    END IF;
    RETURN v_status;
END$$
DELIMITER ;

-- PROCEDURE
DROP PROCEDURE IF EXISTS gym_manager.sp_registrar_pagamento;
DELIMITER $$
CREATE PROCEDURE gym_manager.sp_registrar_pagamento(
    IN p_id_pagamento INT UNSIGNED,
    IN p_data_pagamento DATE
)
MODIFIES SQL DATA
BEGIN
    IF p_id_pagamento IS NULL OR p_id_pagamento = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'p_id_pagamento inválido';
    END IF;

    UPDATE Pagamento
       SET status = 'PAGO',
           data_pagamento = p_data_pagamento
     WHERE idPagamento = p_id_pagamento;
END$$
DELIMITER ;

GRANT EXECUTE ON FUNCTION gym_manager.fn_status_pagamento TO 'gm_app'@'localhost';
GRANT EXECUTE ON PROCEDURE gym_manager.sp_registrar_pagamento TO 'gm_app'@'localhost';


SET @idAss_Joao := (SELECT idAssinatura FROM Assinatura a 
                    JOIN Aluno al ON al.idAluno = a.Aluno_idAluno
                    WHERE al.nome = 'João Silva' 
                    ORDER BY a.data_inicio DESC LIMIT 1);

SET @idPag_JoaoPend := (
  SELECT pg.idPagamento
  FROM Pagamento pg
  WHERE pg.Assinatura_idAssinatura = @idAss_Joao
    AND pg.status IN ('PENDENTE','ATRASADO')
  ORDER BY pg.data_vencimento ASC
  LIMIT 1
);

SET @hoje := CURRENT_DATE;
CALL sp_registrar_pagamento(@idPag_JoaoPend, @hoje);



-- ===============
-- Consultas-Teste 
-- ===============

--  Últimos check-ins (nome + dia)
SELECT a.nome, DATE_FORMAT(f.data_checkin, '%d/%m/%Y') AS data_checkin
FROM Frequencia f
JOIN Aluno a ON a.idAluno = f.Aluno_idAluno
ORDER BY f.data_checkin DESC;

-- Assinaturas e status por aluno
SELECT al.nome AS aluno, p.nome AS plano, a.status, DATE_FORMAT(a.data_inicio, '%d/%m/%Y') AS data_inicio , 
DATE_FORMAT(a.data_fim, '%d/%m/%Y') AS data_fim
FROM Assinatura a
JOIN Aluno al ON al.idAluno = a.Aluno_idAluno
JOIN Plano p  ON p.idPlano = a.Plano_idPlano
ORDER BY al.nome, a.data_inicio DESC;

--  Pagamentos
SELECT al.nome AS aluno, DATE_FORMAT(pg.competencia, '%d/%m/%Y') AS competencia, pg.valor,
 DATE_FORMAT(pg.data_vencimento, '%d/%m/%Y') AS data_vencimento, pg.status, DATE_FORMAT(pg.data_pagamento, '%d/%m/%Y')
FROM Pagamento pg
JOIN Assinatura a ON a.idAssinatura = pg.Assinatura_idAssinatura
JOIN Aluno al ON al.idAluno = a.Aluno_idAluno
ORDER BY pg.competencia DESC, al.nome;

--  Treinos + itens em ordem
SELECT al.nome AS aluno, t.titulo, DATE_FORMAT(t.data_criacao, '%d/%m/%Y') AS data_criacao, t.ativo,
       tx.ordem, e.nome AS exercicio, tx.series, tx.repeticoes, tx.carga_kg
FROM Treino t
JOIN Aluno al ON al.idAluno = t.Aluno_idAluno
JOIN TreinoExercicio tx ON tx.Treino_idTreino = t.idTreino
JOIN Exercicio e ON e.idExercicio = tx.Exercicio_idExercicio
ORDER BY al.nome, t.data_criacao DESC, tx.ordem ASC;

--  Inadimplência 
SELECT al.nome AS aluno, DATE_FORMAT(pg.competencia, '%d/%m/%Y') AS competencia,
 DATE_FORMAT(pg.data_vencimento, '%d/%m/%Y') AS data_vencimento, pg.status
FROM Pagamento pg
JOIN Assinatura a ON a.idAssinatura = pg.Assinatura_idAssinatura
JOIN Aluno al ON al.idAluno = a.Aluno_idAluno
WHERE (pg.status = 'ATRASADO')
   OR (pg.status = 'PENDENTE' AND pg.data_vencimento < CURRENT_DATE)
ORDER BY pg.data_vencimento ASC;

-- VIEW demonstração
SELECT * 
FROM vw_pagamentos_em_aberto
ORDER BY aluno, data_vencimento;

-- Comparação status calculado x status gravado
SELECT 
  al.nome AS aluno,
  DATE_FORMAT(pg.competencia,'%d/%m/%Y') AS competencia,
  DATE_FORMAT(pg.data_vencimento,'%d/%m/%Y') AS vencimento,
  pg.status AS status_coluna,
  fn_status_pagamento(pg.data_vencimento, pg.data_pagamento) AS status_calc
FROM Pagamento pg
JOIN Assinatura a ON a.idAssinatura = pg.Assinatura_idAssinatura
JOIN Aluno al      ON al.idAluno = a.Aluno_idAluno
ORDER BY al.nome, pg.data_vencimento;
