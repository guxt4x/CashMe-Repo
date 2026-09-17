package com.example.cashme.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashme.R;
import com.example.cashme.model.Lancamento;

import java.util.List;

public class LancamentoAdapter extends RecyclerView.Adapter<LancamentoAdapter.LancamentoViewHolder> {

    private List<Lancamento> listaLancamentos;
    private Context context;

    public LancamentoAdapter(List<Lancamento> listaLancamentos, Context context) {
        this.listaLancamentos = listaLancamentos;
        this.context = context;
    }

    @NonNull
    @Override
    public LancamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla (desenha) o layout XML
        View view = LayoutInflater.from(context).inflate(R.layout.item_lancamento, parent, false);
        // Entrega a view inflada para o ViewHolder guardar
        return new LancamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LancamentoViewHolder holder, int position) {
        // Pega o lançamento exato da lista
        Lancamento lancamento = listaLancamentos.get(position);

        //Define os textos normais
        holder.tvDescription.setText(lancamento.getDescricao());
        holder.tvDate.setText(lancamento.getData());

        //logica visivel para o dinheiro (receita x despesa)
        if (lancamento.isReceita()) {
            holder.tvValue.setText("+ R$ " + lancamento.getValor());
            //pinta o texto de verde
            holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.green_income));
        } else {
            holder.tvValue.setText("- R$ " + lancamento.getValor());
            //pinta o texto de vermelho
            holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.red_expense));
        }
    }

    @Override
    public int getItemCount() {
        return listaLancamentos.size();
    }

    public static class LancamentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvDate, tvValue;

        public LancamentoViewHolder(View itemView) {
            super(itemView);

            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvValue = itemView.findViewById(R.id.tvValue);

        }
    }
}
