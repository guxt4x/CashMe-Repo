package com.example.cashme.repository;

import com.example.cashme.model.Lancamento;

import java.util.ArrayList;
import java.util.List;

public class LancamentoRepository {

    public static List<Lancamento> lista = new ArrayList<>();

    static {
        lista.add(new Lancamento("Salário", 3500.00, "Trabalho", "01/09/2026", true));
        lista.add(new Lancamento("Supermercado", 450.00, "Alimentação", "02/09/2026", false));
        lista.add(new Lancamento("Conta de Luz", 120.00, "Contas", "03/09/2026", false));
    }
}
