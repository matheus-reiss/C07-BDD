package com.br.inatel.service.impl;

import com.br.inatel.dao.ExercicioDao;
import com.br.inatel.model.Exercicio;
import com.br.inatel.service.ExercicioService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ExercicioServiceImpl implements ExercicioService {

    private final ExercicioDao dao;

    public ExercicioServiceImpl(ExercicioDao dao) {
        this.dao = Objects.requireNonNull(dao, "dao não pode ser null");
    }

    // CREATE
    @Override
    public Exercicio criar(String nome, String grupoMuscular) throws BusinessException {
        nome = validarNome(nome);
        grupoMuscular = validarGrupo(grupoMuscular);

        Exercicio e = new Exercicio();
        e.setNome(nome);
        e.setGrupoMuscular(grupoMuscular);

        try {
            dao.insert(e);
            return e;
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao criar exercício: " + ex.getMessage());
        }
    }

    // UPDATE
    @Override
    public Exercicio atualizar(long id, String nome, String grupoMuscular)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        nome = validarNome(nome);
        grupoMuscular = validarGrupo(grupoMuscular);

        try {
            Exercicio existente = dao.buscarPorId(id);
            if (existente == null) {
                throw new NotFoundException("Exercício não encontrado: " + id);
            }

            existente.setNome(nome);
            existente.setGrupoMuscular(grupoMuscular);

            boolean ok = dao.atualizar(existente);
            if (!ok) {
                throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            }
            return existente;
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao atualizar exercício: " + ex.getMessage());
        }
    }

    // READ
    @Override
    public Exercicio buscarPorId(long id) throws NotFoundException {
        validarIdPositivo(id);
        try {
            Exercicio e = dao.buscarPorId(id);
            if (e == null) throw new NotFoundException("Exercício não encontrado: " + id);
            return e;
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao buscar exercício: " + ex.getMessage());
        }
    }

    @Override
    public List<Exercicio> listarTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao listar exercícios: " + ex.getMessage());
        }
    }

    // DELETE
    @Override
    public void excluir(long id) throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        try {
            if (!dao.existePorId(id)) {
                throw new NotFoundException("Exercício não encontrado: " + id);
            }
            boolean ok = dao.deletarPorId(id);
            if (!ok) throw new BusinessException("Exclusão não aplicada (id=" + id + ").");
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao excluir exercício: " + ex.getMessage());
        }
    }


    @Override
    public boolean possuiVinculos(long id) {
        validarIdPositivo(id);
        try {
            return dao.possuiVinculos(id);
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao verificar vínculos do exercício: " + ex.getMessage());
        }
    }

    // SEARCH
    @Override
    public List<Exercicio> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) return Collections.emptyList();
        try {
            return dao.buscarPorNome(termo.trim());
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao buscar por nome: " + ex.getMessage());
        }
    }

    @Override
    public List<Exercicio> buscarPorGrupoMuscular(String grupo) {
        if (grupo == null || grupo.isBlank()) return Collections.emptyList();
        try {
            return dao.buscarPorGrupoMuscular(grupo.trim());
        } catch (SQLException ex) {
            throw new BusinessException("Falha ao buscar por grupo muscular: " + ex.getMessage());
        }
    }

    // ===== validações =====
    private void validarIdPositivo(long id) {
        if (id <= 0) throw new BusinessException("Id deve ser positivo.");
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) throw new BusinessException("Nome é obrigatório.");
        String n = nome.trim();
        if (n.length() < 2) throw new BusinessException("Nome deve ter ao menos 2 caracteres.");
        return n;
    }

    private String validarGrupo(String grupo) {
        if (grupo == null || grupo.isBlank()) throw new BusinessException("Grupo muscular é obrigatório.");
        return grupo.trim();
    }
}
