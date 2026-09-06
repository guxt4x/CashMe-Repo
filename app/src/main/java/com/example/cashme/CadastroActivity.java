package com.example.cashme;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CadastroActivity extends AppCompatActivity {

    TextInputEditText etName;
    TextInputEditText etEmail;
    TextInputEditText etPassword;
    TextInputEditText etConfirmPassword;
    MaterialButton btnCriarConta;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastro);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCriarConta = findViewById(R.id.btnCriarConta);

        btnCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameDigitado = etName.getText().toString().trim();
                String emailDigitado = etEmail.getText().toString().trim();
                String passwordDigitado = etPassword.getText().toString().trim();
                String confirmPasswordDigitado = etConfirmPassword.getText().toString().trim();

                boolean temErro = false;

                if (nameDigitado.isEmpty()) {
                    etName.setError("Por favor, digite seu nome");
                    temErro = true;
                }

                if (emailDigitado.isEmpty()) {
                    etEmail.setError("Por favor, digite seu e-mail");
                    temErro = true;
                }

                if (passwordDigitado.isEmpty()) {
                    etPassword.setError("Por favor, digite sua senha");
                    temErro = true;
                }

                if (!passwordDigitado.equals(confirmPasswordDigitado)) {
                    etConfirmPassword.setError("As senhas não coincidem!");
                    temErro = true;
                }

                if (!temErro) {
                    Toast.makeText(CadastroActivity.this,
                            "Conta criada com sucesso",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                }
            }
        });

    }
}
