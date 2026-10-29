package com.example.cashme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import android.widget.Toast;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import com.example.cashme.model.Lancamento;

import com.example.cashme.repository.LancamentoRepository;
import com.example.cashme.ui.LancamentoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.res.Configuration;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabAdd;
    LancamentoAdapter adapter;
    LancamentoRepository repository;
    
    // Variáveis do Painel de Saldo
    TextView tvBalanceValue;
    TextView tvIncomeValue;
    TextView tvExpenseValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Verifica se o usuário está logado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        
        // Mudar o nome do topo da tela para o nome de quem logou!
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            tvWelcome.setText("Olá, " + currentUser.getDisplayName());
        }

        // --- Configuração da Ferramenta de Mudar Tema ---
        ImageView ivThemeToggle = findViewById(R.id.ivProfile);
        ivThemeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checa qual é o tema atual do celular
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    // Se for noite, muda pro claro
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    // Se for claro, muda pra noite
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });

        // --- Configuração do Botão de Logout ---
        ImageView ivLogout = findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Desloga do Firebase
                FirebaseAuth.getInstance().signOut();
                
                // Manda de volta para a tela de Login e fecha a MainActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        // Navegação do FAB
        fabAdd = findViewById(R.id.fabAdd);
        
        // Binding do Painel de Saldo
        tvBalanceValue = findViewById(R.id.tvBalanceValue);
        tvIncomeValue = findViewById(R.id.tvIncomeValue);
        tvExpenseValue = findViewById(R.id.tvExpenseValue);

        repository = new LancamentoRepository(); // Inicializa o repositório

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NovoLancamentoActivity.class);
                startActivity(intent);
            }
        });

        //Encontrar o RecyclerView no XML
        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);

        //Dizer como a lista deve se comportar (Vertical? Horizontal? Grade?)
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        /**
         * Contratar o gerente (Adapter) que criamos, passando a lista falsa que fizemos na Fase 3 e a própria tela (this)
         * E agora passando também um ouvinte para o clique longo de exclusão
         */
        adapter = new LancamentoAdapter(LancamentoRepository.lista, this, new LancamentoAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Lancamento lancamento) {
                confirmarExclusao(lancamento);
            }
        });

        rvTransactions.setAdapter(adapter);
    }
    
    /**
     * Exibe um alerta de confirmação antes de excluir
     */
    private void confirmarExclusao(Lancamento lancamento) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Lançamento")
                .setMessage("Deseja apagar '" + lancamento.getDescricao() + "' no valor de R$ " + lancamento.getValor() + "?")
                .setPositiveButton("Sim, excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletarLancamento(lancamento);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Pede ao repositório para deletar e, em caso de sucesso, atualiza a tela
     */
    private void deletarLancamento(Lancamento lancamento) {
        repository.deletar(lancamento.getId(), new LancamentoRepository.OnDadosCarregadosListener() {
            @Override
            public void onSucesso() {
                Toast.makeText(MainActivity.this, "Apagado com sucesso!", Toast.LENGTH_SHORT).show();
                // A lista local já foi alterada no repositório, então só avisamos a tela:
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                calcularEAtualizarSaldo();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(MainActivity.this, "Erro ao apagar: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Em vez de só avisar o adapter, nós baixamos os dados reais do Firebase toda vez que a tela volta!
        repository.carregarTodos(new LancamentoRepository.OnDadosCarregadosListener() {
            @Override
            public void onSucesso() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                calcularEAtualizarSaldo(); // Atualiza os números no topo da tela!
            }

            @Override
            public void onErro(String mensagem) {
                // Em um app real, mostraríamos um Snackbar vermelho aqui
            }
        });
    }

    /**
     * Calcula a soma das receitas e despesas e atualiza o visual da tela
     */
    private void calcularEAtualizarSaldo() {
        double totalReceitas = 0.0;
        double totalDespesas = 0.0;

        // Percorre a lista que veio do banco de dados
        for (Lancamento l : LancamentoRepository.lista) {
            if (l.isReceita()) {
                totalReceitas += l.getValor();
            } else {
                totalDespesas += l.getValor();
            }
        }

        double saldoTotal = totalReceitas - totalDespesas;

        // Formata para o padrão brasileiro de moeda
        java.text.DecimalFormat df = new java.text.DecimalFormat("R$ #,##0.00");
        df.setDecimalFormatSymbols(new java.text.DecimalFormatSymbols(new java.util.Locale("pt", "BR")));

        // Atualiza a tela
        tvIncomeValue.setText(df.format(totalReceitas));
        tvExpenseValue.setText(df.format(totalDespesas));
        tvBalanceValue.setText(df.format(saldoTotal));

        // Se o saldo ficar negativo, pinta de vermelho. Se ficar positivo/zero, deixa na cor neutra.
        if (saldoTotal < 0) {
            tvBalanceValue.setTextColor(ContextCompat.getColor(this, R.color.red_expense));
        } else {
            tvBalanceValue.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }
    }
}