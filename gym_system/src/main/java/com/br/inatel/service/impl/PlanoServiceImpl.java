package com.br.inatel.service.impl;

import com.br.inatel.dao.PlanoDao;
import com.br.inatel.model.Plano;
import com.br.inatel.service.PlanoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class PlanoServiceImpl implements PlanoService {

    private final PlanoDao dao;

    public PlanoServiceImpl(PlanoDao dao) {
        this.dao = Objects.requireNonNull(dao, "dao não pode ser null");
    }

    // ===== CREATE =====
    @Override
    public Plano criar(String nome, BigDecimal preco, int duracaoMeses) throws BusinessException {
        nome = validarNome(nome);
        validarPreco(preco);
        validarDuracao(duracaoMeses);

        try {
            if (existePlanoComMesmoNome(nome)) {
                throw new BusinessException("Já existe um plano com o nome '" + nome + "'.");
            }

            Plano p = new Plano();
            p.setNome(nome);
            p.setPreco(preco);
            p.setDuracaoMeses(duracaoMeses);

            dao.insert(p);
            return p;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao criar plano: " + e.getMessage());
        }
    }

    // ===== UPDATE =====
    @Override
    public Plano atualizar(long id, String nome, BigDecimal preco, int duracaoMeses)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        nome = validarNome(nome);
        validarPreco(preco);
        validarDuracao(duracaoMeses);

        try {
            Plano existente = dao.buscarPorId(id);
            if (existente == null) throw new NotFoundException("Plano não encontrado: " + id);

            if (existePlanoComMesmoNomeParaOutroId(nome, id)) {
                throw new BusinessException("Já existe outro plano com o nome '" + nome + "'.");
            }

            existente.setNome(nome);
            existente.setPreco(preco);
            existente.setDuracaoMeses(duracaoMeses);

            boolean ok = dao.atualizar(existente);
            if (!ok) throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            return existente;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar plano: " + e.getMessage());
        }
    }

    // ===== READ =====
    @Override
    public Plano buscarPorId(long id) throws NotFoundException {
        validarIdPositivo(id);
        try {
            Plano p = dao.buscarPorId(id);
            if (p == null) throw new NotFoundException("Plano não encontrado: " + id);
            return p;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar plano: " + e.getMessage());
        }
    }

    @Override
    public List<Plano> listarTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar planos: " + e.getMessage());
        }
    }

    // ===== DELETE =====
    @Override
    public void excluir(long id) throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        try {
            if (!dao.existePorId(id)) throw new NotFoundException("Plano não encontrado: " + id);

            boolean ok = dao.deletarPorId(id);
            if (!ok) throw new BusinessException("Exclusão não aplicada (id=" + id + ").");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao excluir plano: " + e.getMessage());
        }
    }

    // ===== SEARCH =====
    @Override
    public List<Plano> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) return List.of();
        try {
            return dao.buscarPorNome(termo.trim());
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar plano por nome: " + e.getMessage());
        }
    }

    @Override
    public List<Plano> buscarPorFaixaPreco(BigDecimal minimo, BigDecimal maximo) {
        if (minimo == null || minimo.signum() < 0) minimo = BigDecimal.ZERO;
        if (maximo == null || maximo.signum() <= 0 || maximo.compareTo(minimo) < 0) {
            throw new BusinessException("Faixa de preço inválida.");
        }
        try {
            return dao.buscarPorFaixaPreco(minimo, maximo);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar por faixa de preço: " + e.getMessage());
        }
    }

    // ===== Helpers =====
    private void validarIdPositivo(long id) {
        if (id <= 0) throw new BusinessException("Id deve ser positivo.");
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) throw new BusinessException("Nome é obrigatório.");
        String n = nome.trim();
        if (n.length() < 2) throw new BusinessException("Nome deve ter ao menos 2 caracteres.");
        return n;
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.signum() <= 0) {
            throw new BusinessException("Preço deve ser positivo.");
        }
    }

    private void validarDuracao(int meses) {
        if (meses <= 0) throw new BusinessException("Duração (em meses) deve ser maior que 0.");
        if (meses > 24) {
            throw new BusinessException("Duração muito longa (máx. 60 meses).");
        }
    }

    private boolean existePlanoComMesmoNome(String nome) throws SQLException {
        return dao.buscarPorNome(nome).stream()
                .anyMatch(p -> p.getNome() != null && p.getNome().equalsIgnoreCase(nome));
    }

    private boolean existePlanoComMesmoNomeParaOutroId(String nome, long idAtual) throws SQLException {
        return dao.buscarPorNome(nome).stream()
                .anyMatch(p -> p.getNome() != null && p.getNome().equalsIgnoreCase(nome) && p.getId() != idAtual);
    }
}
