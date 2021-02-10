package br.edu.ifsul.gabriel.login.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import br.edu.ifsul.gabriel.login.R;

public class MedicamentoReceiver extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamento_receiver);
        tv = findViewById(R.id.title);
        tv.setText(getIntent().getStringExtra("title"));
    }
}
