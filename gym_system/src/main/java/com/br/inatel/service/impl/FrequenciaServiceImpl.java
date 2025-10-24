package com.br.inatel.service.impl;

import com.br.inatel.dao.AlunoDao;
import com.br.inatel.dao.FrequenciaDao;
import com.br.inatel.model.Aluno;
import com.br.inatel.model.Frequencia;
import com.br.inatel.service.FrequenciaService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class FrequenciaServiceImpl implements FrequenciaService {

    private final FrequenciaDao dao;
    private final AlunoDao alunoDao;

    public FrequenciaServiceImpl(FrequenciaDao dao, AlunoDao alunoDao) {
        this.dao = Objects.requireNonNull(dao, "dao não pode ser null");
        this.alunoDao = Objects.requireNonNull(alunoDao, "alunoDao não pode ser null");
    }

    // ===== CREATE =====
    @Override
    public Frequencia registrarCheckin(long idAluno, LocalDate data) throws BusinessException {
        validarIdPositivo(idAluno);
        LocalDate dia = validarData(data);

        try {
            // Um check-in por dia por aluno
            if (dao.existeCheckinNoDia(idAluno, dia)) {
                throw new BusinessException("Já existe check-in para o aluno " + idAluno + " em " + dia + ".");
            }
            if (!alunoDao.existePorId(idAluno)) {
                throw new NotFoundException("Aluno não encontrado: " + idAluno);
            }

            Frequencia f = new Frequencia();
            Aluno a = new Aluno();
            a.setId(idAluno);
            f.setAluno(a);
            f.setDataCheckin(dia);

            dao.insert(f);
            return f;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao registrar check-in: " + e.getMessage());
        }
    }

    // ===== UPDATE =====
    @Override
    public Frequencia atualizar(long id, long idAluno, LocalDate data)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id);
        validarIdPositivo(idAluno);
        LocalDate dia = validarData(data);

        try {
            Frequencia existente = dao.buscarPorId(id);
            if (existente == null) {
                throw new NotFoundException("Frequência não encontrada: " + id);
            }

            boolean dataMudou = existente.getDataCheckin() == null || !existente.getDataCheckin().equals(dia);
            boolean alunoMudou = existente.getAluno() == null || existente.getAluno().getId() != idAluno;

            if (dataMudou || alunoMudou) {
                if (dao.existeCheckinNoDia(idAluno, dia)) {
                    throw new BusinessException("Já existe check-in para o aluno " + idAluno + " em " + dia + ".");
                }
            }

            Aluno a = new Aluno();
            a.setId(idAluno);
            existente.setAluno(a);
            existente.setDataCheckin(dia);

            boolean ok = dao.atualizar(existente);
            if (!ok) {
                throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            }
            return existente;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar frequência: " + e.getMessage());
        }
    }

    // ===== READ =====
    @Override
    public List<Frequencia> listarTodas() {
        try {
            return dao.listarTodas();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar frequências: " + e.getMessage());
        }
    }


    // ===== CONSULTAS =====
    @Override
    public List<Frequencia> listarPorAluno(long idAluno) {
        validarIdPositivo(idAluno);
        try {
            return dao.listarPorAluno(idAluno);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar frequências do aluno: " + e.getMessage());
        }
    }

    @Override
    public List<Frequencia> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new BusinessException("Datas de início e fim são obrigatórias.");
        }
        if (fim.isBefore(inicio)) {
            throw new BusinessException("Data final não pode ser anterior à data inicial.");
        }
        try {
            return dao.listarPorPeriodo(inicio, fim);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar por período: " + e.getMessage());
        }
    }

    @Override
    public Frequencia buscarUltimoCheckinPorAluno(long idAluno) {
        validarIdPositivo(idAluno);
        try {
            return dao.buscarUltimoCheckinPorAluno(idAluno);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar último check-in: " + e.getMessage());
        }
    }

    // ===== validações =====
    private void validarIdPositivo(long id) {
        if (id <= 0) throw new BusinessException("Id deve ser positivo.");
    }

    private LocalDate validarData(LocalDate data) {
        if (data == null) throw new BusinessException("Data é obrigatória.");
        if (data.isAfter(LocalDate.now())) throw new BusinessException("Data de check-in não pode ser no futuro.");
        return data;
    }
}
