package com.br.inatel.dao;

import com.br.inatel.model.Aluno;
import com.br.inatel.model.Assinatura;
import com.br.inatel.model.Plano;
import com.br.inatel.model.enums.AssinaturaStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssinaturaDao {

    private final Connection conn;

    public AssinaturaDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(Assinatura a) throws SQLException {
        String sql = """
            INSERT INTO Assinatura (data_inicio, data_fim, status, Aluno_idAluno, Plano_idPlano)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(a.getDataInicio()));
            ps.setDate(2, Date.valueOf(a.getDataFim()));
            ps.setString(3, a.getStatus().name());
            ps.setLong(4, a.getAluno().getId());
            ps.setLong(5, a.getPlano().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getLong(1));
            }
        }
    }

    // ===== UPDATE =====
    public boolean atualizar(Assinatura a) throws SQLException {
        String sql = """
            UPDATE Assinatura
               SET data_inicio = ?, data_fim = ?, status = ?
             WHERE idAssinatura = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(a.getDataInicio()));
            ps.setDate(2, Date.valueOf(a.getDataFim()));
            ps.setString(3, a.getStatus().name());
            ps.setLong(4, a.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== READ (buscar por ID) =====
    public Assinatura buscarPorId(long id) throws SQLException {
        String sql = """
            SELECT a.idAssinatura, a.data_inicio, a.data_fim, a.status,
                   al.idAluno, al.nome AS aluno_nome,
                   p.idPlano, p.nome AS plano_nome
              FROM Assinatura a
              JOIN Aluno al ON al.idAluno = a.Aluno_idAluno
              JOIN Plano p  ON p.idPlano  = a.Plano_idPlano
             WHERE a.idAssinatura = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    // ===== READ (todas) =====
    public List<Assinatura> listarTodas() throws SQLException {
        String sql = """
            SELECT a.idAssinatura, a.data_inicio, a.data_fim, a.status,
                   al.idAluno, al.nome AS aluno_nome,
                   p.idPlano, p.nome AS plano_nome
              FROM Assinatura a
              JOIN Aluno al ON al.idAluno = a.Aluno_idAluno
              JOIN Plano p  ON p.idPlano  = a.Plano_idPlano
             ORDER BY a.data_inicio DESC, a.idAssinatura DESC
        """;
        List<Assinatura> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    // ===== READ (por aluno) =====
    public List<Assinatura> listarPorAluno(long idAluno) throws SQLException {
        String sql = """
            SELECT a.idAssinatura, a.data_inicio, a.data_fim, a.status,
                   p.idPlano, p.nome AS plano_nome
              FROM Assinatura a
              JOIN Plano p ON p.idPlano = a.Plano_idPlano
             WHERE a.Aluno_idAluno = ?
             ORDER BY a.data_inicio DESC, a.idAssinatura DESC
        """;
        List<Assinatura> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAluno);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assinatura a = new Assinatura();
                    a.setId(rs.getLong("idAssinatura"));
                    a.setDataInicio(rs.getDate("data_inicio").toLocalDate());
                    a.setDataFim(rs.getDate("data_fim").toLocalDate());
                    a.setStatus(AssinaturaStatus.valueOf(rs.getString("status")));

                    Plano p = new Plano();
                    p.setId(rs.getLong("idPlano"));
                    p.setNome(rs.getString("plano_nome"));
                    a.setPlano(p);

                    lista.add(a);
                }
            }
        }
        return lista;
    }

    // ===== READ (por plano) =====
    public List<Assinatura> listarPorPlano(long idPlano) throws SQLException {
        String sql = """
            SELECT a.idAssinatura, a.data_inicio, a.data_fim, a.status,
                   al.idAluno, al.nome AS aluno_nome
              FROM Assinatura a
              JOIN Aluno al ON al.idAluno = a.Aluno_idAluno
             WHERE a.Plano_idPlano = ?
             ORDER BY a.data_inicio DESC, a.idAssinatura DESC
        """;
        List<Assinatura> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPlano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assinatura a = new Assinatura();
                    a.setId(rs.getLong("idAssinatura"));
                    a.setDataInicio(rs.getDate("data_inicio").toLocalDate());
                    a.setDataFim(rs.getDate("data_fim").toLocalDate());
                    a.setStatus(AssinaturaStatus.valueOf(rs.getString("status")));

                    Aluno al = new Aluno();
                    al.setId(rs.getLong("idAluno"));
                    al.setNome(rs.getString("aluno_nome"));
                    a.setAluno(al);

                    lista.add(a);
                }
            }
        }
        return lista;
    }

    // ===== READ (ativas por aluno) =====
    public List<Assinatura> listarAtivasPorAluno(long idAluno) throws SQLException {
        String sql = """
            SELECT idAssinatura, data_inicio, data_fim, status
              FROM Assinatura
             WHERE Aluno_idAluno = ? AND status = 'ATIVA'
        """;
        List<Assinatura> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAluno);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assinatura a = new Assinatura();
                    a.setId(rs.getLong("idAssinatura"));
                    a.setDataInicio(rs.getDate("data_inicio").toLocalDate());
                    a.setDataFim(rs.getDate("data_fim").toLocalDate());
                    a.setStatus(AssinaturaStatus.valueOf(rs.getString("status")));
                    lista.add(a);
                }
            }
        }
        return lista;
    }

    // ===== PATCH (alterar status) =====
    public boolean alterarStatus(long id, AssinaturaStatus status) throws SQLException {
        String sql = "UPDATE Assinatura SET status = ? WHERE idAssinatura = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== SOFT DELETE =====
    public int softDelete(long id) throws SQLException {
        String sql = """
            UPDATE Assinatura
               SET status = 'CANCELADA',
                   data_fim = GREATEST(data_fim, CURRENT_DATE)
             WHERE idAssinatura = ? AND status = 'ATIVA'
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    // ===== EXISTS =====
    public boolean existePorId(long id) throws SQLException {
        String sql = "SELECT 1 FROM Assinatura WHERE idAssinatura = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== Helper para mapear resultado =====
    private Assinatura map(ResultSet rs) throws SQLException {
        Assinatura a = new Assinatura();
        a.setId(rs.getLong("idAssinatura"));
        a.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        a.setDataFim(rs.getDate("data_fim").toLocalDate());
        a.setStatus(AssinaturaStatus.valueOf(rs.getString("status")));

        Aluno al = new Aluno();
        al.setId(rs.getLong("idAluno"));
        al.setNome(rs.getString("aluno_nome"));
        a.setAluno(al);

        Plano p = new Plano();
        p.setId(rs.getLong("idPlano"));
        p.setNome(rs.getString("plano_nome"));
        a.setPlano(p);

        return a;
    }
}
