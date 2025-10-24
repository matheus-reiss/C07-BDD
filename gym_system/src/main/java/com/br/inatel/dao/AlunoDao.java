package com.br.inatel.dao;

import com.br.inatel.model.Aluno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDao {

    private final Connection conn;

    public AlunoDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Aluno aluno) throws SQLException {
        String sql = "INSERT INTO Aluno (nome, nascimento, ativo, telefone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, aluno.getNome());
            if (aluno.getDataNascimento() != null) {
                ps.setDate(2, Date.valueOf(aluno.getDataNascimento()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setBoolean(3, aluno.isAtivo());
            ps.setString(4, aluno.getTelefone());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    aluno.setId(rs.getLong(1));
                }
            }
        }
    }

    // ===== UPDATE =====
    public boolean atualizar(Aluno aluno) throws SQLException {
        String sql = "UPDATE Aluno SET nome = ?, nascimento = ?, telefone = ? WHERE idAluno = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aluno.getNome());
            if (aluno.getDataNascimento() != null) {
                ps.setDate(2, Date.valueOf(aluno.getDataNascimento()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setString(3, aluno.getTelefone());
            ps.setLong(4, aluno.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ (by ID) =====
    public Aluno buscarPorId(long id) throws SQLException {
        String sql = "SELECT idAluno, nome, nascimento, ativo, telefone FROM Aluno WHERE idAluno = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
                return null;
            }
        }
    }

    // ===== READ (all) =====
    public List<Aluno> listarTodos() throws SQLException {
        String sql = "SELECT idAluno, nome, nascimento, ativo, telefone FROM Aluno ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                alunos.add(map(rs));
            }
        }
        return alunos;
    }

    // ===== SEARCH (by name) =====
    public List<Aluno> buscarPorNome(String termo) throws SQLException {
        String sql = "SELECT idAluno, nome, nascimento, ativo, telefone FROM Aluno WHERE nome LIKE CONCAT('%', ?, '%') ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, termo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alunos.add(map(rs));
                }
            }
        }
        return alunos;
    }

    // ===== PATCH (activate/deactivate) =====
    public boolean alterarAtivo(long id, boolean ativo) throws SQLException {
        String sql = "UPDATE Aluno SET ativo = ? WHERE idAluno = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, ativo);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== EXISTS =====
    public boolean existePorId(long id) throws SQLException {
        String sql = "SELECT 1 FROM Aluno WHERE idAluno = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== Helper =====
    private Aluno map(ResultSet rs) throws SQLException {
        Aluno a = new Aluno();
        a.setId(rs.getLong("idAluno"));
        a.setNome(rs.getString("nome"));

        Date data = rs.getDate("nascimento");
        a.setDataNascimento(data != null ? data.toLocalDate() : null);

        a.setAtivo(rs.getBoolean("ativo"));
        a.setTelefone(rs.getString("telefone"));
        return a;
    }
}
