package com.br.inatel.model;

import java.util.ArrayList;
import java.util.List;

public class Instrutor {
    private Long id;
    private String nome;
    private String cref;

    private List<Treino> treinos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }
}
