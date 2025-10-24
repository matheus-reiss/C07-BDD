package com.br.inatel.service;

import com.br.inatel.model.Treino;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface TreinoService {
    Treino criar(String titulo, LocalDate createdAt, boolean ativo, long idInstrutor, long idAluno) throws BusinessException;
    Treino atualizar(long id, String titulo, LocalDate createdAt, boolean ativo, long idInstrutor, long idAluno)
            throws NotFoundException, BusinessException;

    Treino buscarPorId(long id) throws NotFoundException;
    List<Treino> listarTodos();
    void excluir(long id) throws NotFoundException, BusinessException;

    void alterarAtivo(long id, boolean ativo) throws NotFoundException;
    List<Treino> listarPorAluno(long idAluno);
    List<Treino> listarPorInstrutor(long idInstrutor);
}
