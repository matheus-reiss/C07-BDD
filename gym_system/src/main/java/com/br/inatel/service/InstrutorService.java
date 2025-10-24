package com.br.inatel.service;

import com.br.inatel.model.Instrutor;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.util.List;

public interface InstrutorService {
    Instrutor criar(String nome, String cref) throws BusinessException;
    Instrutor atualizar(long id, String nome, String cref) throws NotFoundException, BusinessException;
    List<Instrutor> listarTodos();
    void excluir(long id) throws NotFoundException, BusinessException;

    List<Instrutor> buscarPorNome(String termo);
}
