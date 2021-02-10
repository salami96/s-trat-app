package br.edu.ifsul.gabriel.login.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.edu.ifsul.gabriel.login.R;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Dialog;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.InputFilterMinMax;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Sugestao;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Validator;

public class Medicao extends AppCompatActivity {
    EditText dose,data,hora,vezes,duracao;
    Spinner spinner,desc;
    Switch tratContinuo;
    public ArrayList<String> medicoes = null;
    Button limpar,registrar;
    String id,url,key;
    int idMedicao;
    ProgressBar pb;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicao);
        url = getResources().getString(R.string.baseUrl)+"/medicoes";
        getMedicoes();
        id = (getIntent().getExtras().getString("id"));
        key = (getIntent().getExtras().getString("key"));
        limpar = findViewById(R.id.btLimparLayMedM);
        registrar = findViewById(R.id.btRegistrarLayMedM);
        data = findViewById(R.id.data_iniM);
        hora = findViewById(R.id.hora_iniM);
        vezes = findViewById(R.id.vezesM);
        duracao = findViewById(R.id.duracaoM);
        tratContinuo = findViewById(R.id.tratContinuoM);
        spinner = findViewById(R.id.spinnerM);
        dose = findViewById(R.id.doseM);
        desc = findViewById (R.id.descricaoM);
        pb = findViewById(R.id.progressBarM);
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setHelp(v);
            }
        };
        desc.setOnFocusChangeListener(listener);
        dose.setOnFocusChangeListener(listener);
        vezes.setOnFocusChangeListener(listener);
        duracao.setOnFocusChangeListener(listener);
        vezes.setFilters(new InputFilter[]{new InputFilterMinMax("1","6")});
        duracao.setFilters(new InputFilter[]{new InputFilterMinMax("0","31")});
        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Dialog.data(data,Medicao.this);
                    setHelp(v);
                }
            }
        });
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Dialog.data(data,Medicao.this);
            }
        });
        hora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Dialog.hora(hora,Medicao.this);
                    setHelp(v);
                }
            }
        });
        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Dialog.hora(hora,Medicao.this);
            }
        });
        tratContinuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                setHelp(v);
            }
        });
        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    public void clear(){
        desc.setSelection(0);
        desc.requestFocus();
        dose.setText("");
        data.setText("");
        hora.setText("");
        vezes.setText("");
        tratContinuo.setChecked(false);
        hide();
        duracao.setText("");
        spinner.setSelection(0);
    }
    public void hide(){
        int vis;
        if (tratContinuo.isChecked())
            vis = View.INVISIBLE;
        else
            vis = View.VISIBLE;
        spinner.setVisibility(vis);
        duracao.setVisibility(vis);
    }
    private void register() {
        boolean validate = true;
        if((!Validator.validateNotNull(vezes,getResources().getString(R.string.vVezes)))
                ||(!Validator.validateNotNull(data,getResources().getString(R.string.vData)))
                ||(!Validator.validateNotNull(hora,getResources().getString(R.string.vHora)))
                || id.isEmpty()){
            validate = false;
        }
        if(vezes.getText().toString().equals("5")){
            validate = false;
            vezes.setError(getResources().getString(R.string.eVezes));
            vezes.setFocusable(true);
            vezes.requestFocus();
        }

        if(!tratContinuo.isChecked()){
            if(!Validator.validateNotNull(duracao,getResources().getString(R.string.vDuracao)))
                validate = false;
        }
        if(validate){
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(0);
            RequestQueue mRequestQueue;
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache,network);
            mRequestQueue.start();
            JSONObject params = new JSONObject();
            String doseObs = dose.getText().toString();
            try{
                int d = desc.getSelectedItemPosition() + 1;
                params.put("pac",id);
                params.put("medicao",d);
                params.put("idProf",0);
                if(doseObs.isEmpty())
                    params.put("obs","0");
                else
                    params.put("obs",doseObs);
                params.put("hora",hora.getText().toString());
                params.put("data",data.getText().toString());
                params.put("vezes",vezes.getText().toString());
                if(tratContinuo.isChecked()){
                    params.put("tratContinuo","1");
                    params.put("duracao","0");
                    params.put("dias","0");
                }
                else{
                    params.put("tratContinuo","0");
                    params.put("duracao",duracao.getText().toString());
                    params.put("dias",spinner.getSelectedItem());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    params,
                    new Response.Listener<JSONObject>(){

                        @Override
                        public void onResponse(JSONObject response) {
                            pb.setVisibility(View.INVISIBLE);
                            try {
                                final Context c = getBaseContext();
                                AlertDialog.Builder build = new AlertDialog.Builder(Medicao.this);
                                if (response.getString("status").equals("true")){
                                    build.setMessage(getResources().getString(R.string.succcess))
                                            .setNegativeButton(getResources().getString(R.string.novoMedicao),null)
                                            .setPositiveButton(getResources().getString(R.string.telaInicial),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent i = new Intent(c,Home.class);
                                                            i.putExtra("id",id);
                                                            i.putExtra("key",key);
                                                            i.putExtra("atualizar",true);
                                                            startActivity(i);
                                                        }
                                                    })
                                            .show();
                                    clear();
                                }else{
                                    build.setMessage(getResources().getString(R.string.ocorreu)+response.getString("erro"))
                                            .setNegativeButton(getResources().getString(R.string.tentar),null)
                                            .setPositiveButton(getResources().getString(R.string.telaInicial),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent i = new Intent(c,Home.class);
                                                            i.putExtra("id",id);
                                                            i.putExtra("key",key);
                                                            startActivity(i);
                                                        }
                                                    })
                                            .show();
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
    private void setHelp(View v){
        TextView help = findViewById(R.id.tvHelpM);
        if (dose == v)
            help.setText(getResources().getString(R.string.dicas));
        if (data == v)
            help.setText(getResources().getString(R.string.data));
        if (hora == v)
            help.setText(getResources().getString(R.string.hora));
        if (vezes == v)
            help.setText(getResources().getString(R.string.vezes));
        if ((tratContinuo == v)||(duracao == v))
            help.setText(getResources().getString(R.string.duracao));
    }
    private void getMedicoes(){
        RequestQueue mRequestQueue;
//        pb.setVisibility(View.VISIBLE);
  //      pb.setProgress(0);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            int i;
                            medicoes = new ArrayList<>();
                            for(i=0;i<response.length();i++){
                                medicoes.add(response.getJSONObject(i).getString("descricao"));
                            }
                            desc.setAdapter(new ArrayAdapter(getBaseContext(),R.layout.item_spinner,
                                    medicoes));
                            desc.setPrompt(getResources().getString(R.string.selecione));

                            /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(),
                                    android.R.layout.simple_dropdown_item_1line, medicoes);
                            ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            desc.setAdapter(spinnerArrayAdapter);*/
                            desc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    idMedicao = position;
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });


                            //pb.setVisibility(View.INVISIBLE);
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
