package com.br.inatel.service;

import com.br.inatel.model.TreinoExercicio;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.util.List;

public interface TreinoExercicioService {
    TreinoExercicio adicionar(long idTreino, short ordem, long idExercicio, short series, short reps, Integer cargaKg, short descansoSeg) throws BusinessException;

    TreinoExercicio atualizar(long idTreino, short ordem, long idExercicio, short series, short reps, Integer cargaKg, short descansoSeg) throws NotFoundException, BusinessException;

    void alterarOrdem(long idTreino, short ordemAntiga, short novaOrdem) throws NotFoundException, BusinessException;
    void trocarOrdem(long idTreino, short ordemA, short ordemB) throws NotFoundException, BusinessException;

    TreinoExercicio buscarPorChave(long idTreino, short ordem) throws NotFoundException, BusinessException;
    List<TreinoExercicio> listarPorTreino(long idTreino) throws NotFoundException,BusinessException;

    void removerPorChave(long idTreino, short ordem) throws NotFoundException, BusinessException;
    int removerTodosDoTreino(long idTreino) throws NotFoundException, BusinessException;
}
