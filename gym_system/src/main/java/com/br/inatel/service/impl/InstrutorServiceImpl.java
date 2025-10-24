package com.br.inatel.service.impl;

import com.br.inatel.dao.InstrutorDao;
import com.br.inatel.model.Instrutor;
import com.br.inatel.service.InstrutorService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InstrutorServiceImpl implements InstrutorService {

    private final InstrutorDao dao;

    public InstrutorServiceImpl(InstrutorDao dao) {
        this.dao = Objects.requireNonNull(dao, "dao não pode ser null");
    }

    // ===== CREATE =====
    @Override
    public Instrutor criar(String nome, String cref) throws BusinessException {
        nome = validarNome(nome);
        cref = validarCref(cref);

        Instrutor i = new Instrutor();
        i.setNome(nome);
        i.setCref(cref);

        try {
            dao.insert(i);
            return i;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao criar instrutor: " + e.getMessage());
        }
    }

    // ===== UPDATE =====
    @Override
    public Instrutor atualizar(long id, String nome, String cref)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        nome = validarNome(nome);
        cref = validarCref(cref);

        try {
            Instrutor existente = dao.buscarPorId(id);
            if (existente == null) {
                throw new NotFoundException("Instrutor não encontrado: " + id);
            }

            existente.setNome(nome);
            existente.setCref(cref);

            boolean ok = dao.atualizar(existente);
            if (!ok) {
                throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            }
            return existente;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar instrutor: " + e.getMessage());
        }
    }

    // ===== READ =====
    @Override
    public List<Instrutor> listarTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar instrutores: " + e.getMessage());
        }
    }

    // ===== DELETE =====
    @Override
    public void excluir(long id) throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        try {
            if (!dao.existePorId(id)) {
                throw new NotFoundException("Instrutor não encontrado: " + id);
            }
            boolean ok = dao.deletarPorId(id);
            if (!ok) throw new BusinessException("Exclusão não aplicada (id=" + id + ").");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao excluir instrutor: " + e.getMessage());
        }
    }

    // ===== SEARCH =====
    @Override
    public List<Instrutor> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) return Collections.emptyList();
        try {
            return dao.buscarPorNome(termo.trim());
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar instrutor por nome: " + e.getMessage());
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

    private String validarCref(String cref) {
        if (cref == null || cref.isBlank()) throw new BusinessException("CREF é obrigatório.");
        String c = cref.trim();
        return c;
    }
}
