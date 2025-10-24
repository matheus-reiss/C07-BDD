package com.br.inatel.service.impl;

import com.br.inatel.dao.ExercicioDao;
import com.br.inatel.dao.TreinoDao;
import com.br.inatel.dao.TreinoExercicioDao;
import com.br.inatel.model.Exercicio;
import com.br.inatel.model.Treino;
import com.br.inatel.model.TreinoExercicio;
import com.br.inatel.service.TreinoExercicioService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class TreinoExercicioServiceImpl implements TreinoExercicioService {

    private final TreinoExercicioDao teDao;
    private final TreinoDao treinoDao;
    private final ExercicioDao exercicioDao;

    public TreinoExercicioServiceImpl(TreinoExercicioDao teDao, TreinoDao treinoDao, ExercicioDao exercicioDao) {
        this.teDao = Objects.requireNonNull(teDao, "teDao não pode ser null");
        this.treinoDao = Objects.requireNonNull(treinoDao, "treinoDao não pode ser null");
        this.exercicioDao = Objects.requireNonNull(exercicioDao, "exercicioDao não pode ser null");
    }

    // ========= helper para garantir que o treino existe =========
    private void garantirTreinoExiste(long idTreino) throws NotFoundException {
        try {
            if (!treinoDao.existePorId(idTreino)) {
                throw new NotFoundException("Treino não encontrado: " + idTreino);
            }
        } catch (SQLException e) {
            throw new BusinessException("Falha ao verificar treino: " + e.getMessage());
        }
    }

    // ========== CREATE ==========
    @Override
    public TreinoExercicio adicionar(long idTreino, short ordem, long idExercicio,
                                     short series, short reps, Integer cargaKg, short descansoSeg)
            throws BusinessException {
        validarIdPositivo(idTreino, "idTreino");
        validarIdPositivo(idExercicio, "idExercicio");
        validarOrdem(ordem);
        validarCargaSeriesReps(series, reps, cargaKg, descansoSeg);

        try {
            garantirTreinoExiste(idTreino);
            if (!exercicioDao.existePorId(idExercicio)) throw new NotFoundException("Exercício não encontrado: " + idExercicio);

            if (teDao.existeOrdem(idTreino, ordem)) {
                throw new BusinessException("Já existe item na ordem " + ordem + " para o treino " + idTreino + ".");
            }

            TreinoExercicio te = new TreinoExercicio();
            Treino t = new Treino(); t.setId(idTreino); te.setTreino(t);
            Exercicio e = new Exercicio(); e.setId(idExercicio); te.setExercicio(e);

            te.setOrdem(ordem);
            te.setSeries(series);
            te.setReps(reps);
            te.setCargaKg(cargaKg);
            te.setDescansoSeg(descansoSeg);

            teDao.insert(te);
            return te;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao adicionar exercício ao treino: " + e.getMessage());
        }
    }

    // ========== UPDATE ==========
    @Override
    public TreinoExercicio atualizar(long idTreino, short ordem, long idExercicio,
                                     short series, short reps, Integer cargaKg, short descansoSeg)
            throws NotFoundException, BusinessException {
        validarIdPositivo(idTreino, "idTreino");
        validarIdPositivo(idExercicio, "idExercicio");
        validarOrdem(ordem);
        validarCargaSeriesReps(series, reps, cargaKg, descansoSeg);

        try {
            garantirTreinoExiste(idTreino);

            TreinoExercicio atual = teDao.buscarPorChave(idTreino, ordem);
            if (atual == null) throw new NotFoundException(
                    "Item do treino não encontrado (treino=" + idTreino + ", ordem=" + ordem + ").");

            if (!exercicioDao.existePorId(idExercicio)) {
                throw new NotFoundException("Exercício não encontrado: " + idExercicio);
            }

            Exercicio e = new Exercicio(); e.setId(idExercicio);
            atual.setExercicio(e);
            atual.setSeries(series);
            atual.setReps(reps);
            atual.setCargaKg(cargaKg);
            atual.setDescansoSeg(descansoSeg);

            boolean ok = teDao.atualizar(atual);
            if (!ok) throw new BusinessException("Atualização não aplicada (treino=" + idTreino + ", ordem=" + ordem + ").");
            return atual;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar item do treino: " + e.getMessage());
        }
    }

    // ========== SWAP ==========
    @Override
    public void alterarOrdem(long idTreino, short ordemAntiga, short novaOrdem)
            throws NotFoundException, BusinessException {
        validarIdPositivo(idTreino, "idTreino");
        validarOrdem(ordemAntiga);
        validarOrdem(novaOrdem);

        try {
            garantirTreinoExiste(idTreino);

            TreinoExercicio atual = teDao.buscarPorChave(idTreino, ordemAntiga);
            if (atual == null) throw new NotFoundException(
                    "Item não encontrado para mover (treino=" + idTreino + ", ordem=" + ordemAntiga + ").");

            boolean ok = teDao.alterarOrdem(idTreino, ordemAntiga, novaOrdem);
            if (!ok) throw new BusinessException("Não foi possível alterar a ordem.");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao alterar ordem: " + e.getMessage());
        }
    }

    @Override
    public void trocarOrdem(long idTreino, short ordemA, short ordemB)
            throws NotFoundException, BusinessException {
        validarIdPositivo(idTreino, "idTreino");
        validarOrdem(ordemA);
        validarOrdem(ordemB);

        try {
            garantirTreinoExiste(idTreino);

            if (teDao.buscarPorChave(idTreino, ordemA) == null)
                throw new NotFoundException("Item (ordem " + ordemA + ") não encontrado no treino " + idTreino + ".");
            if (teDao.buscarPorChave(idTreino, ordemB) == null)
                throw new NotFoundException("Item (ordem " + ordemB + ") não encontrado no treino " + idTreino + ".");

            boolean ok = teDao.trocarOrdem(idTreino, ordemA, ordemB);
            if (!ok) throw new BusinessException("Não foi possível trocar as ordens.");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao trocar ordens: " + e.getMessage());
        }
    }

    // ========== READ ==========
    @Override
    public TreinoExercicio buscarPorChave(long idTreino, short ordem) throws NotFoundException {
        validarIdPositivo(idTreino, "idTreino");
        validarOrdem(ordem);
        try {
            garantirTreinoExiste(idTreino);

            TreinoExercicio te = teDao.buscarPorChave(idTreino, ordem);
            if (te == null) throw new NotFoundException(
                    "Item do treino não encontrado (treino=" + idTreino + ", ordem=" + ordem + ").");
            return te;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar item do treino: " + e.getMessage());
        }
    }

    @Override
    public List<TreinoExercicio> listarPorTreino(long idTreino) throws NotFoundException {
        validarIdPositivo(idTreino, "idTreino");
        try {
            garantirTreinoExiste(idTreino);
            return teDao.listarPorTreino(idTreino);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar itens do treino: " + e.getMessage());
        }
    }

    // ========== DELETE ==========
    @Override
    public void removerPorChave(long idTreino, short ordem) throws NotFoundException {
        validarIdPositivo(idTreino, "idTreino");
        validarOrdem(ordem);
        try {
            garantirTreinoExiste(idTreino);

            if (teDao.buscarPorChave(idTreino, ordem) == null) {
                throw new NotFoundException("Item não encontrado para remoção (treino=" + idTreino + ", ordem=" + ordem + ").");
            }
            boolean ok = teDao.deletarPorChave(idTreino, ordem);
            if (!ok) throw new BusinessException("Exclusão não aplicada (treino=" + idTreino + ", ordem=" + ordem + ").");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao excluir item do treino: " + e.getMessage());
        }
    }

    @Override
    public int removerTodosDoTreino(long idTreino) throws NotFoundException {
        validarIdPositivo(idTreino, "idTreino");
        try {
            garantirTreinoExiste(idTreino);
            return teDao.deletarTodosDoTreino(idTreino);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao excluir itens do treino: " + e.getMessage());
        }
    }

    // ========== validações ==========
    private void validarIdPositivo(long id, String campo) {
        if (id <= 0) throw new BusinessException(campo + " deve ser positivo.");
    }

    private void validarOrdem(short ordem) {
        if (ordem <= 0) throw new BusinessException("ordem deve ser >= 1.");
        if (ordem > 5000) throw new BusinessException("ordem muito alta.");
    }

    private void validarCargaSeriesReps(short series, short reps, Integer cargaKg, short descansoSeg) {
        if (series <= 0) throw new BusinessException("series deve ser >= 1.");
        if (reps <= 0) throw new BusinessException("reps deve ser >= 1.");
        if (descansoSeg < 0) throw new BusinessException("descansoSeg não pode ser negativo.");
        if (cargaKg != null && cargaKg < 0) throw new BusinessException("cargaKg não pode ser negativa.");
    }
}
