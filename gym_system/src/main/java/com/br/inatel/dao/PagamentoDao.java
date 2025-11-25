package com.br.inatel.dao;

import com.br.inatel.model.Assinatura;
import com.br.inatel.model.Pagamento;
import com.br.inatel.model.enums.PagamentoStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDao {

    private final Connection conn;

    public PagamentoDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Pagamento p) throws SQLException {
        String sql = """
            INSERT INTO Pagamento (competencia, valor, data_vencimento, status, data_pagamento, Assinatura_idAssinatura)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, toSqlDate(p.getCompetencia()));
            ps.setBigDecimal(2, p.getValor());
            ps.setDate(3, toSqlDate(p.getDataVencimento()));
            ps.setString(4, p.getStatus().name());
            ps.setDate(5, toSqlDate(p.getDataPagamento()));
            ps.setLong(6, p.getAssinatura().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }
        }
    }

    // ===== UPDATE =====
    public boolean atualizar(Pagamento p) throws SQLException {
        String sql = """
            UPDATE Pagamento
               SET competencia = ?, valor = ?, data_vencimento = ?, status = ?, data_pagamento = ?, Assinatura_idAssinatura = ?
             WHERE idPagamento = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, toSqlDate(p.getCompetencia()));
            ps.setBigDecimal(2, p.getValor());
            ps.setDate(3, toSqlDate(p.getDataVencimento()));
            ps.setString(4, p.getStatus().name());
            ps.setDate(5, toSqlDate(p.getDataPagamento()));
            ps.setLong(6, p.getAssinatura().getId());
            ps.setLong(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ (by ID) =====
    public Pagamento buscarPorId(long id) throws SQLException {
        String sql = """
            SELECT idPagamento, Assinatura_idAssinatura, competencia, valor, data_vencimento, status, data_pagamento
              FROM Pagamento
             WHERE idPagamento = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    // ===== READ (all) =====
    public List<Pagamento> listarTodos() throws SQLException {
        String sql = """
            SELECT idPagamento, Assinatura_idAssinatura, competencia, valor, data_vencimento, status, data_pagamento
              FROM Pagamento
             ORDER BY competencia DESC, data_vencimento DESC, idPagamento DESC
        """;
        List<Pagamento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    // ===== DELETE =====
    public boolean deletarPorId(long id) throws SQLException {
        String sql = "DELETE FROM Pagamento WHERE idPagamento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ (por assinatura) =====
    public List<Pagamento> listarPorAssinatura(long idAssinatura) throws SQLException {
        String sql = """
            SELECT idPagamento, competencia, valor, data_vencimento, status, data_pagamento
              FROM Pagamento
             WHERE Assinatura_idAssinatura = ?
             ORDER BY competencia DESC, idPagamento DESC
        """;
        List<Pagamento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAssinatura);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pagamento p = new Pagamento();
                    p.setId(rs.getLong("idPagamento"));
                    p.setCompetencia(toLocalDate(rs.getDate("competencia")));
                    p.setValor(rs.getBigDecimal("valor"));
                    p.setDataVencimento(toLocalDate(rs.getDate("data_vencimento")));
                    p.setStatus(PagamentoStatus.valueOf(rs.getString("status")));
                    p.setDataPagamento(toLocalDate(rs.getDate("data_pagamento")));
                    Assinatura a = new Assinatura();
                    a.setId(idAssinatura);
                    p.setAssinatura(a);
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    // ===== READ (por status) =====
    public List<Pagamento> listarPorStatus(PagamentoStatus status) throws SQLException {
        String sql = """
            SELECT idPagamento, Assinatura_idAssinatura, competencia, valor, data_vencimento, status, data_pagamento
              FROM Pagamento
             WHERE status = ?
             ORDER BY data_vencimento DESC, idPagamento DESC
        """;
        List<Pagamento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== READ (vencidos não pagos) =====
    public List<Pagamento> listarVencidos() throws SQLException {
        String sql = """
            SELECT idPagamento, Assinatura_idAssinatura, competencia, valor, data_vencimento, status, data_pagamento
              FROM Pagamento
             WHERE status = 'PENDENTE'
               AND data_vencimento < CURRENT_DATE
             ORDER BY data_vencimento ASC, idPagamento DESC
        """;
        List<Pagamento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    // ===== PATCH (alterar status) =====
    public boolean alterarStatus(long id, PagamentoStatus status) throws SQLException {
        String sql = "UPDATE Pagamento SET status = ? WHERE idPagamento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== PATCH (registrar pagamento) =====
    public boolean registrarPagamento(long id, LocalDate dataPagamento) throws SQLException {
        String sql = "UPDATE Pagamento SET status = 'PAGO', data_pagamento = ? WHERE idPagamento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, toSqlDate(dataPagamento)); // se vier null do service, ele define LocalDate.now() lá
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== Helpers =====
    private Pagamento map(ResultSet rs) throws SQLException {
        Pagamento p = new Pagamento();
        p.setId(rs.getLong("idPagamento"));

        Assinatura a = new Assinatura();
        a.setId(rs.getLong("Assinatura_idAssinatura"));
        p.setAssinatura(a);

        p.setCompetencia(toLocalDate(rs.getDate("competencia")));
        p.setValor(rs.getBigDecimal("valor"));
        p.setDataVencimento(toLocalDate(rs.getDate("data_vencimento")));
        p.setStatus(PagamentoStatus.valueOf(rs.getString("status")));
        p.setDataPagamento(toLocalDate(rs.getDate("data_pagamento")));
        return p;
    }

    private static LocalDate toLocalDate(Date d) {
        return d != null ? d.toLocalDate() : null;
    }

    private static Date toSqlDate(LocalDate d) {
        return d != null ? Date.valueOf(d) : null;
    }
}
