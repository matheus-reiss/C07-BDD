package com.br.inatel.service;

import com.br.inatel.model.Pagamento;
import com.br.inatel.model.enums.PagamentoStatus;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PagamentoService {
    Pagamento criar(long idAssinatura, LocalDate competencia, BigDecimal valor, LocalDate dataVencimento, PagamentoStatus status)
            throws BusinessException;

    Pagamento atualizar(long id, long idAssinatura, LocalDate competencia, BigDecimal valor, LocalDate dataVencimento, PagamentoStatus status, LocalDate dataPagamento)
            throws NotFoundException, BusinessException;

    Pagamento buscarPorId(long id) throws NotFoundException;
    List<Pagamento> listarTodos();
    void excluir(long id) throws NotFoundException, BusinessException;

    List<Pagamento> listarPorAssinatura(long idAssinatura);
    List<Pagamento> listarPorStatus(PagamentoStatus status);
    List<Pagamento> listarVencidos(); // não pagos e vencidos


    void alterarStatus(long id, PagamentoStatus status) throws NotFoundException, BusinessException;
    void registrarPagamento(long id, LocalDate dataPagamento) throws NotFoundException, BusinessException;
}
