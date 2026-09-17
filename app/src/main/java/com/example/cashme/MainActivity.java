package com.example.cashme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashme.repository.LancamentoRepository;
import com.example.cashme.ui.LancamentoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navegação do FAB
        fabAdd = findViewById(R.id.fabAdd);

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
         */
        LancamentoAdapter adapter = new LancamentoAdapter(LancamentoRepository.lista, this);

        rvTransactions.setAdapter(adapter);
    }
}