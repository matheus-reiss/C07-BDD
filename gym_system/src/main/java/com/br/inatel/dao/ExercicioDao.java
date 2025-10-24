package com.br.inatel.dao;

import com.br.inatel.model.Exercicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExercicioDao {

    private final Connection conn;

    public ExercicioDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Exercicio e) throws SQLException {
        String sql = "INSERT INTO Exercicio (nome, grupo_muscular) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getGrupoMuscular());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getLong(1));
            }
        }
    }

    // ===== UPDATE =====
    public boolean atualizar(Exercicio e) throws SQLException {
        String sql = "UPDATE Exercicio SET nome = ?, grupo_muscular = ? WHERE idExercicio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getGrupoMuscular());
            ps.setLong(3, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ (by ID) =====
    public Exercicio buscarPorId(long id) throws SQLException {
        String sql = "SELECT idExercicio, nome, grupo_muscular FROM Exercicio WHERE idExercicio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    // ===== READ (all) =====
    public List<Exercicio> listarTodos() throws SQLException {
        String sql = "SELECT idExercicio, nome, grupo_muscular FROM Exercicio ORDER BY nome";
        List<Exercicio> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    // ===== SEARCH (by name) =====
    public List<Exercicio> buscarPorNome(String termo) throws SQLException {
        String sql = "SELECT idExercicio, nome, grupo_muscular FROM Exercicio WHERE nome LIKE CONCAT('%', ?, '%') ORDER BY nome";
        List<Exercicio> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, termo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== SEARCH (by group) =====
    public List<Exercicio> buscarPorGrupoMuscular(String grupo) throws SQLException {
        String sql = "SELECT idExercicio, nome, grupo_muscular FROM Exercicio WHERE grupo_muscular LIKE CONCAT('%', ?, '%') ORDER BY nome";
        List<Exercicio> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grupo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== DELETE =====
    public boolean deletarPorId(long id) throws SQLException {
        String sql = "DELETE FROM Exercicio WHERE idExercicio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== EXISTS =====
    public boolean existePorId(long id) throws SQLException {
        String sql = "SELECT 1 FROM Exercicio WHERE idExercicio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== CHECK vinculos =====
    public boolean possuiVinculos(long idExercicio) throws SQLException {
        String sql = "SELECT COUNT(*) AS qtd FROM TreinoExercicio WHERE Exercicio_idExercicio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idExercicio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("qtd") > 0;
                }
                return false;
            }
        }
    }

    // ===== MAP helper =====
    private Exercicio map(ResultSet rs) throws SQLException {
        Exercicio e = new Exercicio();
        e.setId(rs.getLong("idExercicio"));
        e.setNome(rs.getString("nome"));
        e.setGrupoMuscular(rs.getString("grupo_muscular"));
        return e;
    }
}
