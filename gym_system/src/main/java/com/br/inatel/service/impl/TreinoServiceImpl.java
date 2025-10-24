package com.br.inatel.service.impl;

import com.br.inatel.dao.AlunoDao;
import com.br.inatel.dao.InstrutorDao;
import com.br.inatel.dao.TreinoDao;
import com.br.inatel.model.Aluno;
import com.br.inatel.model.Instrutor;
import com.br.inatel.model.Treino;
import com.br.inatel.service.TreinoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TreinoServiceImpl implements TreinoService {

    private final TreinoDao treinoDao;
    private final InstrutorDao instrutorDao;
    private final AlunoDao alunoDao;

    public TreinoServiceImpl(TreinoDao treinoDao, InstrutorDao instrutorDao, AlunoDao alunoDao) {
        this.treinoDao = Objects.requireNonNull(treinoDao, "treinoDao não pode ser null");
        this.instrutorDao = Objects.requireNonNull(instrutorDao, "instrutorDao não pode ser null");
        this.alunoDao = Objects.requireNonNull(alunoDao, "alunoDao não pode ser null");
    }

    // ===== CREATE =====
    @Override
    public Treino criar(String titulo, LocalDate createdAt, boolean ativo, long idInstrutor, long idAluno)
            throws BusinessException {
        titulo = validarTitulo(titulo);
        validarIdPositivo(idInstrutor, "idInstrutor");
        validarIdPositivo(idAluno, "idAluno");
        createdAt = normalizarCreatedAt(createdAt);

        try {
            if (!instrutorDao.existePorId(idInstrutor)) {
                throw new NotFoundException("Instrutor não encontrado: " + idInstrutor);
            }
            if (!alunoDao.existePorId(idAluno)) {
                throw new NotFoundException("Aluno não encontrado: " + idAluno);
            }

            Treino t = new Treino();
            t.setTitulo(titulo);
            t.setCreatedAt(createdAt);
            t.setAtivo(ativo);

            Instrutor i = new Instrutor(); i.setId(idInstrutor); t.setInstrutor(i);
            Aluno a = new Aluno(); a.setId(idAluno); t.setAluno(a);

            treinoDao.insert(t);
            return t;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao criar treino: ", e);
        }
    }

    // ===== UPDATE =====
    @Override
    public Treino atualizar(long id, String titulo, LocalDate createdAt, boolean ativo, long idInstrutor, long idAluno)
            throws NotFoundException, BusinessException {
        validarIdPositivo(id, "id");
        titulo = validarTitulo(titulo);
        validarIdPositivo(idInstrutor, "idInstrutor");
        validarIdPositivo(idAluno, "idAluno");
        createdAt = normalizarCreatedAt(createdAt);

        try {
            Treino existente = treinoDao.buscarPorId(id);
            if (existente == null) throw new NotFoundException("Treino não encontrado: " + id);

            if (!instrutorDao.existePorId(idInstrutor)) {
                throw new NotFoundException("Instrutor não encontrado: " + idInstrutor);
            }
            if (!alunoDao.existePorId(idAluno)) {
                throw new NotFoundException("Aluno não encontrado: " + idAluno);
            }

            existente.setTitulo(titulo);;
            existente.setCreatedAt(createdAt);
            existente.setAtivo(ativo);

            Instrutor i = new Instrutor(); i.setId(idInstrutor); existente.setInstrutor(i);
            Aluno a = new Aluno(); a.setId(idAluno); existente.setAluno(a);

            int rows = treinoDao.atualizar(existente);
            if (rows == 0) throw new BusinessException("Atualização não aplicada (id=" + id + ").");
            return existente;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao atualizar treino: " + e.getMessage());
        }
    }

    // ===== READ =====
    @Override
    public Treino buscarPorId(long id) throws NotFoundException {
        validarIdPositivo(id, "id");
        try {
            Treino t = treinoDao.buscarPorId(id);
            if (t == null) throw new NotFoundException("Treino não encontrado: " + id);
            return t;
        } catch (SQLException e) {
            throw new BusinessException("Falha ao buscar treino: " + e);
        }
    }

    @Override
    public List<Treino> listarTodos() {
        try {
            return treinoDao.listarTodos();
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar treinos: " + e.getMessage());
        }
    }

    // ===== DELETE =====
    @Override
    public void excluir(long id) throws NotFoundException, BusinessException {
      validarIdPositivo(id, "id");
      try {
          int rows = treinoDao.deletarPorId(id);
          if (rows == 0) throw new NotFoundException("Treino não encontrado: " + id);
      } catch (SQLException e){
          throw new BusinessException("Falha ao excluir treino.", e);
      }
    }

    // ===== PATCH =====
    @Override
    public void alterarAtivo(long id, boolean ativo) throws NotFoundException {
        validarIdPositivo(id, "id");
        try {
            int rows = treinoDao.alterarAtivo(id, ativo);
            if (rows == 0) throw new BusinessException("Treino não encontrado: " + id);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao alterar status do treino: ", e);
        }
    }

    // ===== FILTERS =====
    @Override
    public List<Treino> listarPorAluno(long idAluno) {
        validarIdPositivo(idAluno, "idAluno");
        try {
            return treinoDao.listarPorAluno(idAluno);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar treinos do aluno: " + e.getMessage());
        }
    }

    @Override
    public List<Treino> listarPorInstrutor(long idInstrutor) {
        validarIdPositivo(idInstrutor, "idInstrutor");
        try {
            return treinoDao.listarPorInstrutor(idInstrutor);
        } catch (SQLException e) {
            throw new BusinessException("Falha ao listar treinos do instrutor: " + e.getMessage());
        }
    }

    // ===== validações/helpers =====
    private void validarIdPositivo(long id, String campo) {
        if (id <= 0) throw new BusinessException(campo + " deve ser positivo.");
    }

    private String validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) throw new BusinessException("Título é obrigatório.");
        String t = titulo.trim();
        if (t.length() < 3) throw new BusinessException("Título deve ter ao menos 3 caracteres.");
        return t;
    }

    private LocalDate normalizarCreatedAt(LocalDate createdAt) {
        // se vier null, usa hoje; se quiser, pode impedir datas futuras
        if (createdAt == null) return LocalDate.now();
        // if (createdAt.isAfter(LocalDate.now())) throw new BusinessException("createdAt não pode ser no futuro.");
        return createdAt;
    }
}
