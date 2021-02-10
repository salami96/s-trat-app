package br.edu.ifsul.gabriel.login.UI;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifsul.gabriel.login.R;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Validator;

public class Register extends AppCompatActivity {

    EditText etNome, etSobrenome, etTelefone, etEmail, etSenha;
    CheckBox cBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNome = findViewById(R.id.descricao);
        etSobrenome = findViewById(R.id.etSobrenome);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        cBox = findViewById(R.id.checkBox);
        Button btTermos = findViewById(R.id.btTermos);
        Button btRegistrar = findViewById(R.id.btRegistrarL2);
        Button btLimpar = findViewById(R.id.btLimparLayMed);
        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requisicaoVolley();
            }
        });
        btLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpar();
            }
        });
        btTermos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.s-trat.tk/termo_de_uso.pdf";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

    }

    private void requisicaoVolley(){
        boolean validate = true;
        if(!Validator.validateNotNull(etNome,getResources().getString(R.string.vNome))){
            validate = false;
        }
        if(!Validator.validateNotNull(etSobrenome,getResources().getString(R.string.vSobrenome))){
            validate = false;
        }
        if(!Validator.validateNotNull(etTelefone,getResources().getString(R.string.vTelefone))){
            validate = false;
        }
        if(!Validator.validateNotNull(etSenha,getResources().getString(R.string.vSenha))){
            validate = false;
        }
        if(!cBox.isChecked()){
            cBox.setError(getResources().getString(R.string.vTermos));
            validate = false;
        }
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        String nome = etNome.getText().toString();
        String sobrenome = etSobrenome.getText().toString();
        String telefone = etTelefone.getText().toString();
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();
        if(!Validator.validateEmail(email)){
            validate = false;
            etEmail.setError(getResources().getString(R.string.vEmail));
            etEmail.setFocusable(true);
            etEmail.requestFocus();
        }
        if (validate){
            JSONObject params = new JSONObject();
            try{
                params.put("nome",nome);
                params.put("sobrenome",sobrenome);
                params.put("telefone",telefone);
                params.put("email",email);
                params.put("senha",senha);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String url = "****/pacientes";

            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")){
                                Toast.makeText(getBaseContext(),getResources().getString(R.string.msgPaciente),
                                        Toast.LENGTH_LONG).show();
                                limpar();
                                Intent Main = new Intent(Register.this, Login.class);
                                startActivity(Main);
                            }else{
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.ocorreu)
                                        + response.getString("erro"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erro volley", error.toString());
                    }
                });
            mRequestQueue.add(request);
        }
    }
    public void limpar(){
        etNome.setText("");
        etSobrenome.setText("");
        etTelefone.setText("");
        etEmail.setText("");
        etSenha.setText("");
    }
}
