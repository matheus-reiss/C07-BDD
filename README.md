
# 🏋️‍♂️ Sistema de Gestão de Academia

## 📌 Ideia

O projeto tem como objetivo o desenvolvimento de um sistema de banco de dados para gerenciar uma academia.
O sistema foi modelado para controlar alunos, planos de assinatura, pagamentos, treinos, instrutores, exercícios e a frequência de comparecimento dos alunos.

Funcionalidades:

- Cadastrar alunos e instrutores.
- Gerenciar planos e assinaturas ativas ou expiradas.
- Registrar pagamentos de mensalidades e controlar inadimplência.
- Criar treinos personalizados por instrutor, compostos por exercícios.
- Controlar as idas dos alunos à academia por meio de registros de frequência.


## 🎯 Escopo do Projeto
O sistema abrange as seguintes funcionalidades principais:
- Aluno: cadastro com informações básicas e status ativo/inativo.
- Instrutor: cadastro com CREF obrigatório.
- Plano: planos de diferentes durações e valores (diário, mensal, trimestral, anual, etc).
- Assinatura: vínculo entre aluno e plano, com datas de início/fim e status (ativa, cancelada, expirada, suspensa).
- Pagamento: controle das mensalidades por competência (mês/ano de referência), com data de vencimento, data de pagamento e status (aberto, pago, atrasado).
- Treino: plano de exercícios criado por um instrutor para um aluno, com título, objetivo, data de criação e status ativo/inativo.
- Exercício: lista de exercícios com o grupo muscular trabalhado.
- TreinoExercicio: tabela associativa que representa quais exercícios fazem parte de cada treino, incluindo ordem, séries, repetições, carga e descanso.
- Frequência: registros de check-in de alunos, permitindo acompanhar a frequência dos alunos.


## 🧩 Entidades e Atributos Principais

- Aluno: idAluno, nome, nascimento, telefone, ativo.
- Instrutor: idInstrutor, nome, cref.
- Plano: idPlano, nome, duracao_meses, valor.
- Assinatura: idAssinatura, aluno_id, plano_id, data_inicio, data_fim, status.
- Pagamento: idPagamento, assinatura_id, competencia, valor, data_vencimento, data_pagamento, status.
- Treino: idTreino, aluno_id, instrutor_id, titulo, data_criacao, ativo.
- Exercicio: idExercicio, nome, grupo_muscular.
- TreinoExercicio: treino_id, exercicio_id, ordem, series, repeticoes, carga_kg, descanso_seg.
- Frequencia: idFrequencia, aluno_id, data_checkin



##  🔗 Relacionamentos entre as Entidades
- __Aluno 1:N Assinatura__ — Um aluno pode ter várias assinaturas ao longo do tempo, mas apenas uma ativa por vez.     
- __Plano 1:N Assinatura__ — Cada plano pode ser contratado por vários alunos.
- __Assinatura 1:N Pagamento__ — Cada assinatura gera pagamentos mensais (ou conforme o plano definido).
- __Aluno 1:N Treino__ — Cada aluno pode ter diversos treinos ao longo do tempo.
- __Instrutor 1:N Treino__ — Um instrutor pode montar treinos para vários alunos.
- __Treino N:N Exercício (via TreinoExercicio)__ — Um treino é composto por vários exercícios, e um mesmo exercício pode estar presente em diferentes treinos.                A tabela **TreinoExercicio** guarda atributos adicionais (ordem, séries, repetições, carga e descanso).
- __Aluno 1:N Frequencia__ — Cada check-in realizado na academia é vinculado a um aluno.