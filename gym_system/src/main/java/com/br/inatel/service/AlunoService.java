package com.br.inatel.service;


import com.br.inatel.model.Aluno;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;
public interface AlunoService {
    Aluno criar(String nome, LocalDate nascimento, String telefone) throws BusinessException;
    Aluno atualizar(long id, String nome, LocalDate nascimento, String telefone) throws NotFoundException, BusinessException;
    void ativar(long id) throws NotFoundException;
    void desativar(long id) throws NotFoundException;
    Aluno buscarPorId(long id) throws NotFoundException;
    List<Aluno> listarTodos();

    List<Aluno> buscarPorNome(String termo);;
}
