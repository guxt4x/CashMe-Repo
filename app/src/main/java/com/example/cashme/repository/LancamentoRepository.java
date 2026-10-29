package com.example.cashme.repository;

import android.util.Log;

import com.example.cashme.model.Lancamento;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LancamentoRepository {

    // Instância do banco de dados na nuvem
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference lancamentosRef = db.collection("lancamentos");

    // Mantemos uma lista local em memória para servir a UI rapidamente após o carregamento
    public static List<Lancamento> lista = new ArrayList<>();

    // Callback (Interface) para avisar a tela quando os dados chegarem da internet
    public interface OnDadosCarregadosListener {
        void onSucesso();
        void onErro(String mensagem);
    }

    /**
     * Salva um lançamento novo na nuvem (Firestore) e adiciona na lista local.
     */
    public void salvar(Lancamento lancamento, final OnDadosCarregadosListener listener) {
        // Usa o próprio ID gerado pela nossa classe como ID do documento no Firebase
        lancamentosRef.document(lancamento.getId())
                .set(lancamento)
                .addOnSuccessListener(aVoid -> {
                    lista.add(lancamento); // Adiciona na memória local
                    listener.onSucesso();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao salvar", e);
                    listener.onErro(e.getMessage());
                });
    }

    /**
     * Busca todos os lançamentos do Firebase, ordena pela data de criação e atualiza a lista local.
     */
    public void carregarTodos(final OnDadosCarregadosListener listener) {
        lancamentosRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear(); // Limpa a lista antiga
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // O Firebase pega o JSON da nuvem e transforma na nossa classe Lancamento automaticamente!
                        Lancamento lancamento = document.toObject(Lancamento.class);
                        if (lancamento != null) {
                            lista.add(lancamento);
                        }
                    }
                    listener.onSucesso();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao carregar", e);
                    listener.onErro(e.getMessage());
                });
    }
    
    /**
     * Deleta um lançamento do Firebase usando o seu ID
     */
    public void deletar(String idLancamento, final OnDadosCarregadosListener listener) {
        lancamentosRef.document(idLancamento)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove da lista local (código compatível com API antiga)
                    for (int i = 0; i < lista.size(); i++) {
                        if (lista.get(i).getId().equals(idLancamento)) {
                            lista.remove(i);
                            break;
                        }
                    }
                    listener.onSucesso();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao deletar", e);
                    listener.onErro(e.getMessage());
                });
    }
}
