package com.br.inatel.service;

import com.br.inatel.model.Frequencia;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface FrequenciaService {
    Frequencia registrarCheckin(long idAluno, LocalDate data) throws BusinessException; // garantir 1 por dia
    Frequencia atualizar(long id, long idAluno, LocalDate data) throws NotFoundException, BusinessException;
    List<Frequencia> listarTodas();

    List<Frequencia> listarPorAluno(long idAluno);
    List<Frequencia> listarPorPeriodo(LocalDate inicio, LocalDate fim);
    Frequencia buscarUltimoCheckinPorAluno(long idAluno);
}
