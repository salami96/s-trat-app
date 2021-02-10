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
import android.widget.AutoCompleteTextView;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifsul.gabriel.login.R;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.AutoCompleteAdapter;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Dialog;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.InputFilterMinMax;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Sugestao;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Validator;

public class Medicamento extends AppCompatActivity {
    AutoCompleteTextView desc;
    EditText dose,data,hora,vezes,duracao;
    Spinner spinner;
    Switch tratContinuo;
    public Sugestao selected = null;
    Button limpar,registrar;
    String id, key;
    ProgressBar pb;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamento);
        id = (getIntent().getExtras().getString("id"));
        key = (getIntent().getExtras().getString("key"));
        limpar = findViewById(R.id.btLimparLayMed);
        registrar = findViewById(R.id.btRegistrarLayMed);
        data = findViewById(R.id.data_ini);
        hora = findViewById(R.id.hora_ini);
        vezes = findViewById(R.id.vezes);
        duracao = findViewById(R.id.duracao);
        tratContinuo = findViewById(R.id.tratContinuo);
        spinner = findViewById(R.id.spinner);
        dose = findViewById(R.id.dose);
        desc = findViewById (R.id.descricao);
        pb = findViewById(R.id.progressBar);
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
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter (this,android.R.layout.simple_list_item_1);
        desc.setAdapter (adapter);
        desc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = new Sugestao(
                        adapter.getSugestao(position).nome,
                        adapter.getSugestao(position).dose,
                        adapter.getSugestao(position).id
                );
                desc.setText(selected.toString());
                //dose.setText(selected.dose);
            }
        });

        vezes.setFilters(new InputFilter[]{new InputFilterMinMax("1","6")});
        duracao.setFilters(new InputFilter[]{new InputFilterMinMax("0","31")});
        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Dialog.data(data,Medicamento.this);
                    setHelp(v);
                }
            }
        });
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Dialog.data(data,Medicamento.this);
            }
        });
        hora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Dialog.hora(hora,Medicamento.this);
                    setHelp(v);
                }
            }
        });
        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Dialog.hora(hora,Medicamento.this);
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
        desc.setText("");
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
        if((!Validator.validateNotNull(desc,getResources().getString(R.string.vMedicamento)))
                ||(!Validator.validateNotNull(vezes,getResources().getString(R.string.vVezes)))
                ||(!Validator.validateNotNull(data,getResources().getString(R.string.vData)))
                ||(!Validator.validateNotNull(hora,getResources().getString(R.string.vHora)))
                || id.isEmpty()){
            validate = false;
        }
        if(selected == null){
            desc.setError(getResources().getString(R.string.eMedicamento));
            desc.setFocusable(true);
            desc.requestFocus();
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
                params.put("pac",id);
                params.put("idMed",selected.id);
                params.put("idProf",0);
                params.put("importante",0);
                if(doseObs.isEmpty())
                    params.put("dose","0");
                else
                    params.put("dose",doseObs);
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
            final String url = getResources().getString(R.string.baseUrl) + "/receitas/";
            Log.v("params",params.toString());
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
                                AlertDialog.Builder build = new AlertDialog.Builder(Medicamento.this);
                                if (response.getString("status").equals("true")){
                                    build.setMessage(getResources().getString(R.string.succcess))
                                            .setNegativeButton(getResources().getString(R.string.novoMed),null)
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
        TextView help = findViewById(R.id.tvHelp);
        if (desc == v)
            help.setText(getResources().getString(R.string.hintMedicamento));
        if (dose == v)
            help.setText(getResources().getString(R.string.dose));
        if (data == v)
            help.setText(getResources().getString(R.string.data));
        if (hora == v)
            help.setText(getResources().getString(R.string.hora));
        if (vezes == v)
            help.setText(getResources().getString(R.string.vezes));
        if ((tratContinuo == v)||(duracao == v))
            help.setText(getResources().getString(R.string.duracao));
    }
}
