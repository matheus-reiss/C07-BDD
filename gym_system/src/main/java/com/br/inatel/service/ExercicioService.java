package com.br.inatel.service;

import com.br.inatel.model.Exercicio;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.util.List;

public interface ExercicioService {
    Exercicio criar(String nome, String grupoMuscular) throws BusinessException;
    Exercicio atualizar(long id, String nome, String grupoMuscular) throws NotFoundException, BusinessException;
    Exercicio buscarPorId(long id) throws NotFoundException;
    List<Exercicio> listarTodos();
    void excluir(long id) throws NotFoundException, BusinessException;

    List<Exercicio> buscarPorNome(String termo);
    List<Exercicio> buscarPorGrupoMuscular(String grupo);

    boolean possuiVinculos(long id);
}
