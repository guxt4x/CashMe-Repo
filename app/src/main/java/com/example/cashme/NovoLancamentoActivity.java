package com.example.cashme;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cashme.model.Lancamento;
import com.example.cashme.repository.LancamentoRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class NovoLancamentoActivity extends AppCompatActivity {

    ImageView ivBack;
    /**
     * variavel para controlar o que é receita e despesa
     */
    boolean isReceita = false;

    /**
     * declarando os componentes que vão ser manipulados
     */
    MaterialCardView cvIncome;
    MaterialCardView cvExpense;
    TextView tvIncomeText;
    TextView tvExpenseText;
    EditText etValue;
    AutoCompleteTextView actvCategory;
    EditText etDate;
    EditText etDescription;
    MaterialButton btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_novo_lancamento);
        ivBack = findViewById(R.id.ivBack);

        cvIncome = findViewById(R.id.cvIncome);
        cvExpense = findViewById(R.id.cvExpense);
        tvIncomeText = findViewById(R.id.tvIncomeText);
        tvExpenseText = findViewById(R.id.tvExpenseText);

        etValue = findViewById(R.id.etValue);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cvIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReceita = true;
                atualizarCorDosCards();
            }
        });

        cvExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReceita = false;
                atualizarCorDosCards();
            }
        });

        etValue.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Se o texto digitado for diferente do atual (para evitar loop infinito)
                if (!s.toString().equals(current)) {
                    // Tira o vigia rapidamente para poder formatar
                    etValue.removeTextChangedListener(this);

                    // 1. Limpa tudo que não for número (tira vírgula, ponto, etc)
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        // Transforma em Double e divide por 100 para ter os centavos
                        // (ex: digitou "150" -> 150 / 100 = 1.50)
                        double parsed = Double.parseDouble(cleanString) / 100.00;

                        // Formata no padrão brasileiro (1.000,00)
                        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
                        df.setDecimalFormatSymbols(new java.text.DecimalFormatSymbols(new java.util.Locale("pt", "BR")));
                        String formatted = df.format(parsed);

                        // Salva o estado atual e devolve pra tela
                        current = formatted;
                        etValue.setText(formatted);

                        // Joga o cursor do teclado lá pro final do texto
                        etValue.setSelection(formatted.length());
                    } else {
                        current = "";
                        etValue.setText("");
                    }

                    // Coloca o vigia de volta
                    etValue.addTextChangedListener(this);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        actvCategory = findViewById(R.id.actvCategory);
        // Lista de opçoes com vetor
        String[] categorias = {"Alimentação", "Trabalho", "Transporte", "Saúde", "Lazer", "Contas", "Outros"};
        // Criar um ArrayAdapter simples usando um layout nativo do Android
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categorias
        );
        // ligar o adapter ao campo
        actvCategory.setAdapter(adapterCategorias);

        // calendário para o usuario
        etDate = findViewById(R.id.etDate);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendario = Calendar.getInstance();
                int dia = calendario.get(Calendar.DAY_OF_MONTH);
                int mes = calendario.get(Calendar.MONTH);
                int ano = calendario.get(Calendar.YEAR);

                // cria a janela do calendario
                DatePickerDialog datePicker = new DatePickerDialog(
                        NovoLancamentoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                String dataFormatada = String.format("%02d/%02d/%04d", dayOfMonth, (month + 1), year);
                                etDate.setText(dataFormatada);
                            }
                        },
                ano, mes, dia
                );
                datePicker.show();
            }
        });

        // Criando o botão de salvar
        etDescription = findViewById(R.id.etDescription);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pegar os textos
                String descricaoStr = etDescription.getText().toString().trim();
                String categoriaStr = actvCategory.getText().toString().trim();
                String dataStr = etDate.getText().toString().trim();
                String valorStr = etValue.getText().toString().trim();

                // validação
                if (descricaoStr.isEmpty() || valorStr.isEmpty()) {
                    Toast.makeText(NovoLancamentoActivity.this,
                            "Preencha a descrição e o valor!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 3. Converter o Valor (Tirar a formatação bonita e transformar em Double)
                // Ex: "1.500,00" -> tira ponto -> "1500,00" -> troca virgula -> "1500.00" -> Double
                String valorLimpo = valorStr.replace(".", "").replace(",", ".");
                double valorDouble = Double.parseDouble(valorLimpo);

                Lancamento novoLancamento = new Lancamento(descricaoStr, valorDouble, categoriaStr, dataStr, isReceita);
                
                // Agora salvamos de verdade na nuvem!
                LancamentoRepository repo = new LancamentoRepository();
                repo.salvar(novoLancamento, new LancamentoRepository.OnDadosCarregadosListener() {
                    @Override
                    public void onSucesso() {
                        Toast.makeText(NovoLancamentoActivity.this, "Lançamento salvo na nuvem!", Toast.LENGTH_SHORT).show();
                        finish(); // Fecha a tela só DEPOIS de salvar na nuvem
                    }

                    @Override
                    public void onErro(String mensagem) {
                        Toast.makeText(NovoLancamentoActivity.this, "Erro ao salvar: " + mensagem, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * Método que muda a cor dos cards dependendo do valor da variável 'isReceita'
     */
    private void atualizarCorDosCards () {
        int colorGreen = ContextCompat.getColor(this, R.color.green_primary);
        int colorRed = ContextCompat.getColor(this, R.color.red_expense);
        int colorGray = ContextCompat.getColor(this, R.color.text_secondary);
        int colorWhite = ContextCompat.getColor(this, R.color.white);

        // Pega as cores "fracas" para o fundo (o parseInt converte a cor hexadecimal)
        int colorGreenLight = android.graphics.Color.parseColor("#E3F9EC");
        int colorRedLight = android.graphics.Color.parseColor("#FDECEA");

        if (isReceita) {
            //ativa o Receita
            cvIncome.setCardBackgroundColor(colorGreenLight);
            cvIncome.setStrokeColor(colorGreen);
            cvExpense.setStrokeWidth(4);
            tvIncomeText.setTextColor(colorGreen);

            //desativa o despesa
            cvExpense.setCardBackgroundColor(colorWhite);
            cvExpense.setStrokeColor(colorGray);
            cvExpense.setStrokeWidth(2); // volta ao normal
            tvExpenseText.setTextColor(colorGray);
        } else {
            // Desativa o receita
            cvIncome.setCardBackgroundColor(colorWhite);
            cvIncome.setStrokeColor(colorGray);
            cvIncome.setStrokeWidth(2); // volta ao normal
            tvIncomeText.setTextColor(colorGray);

            // Ativa o despesa
            cvExpense.setCardBackgroundColor(colorRedLight);
            cvExpense.setStrokeColor(colorRed);
            cvExpense.setStrokeWidth(4);
            tvExpenseText.setTextColor(colorRed);
        }
    }
}
