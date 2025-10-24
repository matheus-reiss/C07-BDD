package com.br.inatel.service.impl;

import com.br.inatel.dao.AlunoDao;
import com.br.inatel.model.Aluno;
import com.br.inatel.service.AlunoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AlunoServiceImpl implements AlunoService {

    private final AlunoDao dao;

    public AlunoServiceImpl(AlunoDao dao) {
        this.dao = Objects.requireNonNull(dao, "dao não pode ser null");
    }

    // ====== CREATE ======
    @Override
    public Aluno criar(String nome, LocalDate nascimento, String telefone) throws BusinessException {
        validarNome(nome);
        validarNascimento(nascimento);

        Aluno aluno = new Aluno();
        aluno.setNome(nome.trim());
        aluno.setDataNascimento(nascimento);
        aluno.setTelefone(telefone != null ? telefone.trim() : null);
        aluno.setAtivo(true); // regra: entra ativo por padrão

        try {
            dao.insert(aluno);
            return aluno;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao criar aluno: " + e.getMessage());
        }
    }

    // ====== UPDATE ======
    @Override
    public Aluno atualizar(long id, String nome, LocalDate nascimento, String telefone)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        validarNome(nome);
        validarNascimento(nascimento);

        try {
            Aluno existente = dao.buscarPorId(id);
            if (existente == null) {
                throw new NotFoundException("Aluno não encontrado: " + id);
            }

            existente.setNome(nome.trim());
            existente.setDataNascimento(nascimento);
            existente.setTelefone(telefone != null ? telefone.trim() : null);

            boolean ok = dao.atualizar(existente);
            if (!ok) {
                // Em teoria não deve acontecer, pois já validamos a existência
                throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            }
            return existente;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar aluno: " + e.getMessage());
        }
    }

    // ====== PATCH: ATIVAR/DESATIVAR ======
    @Override
    public void ativar(long id) throws NotFoundException {
        alterarAtivo(id, true);
    }

    @Override
    public void desativar(long id) throws NotFoundException {
        alterarAtivo(id, false);
    }

    private void alterarAtivo(long id, boolean ativo) throws NotFoundException {
        validarIdPositivo(id);
        try {
            if (!dao.existePorId(id)) {
                throw new NotFoundException("Aluno não encontrado: " + id);
            }
            boolean ok = dao.alterarAtivo(id, ativo);
            if (!ok) {
                throw new BusinessException("Não foi possível " + (ativo ? "ativar" : "desativar") + " o aluno (id=" + id + ").");
            }
        } catch (SQLException e) {
            throw new BusinessException("Falha ao alterar status do aluno: " + e.getMessage());
        }
    }

    // ====== READ ======
    @Override
    public Aluno buscarPorId(long id) throws NotFoundException {
        validarIdPositivo(id);
        try {
            Aluno a = dao.buscarPorId(id);
            if (a == null) {
                throw new NotFoundException("Aluno não encontrado: " + id);
            }
            return a;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar aluno: " + e.getMessage());
        }
    }

    @Override
    public List<Aluno> listarTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar alunos: " + e.getMessage());
        }
    }


    // ====== SEARCH ======
    @Override
    public List<Aluno> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return dao.buscarPorNome(termo.trim());
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar alunos por nome: " + e.getMessage());
        }
    }

    // ====== validações ======
    private void validarIdPositivo(long id) {
        if (id <= 0) throw new BusinessException("Id deve ser positivo.");
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BusinessException("Nome é obrigatório.");
        }
        if (nome.trim().length() < 2) {
            throw new BusinessException("Nome deve ter ao menos 2 caracteres.");
        }
    }

    private void validarNascimento(LocalDate nascimento) {
        if (nascimento == null) return;
        if (nascimento.isAfter(LocalDate.now())) {
            throw new BusinessException("Data de nascimento não pode ser no futuro.");
        }
    }
}
