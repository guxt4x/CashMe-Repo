package com.example.cashme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail;
    TextInputEditText etPassword;
    TextView tvForgotPassword;
    MaterialButton btnEntrar;
    TextView tvCadastrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Antes de carregar a tela, verifica se já está logado
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // Se já tem um usuário logado, vai direto pra Main!
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvCadastrar = findViewById(R.id.tvCadastrar);

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        LoginActivity.this,
                        "Função em desenvolvimento. Vefifique seu e-mail!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        tvCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pegamos o que o usuário digitou e tiramos os espaços em branco do início e fim
                String emailDigitado = etEmail.getText().toString().trim();
                String senhaDigitada = etPassword.getText().toString().trim();

                boolean temErro = false;

                if (emailDigitado.isEmpty()) {
                    etEmail.setError("Por favor, digite seu e-mail");
                    temErro = true;
                }

                if (senhaDigitada.isEmpty()) {
                    etPassword.setError("Por favor, digite sua senha");
                    temErro = true;
                }

                if (!temErro) {
                    btnEntrar.setEnabled(false); // Evita duplo clique
                    
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(emailDigitado, senhaDigitada)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    btnEntrar.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "E-mail ou senha incorretos.", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}
