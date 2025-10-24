package com.br.inatel.service;

import com.br.inatel.model.Plano;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface PlanoService {
    Plano criar(String nome, BigDecimal preco, int duracaoMeses) throws BusinessException;
    Plano atualizar(long id, String nome, BigDecimal preco, int duracaoMeses) throws NotFoundException, BusinessException;
    Plano buscarPorId(long id) throws NotFoundException;
    List<Plano> listarTodos();
    void excluir(long id) throws NotFoundException, BusinessException;

    List<Plano> buscarPorNome(String termo);
    List<Plano> buscarPorFaixaPreco(BigDecimal minimo, BigDecimal maximo);
}
