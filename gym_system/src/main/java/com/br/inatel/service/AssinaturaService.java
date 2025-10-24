package com.br.inatel.service;

import com.br.inatel.model.Assinatura;
import com.br.inatel.model.enums.AssinaturaStatus;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface AssinaturaService {
    Assinatura criar(long idAluno, long idPlano, LocalDate dataInicio, LocalDate dataFim) throws BusinessException;

    Assinatura atualizar(long id, LocalDate dataInicio, LocalDate dataFim, AssinaturaStatus status)
            throws NotFoundException, BusinessException;

    List<Assinatura> listarTodas();
    void excluir(long id) throws NotFoundException, BusinessException;

    void alterarStatus(long id, AssinaturaStatus novoStatus) throws NotFoundException, BusinessException;



    List<Assinatura> listarPorAluno(long idAluno);
    List<Assinatura> listarPorPlano(long idPlano);


}
