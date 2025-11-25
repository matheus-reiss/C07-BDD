package com.br.inatel.dao;

import com.br.inatel.model.Exercicio;
import com.br.inatel.model.Treino;
import com.br.inatel.model.TreinoExercicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreinoExercicioDao {

    private final Connection conn;

    public TreinoExercicioDao(Connection conn) {
        this.conn = conn;
    }

    // ===== CREATE =====
    public void insert(TreinoExercicio te) throws SQLException {
        String sql = """
            INSERT INTO TreinoExercicio
                (Treino_idTreino, Exercicio_idExercicio, ordem, series, repeticoes, carga_kg, descanso_seg)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, te.getTreino().getId());
            ps.setLong(2, te.getExercicio().getId());
            ps.setInt(3, te.getOrdem());
            ps.setInt(4, te.getSeries());
            ps.setInt(5, te.getReps());
            if (te.getCargaKg() != null) ps.setInt(6, te.getCargaKg()); else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, te.getDescansoSeg());
            ps.executeUpdate();
        }
    }

    // ===== UPDATE (mesma ordem) =====
    public boolean atualizar(TreinoExercicio te) throws SQLException {
        String sql = """
            UPDATE TreinoExercicio
               SET Exercicio_idExercicio = ?,
                   series = ?,
                   repeticoes = ?,
                   carga_kg = ?,
                   descanso_seg = ?
             WHERE Treino_idTreino = ? AND ordem = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, te.getExercicio().getId());
            ps.setInt(2, te.getSeries());
            ps.setInt(3, te.getReps());
            if (te.getCargaKg() != null) ps.setInt(4, te.getCargaKg()); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, te.getDescansoSeg());
            ps.setLong(6, te.getTreino().getId());
            ps.setInt(7, te.getOrdem());
            return ps.executeUpdate() > 0;
        }
    }

    // ===== ALTERAR ORDEM (1 item) =====
    public boolean alterarOrdem(long idTreino, int ordemAntiga, int novaOrdem) throws SQLException {
        String sql = "UPDATE TreinoExercicio SET ordem = ? WHERE Treino_idTreino = ? AND ordem = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, novaOrdem);
            ps.setLong(2, idTreino);
            ps.setInt(3, ordemAntiga);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean existeOrdem(long idTreino, int ordem) throws SQLException {
        String sql = "SELECT 1 FROM TreinoExercicio WHERE Treino_idTreino = ? AND ordem = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTreino);
            ps.setInt(2, ordem);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== TROCAR ORDEM (2 itens) =====
    public boolean trocarOrdem(long idTreino, int ordemA, int ordemB) throws SQLException {
        String sql = """
            UPDATE TreinoExercicio
               SET ordem = CASE
                   WHEN ordem = ? THEN ?
                   WHEN ordem = ? THEN ?
               END
             WHERE Treino_idTreino = ? AND ordem IN (?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ordemA);
            ps.setInt(2, ordemB);
            ps.setInt(3, ordemB);
            ps.setInt(4, ordemA);
            ps.setLong(5, idTreino);
            ps.setInt(6, ordemA);
            ps.setInt(7, ordemB);
            return ps.executeUpdate() == 2;
        }
    }

    // ===== READ  =====
    public TreinoExercicio buscarPorChave(long idTreino, int ordem) throws SQLException {
        String sql = """
            SELECT tx.Treino_idTreino, tx.ordem, tx.Exercicio_idExercicio,
                   tx.series, tx.repeticoes, tx.carga_kg, tx.descanso_seg,
                   e.nome AS exercicio_nome
              FROM TreinoExercicio tx
              JOIN Exercicio e ON e.idExercicio = tx.Exercicio_idExercicio
             WHERE tx.Treino_idTreino = ? AND tx.ordem = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTreino);
            ps.setInt(2, ordem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    // ===== READ  =====
    public List<TreinoExercicio> listarPorTreino(long idTreino) throws SQLException {
        String sql = """
            SELECT tx.Treino_idTreino, tx.ordem, tx.Exercicio_idExercicio,
                   tx.series, tx.repeticoes, tx.carga_kg, tx.descanso_seg,
                   e.nome AS exercicio_nome
              FROM TreinoExercicio tx
              JOIN Exercicio e ON e.idExercicio = tx.Exercicio_idExercicio
             WHERE tx.Treino_idTreino = ?
             ORDER BY tx.ordem ASC
        """;
        List<TreinoExercicio> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTreino);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    // ===== DELETE (1 item) =====
    public boolean deletarPorChave(long idTreino, int ordem) throws SQLException {
        String sql = "DELETE FROM TreinoExercicio WHERE Treino_idTreino = ? AND ordem = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTreino);
            ps.setInt(2, ordem);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== DELETE (todos do treino) =====
    public int deletarTodosDoTreino(long idTreino) throws SQLException {
        String sql = "DELETE FROM TreinoExercicio WHERE Treino_idTreino = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTreino);
            return ps.executeUpdate();
        }
    }

    // ===== MAP helper =====
    private TreinoExercicio map(ResultSet rs) throws SQLException {
        TreinoExercicio te = new TreinoExercicio();

        Treino t = new Treino();
        t.setId(rs.getLong("Treino_idTreino"));
        te.setTreino(t);

        te.setOrdem((short) rs.getInt("ordem"));

        Exercicio e = new Exercicio();
        e.setId(rs.getLong("Exercicio_idExercicio"));
        e.setNome(rs.getString("exercicio_nome"));
        te.setExercicio(e);

        te.setSeries((short) rs.getInt("series"));
        te.setReps((short) rs.getInt("repeticoes"));

        int carga = rs.getInt("carga_kg");
        te.setCargaKg(rs.wasNull() ? null : carga);

        te.setDescansoSeg((short) rs.getInt("descanso_seg"));

        return te;
    }
}
