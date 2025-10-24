package com.br.inatel.dao;

import com.br.inatel.model.Plano;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanoDao {

    private final Connection conn;

    public PlanoDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Plano p) throws SQLException {
        String sql = "INSERT INTO Plano (nome, valor, duracao) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setBigDecimal(2, p.getPreco());
            ps.setInt(3, p.getDuracaoMeses());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }
        }
    }

    // ===== UPDATE =====
    public boolean atualizar(Plano p) throws SQLException {
        String sql = "UPDATE Plano SET nome = ?, valor = ?, duracao = ? WHERE idPlano = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setBigDecimal(2, p.getPreco());
            ps.setInt(3, p.getDuracaoMeses());
            ps.setLong(4, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ (by ID) =====
    public Plano buscarPorId(long id) throws SQLException {
        String sql = "SELECT idPlano, nome, valor, duracao FROM Plano WHERE idPlano = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    // ===== READ (all) =====
    public List<Plano> listarTodos() throws SQLException {
        String sql = "SELECT idPlano, nome, valor, duracao FROM Plano ORDER BY nome";
        List<Plano> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    // ===== DELETE =====
    public boolean deletarPorId(long id) throws SQLException {
        // evita violação de FK com Assinatura
        if (possuiVinculos(id)) {
            return false;
        }
        String sql = "DELETE FROM Plano WHERE idPlano = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== EXISTS =====
    public boolean existePorId(long id) throws SQLException {
        String sql = "SELECT 1 FROM Plano WHERE idPlano = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== SEARCH (by name LIKE) =====
    public List<Plano> buscarPorNome(String termo) throws SQLException {
        String sql = "SELECT idPlano, nome, valor, duracao FROM Plano WHERE nome LIKE CONCAT('%', ?, '%') ORDER BY nome";
        List<Plano> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, termo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== SEARCH (by price range) =====
    public List<Plano> buscarPorFaixaPreco(BigDecimal minimo, BigDecimal maximo) throws SQLException {
        String sql = """
            SELECT idPlano, nome, valor, duracao
              FROM Plano
             WHERE valor BETWEEN ? AND ?
             ORDER BY valor, nome
        """;
        List<Plano> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, minimo);
            ps.setBigDecimal(2, maximo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== FK check: há assinaturas para este plano? =====
    public boolean possuiVinculos(long idPlano) throws SQLException {
        String sql = "SELECT COUNT(*) AS qtd FROM Assinatura WHERE Plano_idPlano = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPlano);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("qtd") > 0;
                return false;
            }
        }
    }

    // ===== MAP helper =====
    private Plano map(ResultSet rs) throws SQLException {
        Plano p = new Plano();
        p.setId(rs.getLong("idPlano"));
        p.setNome(rs.getString("nome"));
        p.setPreco(rs.getBigDecimal("valor"));
        p.setDuracaoMeses(rs.getInt("duracao"));
        return p;
    }
}
