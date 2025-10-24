package com.br.inatel.dao;

import com.br.inatel.model.Aluno;
import com.br.inatel.model.Instrutor;
import com.br.inatel.model.Treino;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TreinoDao {

    private final Connection conn;

    public TreinoDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Treino t) throws SQLException {
        String sql = "INSERT INTO Treino (titulo, data_criacao, ativo, Instrutor_idInstrutor, Aluno_idAluno) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTitulo());
            ps.setDate(2, toSqlDate(t.getCreatedAt()));
            ps.setBoolean(3, t.isAtivo());
            ps.setLong(4, t.getInstrutor().getId());
            ps.setLong(5, t.getAluno().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(rs.getLong(1));
            }
        }
    }

    // ===== UPDATE =====
    public int atualizar(Treino t) throws SQLException {
        String sql = "UPDATE Treino SET titulo = ?, data_criacao = ?, ativo = ?, Instrutor_idInstrutor = ?, Aluno_idAluno = ? WHERE idTreino = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTitulo());
            ps.setDate(2, toSqlDate(t.getCreatedAt()));
            ps.setBoolean(3, t.isAtivo());
            ps.setLong(4, t.getInstrutor().getId());
            ps.setLong(5, t.getAluno().getId());
            ps.setLong(6, t.getId());
            return ps.executeUpdate();
        }
    }

    // ===== READ (by ID) =====
    public Treino buscarPorId(long id) throws SQLException {
        String sql = """
            SELECT t.idTreino, t.titulo, t.data_criacao, t.ativo,
                   t.Instrutor_idInstrutor, t.Aluno_idAluno
              FROM Treino t
             WHERE t.idTreino = ?
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
    public List<Treino> listarTodos() throws SQLException {
        String sql = """
            SELECT t.idTreino, t.titulo, t.data_criacao, t.ativo,
                   t.Instrutor_idInstrutor, t.Aluno_idAluno
              FROM Treino t
             ORDER BY t.data_criacao DESC, t.idTreino DESC
        """;
        List<Treino> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    // ===== FILTERS =====
    public List<Treino> listarPorAluno(long idAluno) throws SQLException {
        String sql = """
            SELECT t.idTreino, t.titulo, t.data_criacao, t.ativo,
                   t.Instrutor_idInstrutor, t.Aluno_idAluno
              FROM Treino t
             WHERE t.Aluno_idAluno = ?
             ORDER BY t.data_criacao DESC, t.idTreino DESC
        """;
        List<Treino> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAluno);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    public List<Treino> listarPorInstrutor(long idInstrutor) throws SQLException {
        String sql = """
            SELECT t.idTreino, t.titulo, t.data_criacao, t.ativo,
                   t.Instrutor_idInstrutor, t.Aluno_idAluno
              FROM Treino t
             WHERE t.Instrutor_idInstrutor = ?
             ORDER BY t.data_criacao DESC, t.idTreino DESC
        """;
        List<Treino> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idInstrutor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== PATCH: ativo =====
    public int alterarAtivo(long id, boolean ativo) throws SQLException {
        String sql = "UPDATE Treino SET ativo = ? WHERE idTreino = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1,ativo);
            ps.setLong(2, id);
            return ps.executeUpdate();
        }
    }

    // ===== DELETE =====
    public int deletarPorId(long id) throws SQLException {
        String sql = "DELETE FROM Treino WHERE idTreino = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }

    }

    // ===== EXISTS =====
    public boolean existePorId(long id) throws SQLException {
        String sql = "SELECT 1 FROM Treino WHERE idTreino = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== MAP helper =====
    private Treino map(ResultSet rs) throws SQLException {
        Treino t = new Treino();
        t.setId(rs.getLong("idTreino"));
        t.setTitulo(rs.getString("titulo"));

        Date d = rs.getDate("data_criacao");
        t.setCreatedAt(d != null ? d.toLocalDate() : null);

        t.setAtivo(rs.getBoolean("ativo"));

        Instrutor i = new Instrutor();
        i.setId(rs.getLong("Instrutor_idInstrutor"));
        t.setInstrutor(i);

        Aluno a = new Aluno();
        a.setId(rs.getLong("Aluno_idAluno"));
        t.setAluno(a);

        return t;
    }

    private static Date toSqlDate(LocalDate d) {
        return d != null ? Date.valueOf(d) : null;
    }
}
