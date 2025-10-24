package com.br.inatel.service.impl;

import com.br.inatel.dao.AssinaturaDao;
import com.br.inatel.dao.PagamentoDao;
import com.br.inatel.model.Assinatura;
import com.br.inatel.model.Pagamento;
import com.br.inatel.model.enums.PagamentoStatus;
import com.br.inatel.service.PagamentoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoDao pagamentoDao;
    private final AssinaturaDao assinaturaDao;

    public PagamentoServiceImpl(PagamentoDao pagamentoDao, AssinaturaDao assinaturaDao) {
        this.pagamentoDao = Objects.requireNonNull(pagamentoDao, "pagamentoDao não pode ser null");
        this.assinaturaDao = Objects.requireNonNull(assinaturaDao, "assinaturaDao não pode ser null");
    }

    // ========= CREATE =========
    @Override
    public Pagamento criar(long idAssinatura, LocalDate competencia, BigDecimal valor,
                           LocalDate dataVencimento, PagamentoStatus status) throws BusinessException {
        validarIdPositivo(idAssinatura, "idAssinatura");
        validarCompetencia(competencia);
        validarValor(valor);
        validarVencimento(competencia, dataVencimento);

        if (status == null) status = PagamentoStatus.PENDENTE;

        try {
            if (!assinaturaDao.existePorId(idAssinatura)) {
                throw new NotFoundException("Assinatura não encontrada: " + idAssinatura);
            }

            Pagamento p = new Pagamento();
            p.setCompetencia(competencia);
            p.setValor(valor);
            p.setDataVencimento(dataVencimento);
            p.setStatus(status);
            p.setDataPagamento(null);

            Assinatura a = new Assinatura();
            a.setId(idAssinatura);
            p.setAssinatura(a);

            pagamentoDao.insert(p);
            return p;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao criar pagamento: " + e.getMessage());
        }
    }

    // ========= UPDATE =========
    @Override
    public Pagamento atualizar(long id, long idAssinatura, LocalDate competencia, BigDecimal valor,
                               LocalDate dataVencimento, PagamentoStatus status, LocalDate dataPagamento)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        validarIdPositivo(idAssinatura, "idAssinatura");
        validarCompetencia(competencia);
        validarValor(valor);
        validarVencimento(competencia, dataVencimento);
        if (status == null) throw new BusinessException("Status é obrigatório.");

        try {
            Pagamento existente = pagamentoDao.buscarPorId(id);
            if (existente == null) throw new NotFoundException("Pagamento não encontrado: " + id);

            if (!assinaturaDao.existePorId(idAssinatura)) {
                throw new NotFoundException("Assinatura não encontrada: " + idAssinatura);
            }

            if (existente.getStatus() == PagamentoStatus.PAGO) {
                if (status != PagamentoStatus.PAGO) {
                    throw new BusinessException("Pagamento já está PAGO — não é permitido alterar para outro status.");
                }
                if (dataPagamento == null) {
                    dataPagamento = existente.getDataPagamento() != null ? existente.getDataPagamento() : LocalDate.now();
                }
            }

            existente.setCompetencia(competencia);
            existente.setValor(valor);
            existente.setDataVencimento(dataVencimento);
            existente.setStatus(status);
            existente.setDataPagamento(dataPagamento);

            Assinatura a = new Assinatura();
            a.setId(idAssinatura);
            existente.setAssinatura(a);

            boolean ok = pagamentoDao.atualizar(existente);
            if (!ok) throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            return existente;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar pagamento: " + e.getMessage());
        }
    }

    // ========= READ =========
    @Override
    public Pagamento buscarPorId(long id) throws NotFoundException {
        validarIdPositivo(id, "id");
        try {
            Pagamento p = pagamentoDao.buscarPorId(id);
            if (p == null) throw new NotFoundException("Pagamento não encontrado: " + id);
            return p;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar pagamento: " + e.getMessage());
        }
    }

    @Override
    public List<Pagamento> listarTodos() {
        try {
            return pagamentoDao.listarTodos();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar pagamentos: " + e.getMessage());
        }
    }

    // ========= DELETE =========
    @Override
    public void excluir(long id) throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        try {
            Pagamento p = pagamentoDao.buscarPorId(id);
            if (p == null) throw new NotFoundException("Pagamento não encontrado: " + id);

            // Não excluir pagamento ja PAGO
            if (p.getStatus() == PagamentoStatus.PAGO) {
                throw new BusinessException("Não é permitido excluir um pagamento já pago (id=" + id + ").");
            }

            boolean ok = pagamentoDao.deletarPorId(id);
            if (!ok) throw new BusinessException("Exclusão não aplicada (id=" + id + ").");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao excluir pagamento: " + e.getMessage());
        }
    }

    // ========= CONSULTAS =========
    @Override
    public List<Pagamento> listarPorAssinatura(long idAssinatura) {
        validarIdPositivo(idAssinatura, "idAssinatura");
        try {
            return pagamentoDao.listarPorAssinatura(idAssinatura);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar pagamentos da assinatura: " + e.getMessage());
        }
    }

    @Override
    public List<Pagamento> listarPorStatus(PagamentoStatus status) {
        if (status == null) throw new BusinessException("Status é obrigatório.");
        try {
            return pagamentoDao.listarPorStatus(status);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar por status: " + e.getMessage());
        }
    }

    @Override
    public List<Pagamento> listarVencidos() {
        try {
            return pagamentoDao.listarVencidos();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar vencidos: " + e.getMessage());
        }
    }

    // ========= PATCHES =========
    @Override
    public void alterarStatus(long id, PagamentoStatus status) throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        if (status == null) throw new BusinessException("Status é obrigatório.");

        try {
            Pagamento atual = pagamentoDao.buscarPorId(id);
            if (atual == null) throw new NotFoundException("Pagamento não encontrado: " + id);

            // Se já está PAGO, não deixa mudar para outro
            if (atual.getStatus() == PagamentoStatus.PAGO && status != PagamentoStatus.PAGO) {
                throw new BusinessException("Pagamento já está PAGO — não é permitido alterar para outro status.");
            }

            boolean ok = pagamentoDao.alterarStatus(id, status);
            if (!ok) throw new BusinessException("Não foi possível alterar o status (id=" + id + ").");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao alterar status: " + e.getMessage());
        }
    }

    @Override
    public void registrarPagamento(long id, LocalDate dataPagamento) throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        if (dataPagamento == null) dataPagamento = LocalDate.now();

        try {
            Pagamento atual = pagamentoDao.buscarPorId(id);
            if (atual == null) throw new NotFoundException("Pagamento não encontrado: " + id);

            if (atual.getStatus() == PagamentoStatus.PAGO) {
                throw new BusinessException("Pagamento já está PAGO.");
            }

            boolean ok = pagamentoDao.registrarPagamento(id, dataPagamento);
            if (!ok) throw new BusinessException("Não foi possível registrar o pagamento (id=" + id + ").");
        } catch (SQLException e) {
            throw new BusinessException("Falha ao registrar pagamento: " + e.getMessage());
        }
    }

    // ========= validações =========
    private void validarIdPositivo(long id, String campo) {
        if (id <= 0) throw new BusinessException(campo + " deve ser positivo.");
    }

    private void validarCompetencia(LocalDate competencia) {
        if (competencia == null) throw new BusinessException("Competência é obrigatória.");
    }

    private void validarVencimento(LocalDate competencia, LocalDate vencimento) {
        if (vencimento == null) throw new BusinessException("Data de vencimento é obrigatória.");
        if (competencia != null && vencimento.isBefore(competencia)) {
            throw new BusinessException("Vencimento não pode ser anterior à competência.");
        }
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new BusinessException("Valor deve ser positivo.");
        }
    }
}
