package com.br.inatel.dao;

import com.br.inatel.model.Instrutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstrutorDao {

    private final Connection conn;

    public InstrutorDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Instrutor i) throws SQLException {
        String sql = "INSERT INTO Instrutor (nome, cref) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getNome());
            ps.setString(2, i.getCref());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setId(rs.getLong(1));
            }
        }
    }

    // ===== UPDATE =====
    public boolean atualizar(Instrutor i) throws SQLException {
        String sql = "UPDATE Instrutor SET nome = ?, cref = ? WHERE idInstrutor = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, i.getNome());
            ps.setString(2, i.getCref());
            ps.setLong(3, i.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ (by ID) =====
    public Instrutor buscarPorId(long id) throws SQLException {
        String sql = "SELECT idInstrutor, nome, cref FROM Instrutor WHERE idInstrutor = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    // ===== READ (all) =====
    public List<Instrutor> listarTodos() throws SQLException {
        String sql = "SELECT idInstrutor, nome, cref FROM Instrutor ORDER BY nome";
        List<Instrutor> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    // ===== SEARCH (by name) =====
    public List<Instrutor> buscarPorNome(String termo) throws SQLException {
        String sql = "SELECT idInstrutor, nome, cref FROM Instrutor WHERE nome LIKE CONCAT('%', ?, '%') ORDER BY nome";
        List<Instrutor> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, termo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== DELETE =====
    public boolean deletarPorId(long id) throws SQLException {
        // (Opcional, mas recomendado) Evita erro de FK checando vínculos antes
        if (possuiVinculos(id)) {
            return false; // há treinos vinculados → não exclui
        }
        String sql = "DELETE FROM Instrutor WHERE idInstrutor = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== EXISTS =====
    public boolean existePorId(long id) throws SQLException {
        String sql = "SELECT 1 FROM Instrutor WHERE idInstrutor = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean possuiVinculos(long idInstrutor) throws SQLException {
        String sql = "SELECT COUNT(*) AS qtd FROM Treino WHERE Instrutor_idInstrutor = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idInstrutor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("qtd") > 0;
                return false;
            }
        }
    }

    // ===== MAP helper =====
    private Instrutor map(ResultSet rs) throws SQLException {
        Instrutor i = new Instrutor();
        i.setId(rs.getLong("idInstrutor"));
        i.setNome(rs.getString("nome"));
        i.setCref(rs.getString("cref"));
        return i;
    }
}
