package com.example.cashme.model;

import java.util.UUID;

public class Lancamento {

    private String id;
    private String descricao;
    private double valor;
    private String categoria;
    private String data;
    private boolean isReceita;

    public Lancamento(String descricao, double valor, String categoria, String data, boolean isReceita) {
        this.id = UUID.randomUUID().toString();
        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.data = data;
        this.isReceita = isReceita;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isReceita() {
        return isReceita;
    }

    public void setReceita(boolean receita) {
        isReceita = receita;
    }
}
