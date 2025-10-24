package com.br.inatel.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Plano {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer duracaoMeses;

    private List<Assinatura> assinaturas = new ArrayList<>();

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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getDuracaoMeses() {
        return duracaoMeses;
    }

    public void setDuracaoMeses(Integer duracaoMeses) {
        this.duracaoMeses = duracaoMeses;
    }
}
