package com.br.inatel.service.impl;

import com.br.inatel.dao.AlunoDao;
import com.br.inatel.dao.AssinaturaDao;
import com.br.inatel.dao.PlanoDao;
import com.br.inatel.model.Aluno;
import com.br.inatel.model.Assinatura;
import com.br.inatel.model.Plano;
import com.br.inatel.model.enums.AssinaturaStatus;
import com.br.inatel.service.AssinaturaService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class AssinaturaServiceImpl implements AssinaturaService {

    private final AssinaturaDao assinaturaDao;
    private final AlunoDao alunoDao;
    private final PlanoDao planoDao;

    public AssinaturaServiceImpl(AssinaturaDao assinaturaDao, AlunoDao alunoDao, PlanoDao planoDao) {
        this.assinaturaDao = Objects.requireNonNull(assinaturaDao, "assinaturaDao não pode ser null");
        this.alunoDao = Objects.requireNonNull(alunoDao, "alunoDao não pode ser null");
        this.planoDao = Objects.requireNonNull(planoDao, "planoDao não pode ser null");
    }

    // ========= CREATE =========
    @Override
    public Assinatura criar(long idAluno, long idPlano, LocalDate dataInicio, LocalDate dataFim)
            throws BusinessException {
        validarIdPositivo(idAluno, "idAluno");
        validarIdPositivo(idPlano, "idPlano");
        validarDatas(dataInicio, dataFim);

        AssinaturaStatus status = AssinaturaStatus.ATIVA;

        try {
            if (!alunoExiste(idAluno)) {
                throw new NotFoundException("Aluno não encontrado: " + idAluno);
            }
            if (!planoExiste(idPlano)) {
                throw new NotFoundException("Plano não encontrado: " + idPlano);
            }

            //  Evita mais de uma assinatura ATIVA por aluno:
             if (!assinaturaDao.listarAtivasPorAluno(idAluno).isEmpty()) {
                 throw new BusinessException("Já existe uma assinatura ATIVA para o aluno " + idAluno + ".");
             }

            Assinatura a = new Assinatura();
            a.setAluno(refAluno(idAluno));
            a.setPlano(refPlano(idPlano));
            a.setDataInicio(dataInicio);
            a.setDataFim(dataFim);
            a.setStatus(status);

            assinaturaDao.insert(a);
            return a;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao criar assinatura: " + e.getMessage());
        }
    }

    // ========= UPDATE =========
    @Override
    public Assinatura atualizar(long id, LocalDate dataInicio, LocalDate dataFim, AssinaturaStatus status)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        validarDatas(dataInicio, dataFim);
        if (status == null) {
            throw new BusinessException("Status é obrigatório.");
        }

        try {
            Assinatura existente = assinaturaDao.buscarPorId(id);
            if (existente == null) {
                throw new NotFoundException("Assinatura não encontrada: " + id);
            }

            existente.setDataInicio(dataInicio);
            existente.setDataFim(dataFim);
            existente.setStatus(status);

            boolean ok = assinaturaDao.atualizar(existente);
            if (!ok) {
                throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            }

            return existente;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar assinatura: " + e.getMessage());
        }
    }

    // ========= READ =========
    @Override
    public List<Assinatura> listarTodas() {
        try {
            return assinaturaDao.listarTodas();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar assinaturas: " + e.getMessage());
        }
    }

    // ========= SOFT delete =========
    @Override
    public void excluir(long id) throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        try {
            var atual = assinaturaDao.buscarPorId(id);
            if (atual == null) {
                throw new NotFoundException("Assinatura não encontrada: " + id);
            }

            AssinaturaStatus st = atual.getStatus();
            if (st != AssinaturaStatus.ATIVA) {
                String msg = switch (st) {
                    case INATIVA   -> "Você não pode cancelar uma assinatura inativa.";
                    case ATRASADA  -> "Você não pode cancelar uma assinatura atrasada.";
                    case CANCELADA -> "Assinatura já está cancelada.";
                    default        -> "Status inválido para cancelamento: " + st;
                };
                throw new BusinessException(msg);
            }
            int rows = assinaturaDao.softDelete(id);
            if (rows == 0) {
                throw new BusinessException("Não foi possível cancelar: a assinatura não está mais ativa.");
            }

        } catch (SQLException e) {
            throw new BusinessException("Falha ao cancelar assinatura: " + e.getMessage());
        }
    }

    // ========= PATCHES =========
    @Override
    public void alterarStatus(long id, AssinaturaStatus novoStatus) throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        if (novoStatus == null) throw new BusinessException("Status é obrigatório.");

        try {
            if (!assinaturaDao.existePorId(id)) {
                throw new NotFoundException("Assinatura não encontrada: " + id);
            }
            boolean ok = assinaturaDao.alterarStatus(id, novoStatus);
            if (!ok) {
                throw new BusinessException("Não foi possível alterar o status (id=" + id + ").");
            }
        } catch (SQLException e) {
            throw new BusinessException("Falha ao alterar status: " + e.getMessage());
        }
    }


    // ========= CONSULTAS =========
    @Override
    public List<Assinatura> listarPorAluno(long idAluno) {
        validarIdPositivo(idAluno, "idAluno");
        try {
            return assinaturaDao.listarPorAluno(idAluno);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar assinaturas do aluno: " + e.getMessage());
        }
    }


    @Override
    public List<Assinatura> listarPorPlano(long idPlano) {
        validarIdPositivo(idPlano, "idPlano");
        try {
            return assinaturaDao.listarPorPlano(idPlano);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar assinaturas do plano: " + e.getMessage());
        }
    }

    // ========= Helpers =========
    private boolean alunoExiste(long idAluno) throws SQLException {
        return alunoDao.existePorId(idAluno);
    }

    private boolean planoExiste(long idPlano) throws SQLException {
        return planoDao.existePorId(idPlano);
    }

    private Aluno refAluno(long idAluno) {
        Aluno a = new Aluno();
        a.setId(idAluno);
        return a;
    }

    private Plano refPlano(long idPlano) {
        Plano p = new Plano();
        p.setId(idPlano);
        return p;
    }

    private void validarIdPositivo(long id, String campo) {
        if (id <= 0) throw new BusinessException(campo + " deve ser positivo.");
    }

    private void validarDatas(LocalDate inicio, LocalDate fim) {
        if (inicio == null) throw new BusinessException("dataInicio é obrigatória.");
        if (fim != null && fim.isBefore(inicio)) {
            throw new BusinessException("dataFim não pode ser anterior a dataInicio.");
        }
    }
}
