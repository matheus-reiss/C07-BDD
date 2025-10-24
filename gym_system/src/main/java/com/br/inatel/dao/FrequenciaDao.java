package com.br.inatel.dao;

import com.br.inatel.model.Aluno;
import com.br.inatel.model.Frequencia;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FrequenciaDao {

    private final Connection conn;

    public FrequenciaDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Frequencia f) throws SQLException {
        String sql = "INSERT INTO Frequencia (Aluno_idAluno, data_checkin) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, f.getAluno().getId());
            ps.setDate(2, Date.valueOf(f.getDataCheckin()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getLong(1));
            }
        }
    }

    // ===== UPDATE =====
    public boolean atualizar(Frequencia f) throws SQLException {
        String sql = "UPDATE Frequencia SET Aluno_idAluno = ?, data_checkin = ? WHERE idFrequencia = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, f.getAluno().getId());
            ps.setDate(2, Date.valueOf(f.getDataCheckin()));
            ps.setLong(3, f.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ  =====
    public Frequencia buscarPorId(long id) throws SQLException {
        String sql = """
            SELECT f.idFrequencia, f.data_checkin,
                   a.idAluno, a.nome AS aluno_nome
              FROM Frequencia f
              JOIN Aluno a ON a.idAluno = f.Aluno_idAluno
             WHERE f.idFrequencia = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBasico(rs);
                return null;
            }
        }
    }

    public List<Frequencia> listarTodas() throws SQLException {
        String sql = """
            SELECT f.idFrequencia, f.data_checkin,
                   a.idAluno, a.nome AS aluno_nome
              FROM Frequencia f
              JOIN Aluno a ON a.idAluno = f.Aluno_idAluno
             ORDER BY f.data_checkin DESC, f.idFrequencia DESC
        """;
        List<Frequencia> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapBasico(rs));
            }
        }
        return lista;
    }

    public List<Frequencia> listarPorAluno(long idAluno) throws SQLException {
        String sql = """
            SELECT f.idFrequencia, f.data_checkin,
                   a.idAluno, a.nome AS aluno_nome
              FROM Frequencia f
              JOIN Aluno a ON a.idAluno = f.Aluno_idAluno
             WHERE f.Aluno_idAluno = ?
             ORDER BY f.data_checkin DESC, f.idFrequencia DESC
        """;
        List<Frequencia> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1,idAluno);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    lista.add(mapBasico(rs));
                }
            }
        }
        return  lista;
    }

    public List<Frequencia> listarPorPeriodo(LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = """
            SELECT f.idFrequencia, f.data_checkin,
                   a.idAluno, a.nome AS aluno_nome
              FROM Frequencia f
              JOIN Aluno a ON a.idAluno = f.Aluno_idAluno
             WHERE f.data_checkin BETWEEN ? AND ?
             ORDER BY f.data_checkin DESC, f.idFrequencia DESC
        """;
        List<Frequencia> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapBasico(rs));
            }
        }
        return lista;
    }

    public Frequencia buscarUltimoCheckinPorAluno(long idAluno) throws SQLException {
        String sql = """
            SELECT f.idFrequencia, f.data_checkin,
                   a.idAluno, a.nome AS aluno_nome
              FROM Frequencia f
              JOIN Aluno a ON a.idAluno = f.Aluno_idAluno
             WHERE f.Aluno_idAluno = ?
             ORDER BY f.data_checkin DESC, f.idFrequencia DESC
             LIMIT 1
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1,idAluno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBasico(rs);
                return null;
            }
        }
    }

    // ===== EXISTS  =====
    public boolean existeCheckinNoDia(long idAluno, LocalDate dia) throws SQLException {
        String sql = """
            SELECT 1
              FROM Frequencia
             WHERE Aluno_idAluno = ? AND data_checkin = ?
             LIMIT 1
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAluno);
            ps.setDate(2, Date.valueOf(dia));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== Map helper =====
    private Frequencia mapBasico(ResultSet rs) throws SQLException {
        Frequencia f = new Frequencia();
        f.setId(rs.getLong("idFrequencia"));

        Date d = rs.getDate("data_checkin");
        f.setDataCheckin(d != null ? d.toLocalDate() : null);

        Aluno a = new Aluno();
        a.setId(rs.getLong("idAluno"));
        a.setNome(rs.getString("aluno_nome"));
        f.setAluno(a);

        return f;
    }
}
