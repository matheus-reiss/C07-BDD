package com.br.inatel.model;

import com.br.inatel.model.enums.AssinaturaStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Assinatura {
    private Long id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private AssinaturaStatus status;

    private Aluno aluno;
    private Plano plano;
    private List<Pagamento> pagamentos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public AssinaturaStatus getStatus() {
        return status;
    }

    public void setStatus(AssinaturaStatus status) {
        this.status = status;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Plano getPlano() {
        return plano;
    }

    public void setPlano(Plano plano) {
        this.plano = plano;
    }
}
