package br.edu.ifsul.gabriel.login.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import br.edu.ifsul.gabriel.login.R;

public class AddLembrete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lembrete);
        final CardView red = findViewById(R.id.red);
        final CardView blue = findViewById(R.id.blue);
        final CardView green = findViewById(R.id.green);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                red.setCardElevation(3);
                Intent Medicamento = new Intent(AddLembrete.this, Medicamento.class);
                Medicamento.putExtra("id",getIntent().getExtras().getString("id"));
                Medicamento.putExtra("key",getIntent().getExtras().getString("key"));
                startActivity(Medicamento);
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blue.setCardElevation(3);
                Intent Procedimento = new Intent(AddLembrete.this, Procedimento.class);
                Procedimento.putExtra("id",getIntent().getExtras().getString("id"));
                Procedimento.putExtra("key",getIntent().getExtras().getString("key"));
                startActivity(Procedimento);
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setCardElevation(3);
                Intent Medicao = new Intent(AddLembrete.this, Medicao.class);
                Medicao.putExtra("id",getIntent().getExtras().getString("id"));
                Medicao.putExtra("key",getIntent().getExtras().getString("key"));
                startActivity(Medicao);
            }
        });
    }
}
