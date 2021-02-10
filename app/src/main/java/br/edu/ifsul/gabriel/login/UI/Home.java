package br.edu.ifsul.gabriel.login.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.edu.ifsul.gabriel.login.Adaptador;
import br.edu.ifsul.gabriel.login.Lembrete;
import br.edu.ifsul.gabriel.login.LembreteDAO;
import br.edu.ifsul.gabriel.login.Notify;
import br.edu.ifsul.gabriel.login.R;
import br.edu.ifsul.gabriel.login.UI.Ui_utils.Validator;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

public class Home extends AppCompatActivity {
    RecyclerView rv;
    String id, key;
    List<Lembrete> val;
    Adaptador adaptador;
    private LembreteDAO fonteDados;
    FabSpeedDial fab;
    SeekBar dor;
    SeekBar humor;
    SeekBar temp;
    EditText descSintoma;
    TextView nivelDor;
    TextView nivelHumor;
    TextView nivelTemp;
    SwipeRefreshLayout mSwipe;
    MediaPlayer mp;
    Float f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initalizeViews();
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layout);
        fonteDados = new LembreteDAO(Home.this, id);
        try {
            fonteDados.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onNewIntent(getIntent());
        atualizaLista();

        //requisicaoVolley();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView textView, int itemId) {
                if (itemId == (R.id.one)){
                    Intent AddLembrete = new Intent(Home.this, br.edu.ifsul.gabriel.login.UI.AddLembrete.class);
                    AddLembrete.putExtra("id",id);
                    AddLembrete.putExtra("key",key);
                    startActivity(AddLembrete);
                }else if (itemId == (R.id.two)){
                    alert_novaMedicao();
                }else if (itemId == R.id.three) {
                    alert_sintoma();
                }else if (itemId == (R.id.four)){
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = getResources().getString(R.string.shareText)+ id + "/" + key;
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.shareVia)));
                }
            }
        });

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requisicaoVolley();
            }
        });
    }
    @Override
    protected void onNewIntent(Intent i) {
        if(i.getBooleanExtra("autoRefresh", false)){
            fonteDados.excluirLembretes();
            mSwipe.setRefreshing(true);
            requisicaoVolley();
            Notify.autoRefresh(getApplicationContext(), i.getStringExtra("id"),
                    i.getStringExtra("key"));
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }, 10000);
        }
        if (i.getBooleanExtra("isFromNotify",false)){
            if (i.getBooleanExtra("isMedicao",false))
                alert_resMedicao(fonteDados.pegarInfo(i.getIntExtra("idLemb",0)));
            else {
                if(i.getBooleanExtra("importante",false)) {
                    mp = MediaPlayer.create(this, R.raw.alarm);
                    mp.start();
                }
                alertLembrete(fonteDados.pegarInfo(i.getIntExtra("idLemb",0)));
                /*                if(getIntent().getBooleanExtra("realizado",false)) {
                 //   fonteDados.setDone(getIntent().getIntExtra("idLemb", 0), true);
                }
                else if(getIntent().getBooleanExtra("adiado",false)){
                    fonteDados.adiar(getIntent().getIntExtra("idLemb",0),2);
                    Notify.make((fonteDados.pegarInfo(getIntent().getIntExtra("idLemb",0))),this,id);
                }*/
            }
        }
        if (i.getBooleanExtra("atualizar",false)){
            mSwipe.setRefreshing(true);
            requisicaoVolley();
        }
    }
    public void inform(View view){
        final ImageButton v = (ImageButton) view;
        ConstraintLayout box = (ConstraintLayout) v.getParent();
        CardView cd = (CardView) box.getParent();
        Lembrete l1 = fonteDados.pegarInfo(cd.getId());
        ArrayList strings = l1.getStrings();
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(),R.layout.alert_list,strings);
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.informacoes))
                .setSingleChoiceItems(adapter,0,null)
                .setNeutralButton(getResources().getString(R.string.fechar), null)
                .setIcon(R.drawable.info)
                .show();
    }
    public void tomar(View view){
        Switch v = (Switch) view;
        LinearLayout box = (LinearLayout) v.getParent();
        ConstraintLayout constraintLayout = (ConstraintLayout) box.getParent();
        CardView cd = (CardView) constraintLayout.getParent();
        Lembrete l1 = fonteDados.pegarInfo(cd.getId());
        if(v.isChecked()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                v.setThumbResource(R.drawable.check);
            }
            v.setText(getResources().getString(R.string.jaFiz));
            box.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.medicao));
            if(l1.atv instanceof ClasseMedicao){
                alert_resMedicao(l1);
            } else {
                fonteDados.setDone(cd.getId(),true);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                v.setThumbResource(R.drawable.ncheck);
            }
            v.setText(getResources().getString(R.string.nFiz));
            box.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.medicamento));
            if(l1.atv instanceof ClasseMedicao){
                fonteDados.setDone(cd.getId(),false,"");
            }else {
                fonteDados.setDone(cd.getId(), false);
            }
        }
    }
    private void requisicaoVolley(){
        RequestQueue mRequestQueue;
//        pb.setVisibility(View.VISIBLE);
  //      pb.setProgress(0);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        final String url = "****/receitas/"+id;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jMedicamento = response.getJSONArray("medicamentos");
                            JSONArray jProc = response.getJSONArray("procedimentos");
                            JSONArray jMedicoes = response.getJSONArray("medicoes");
                            int i,realizado,idProf;
                            String nomeObj, idObj, hora, obs;
                            if((jMedicamento.length()==0)&&(jProc.length()==0)
                                    &&(jMedicoes.length()==0)){
                                mSwipe.setRefreshing(false);
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.semRegistros), Toast.LENGTH_LONG).show();
                            }
                            for(i=0;i<jMedicamento.length();i++){
                                idObj = jMedicamento.getJSONObject(i).getString("id_med");
                                hora = jMedicamento.getJSONObject(i).getString("hora");
                                nomeObj = jMedicamento.getJSONObject(i).getString("medicamento");
                                obs = jMedicamento.getJSONObject(i).getString("dosagem");
                                idProf = jMedicamento.getJSONObject(i).getInt("id_prof");
                                realizado = jMedicamento.getJSONObject(i).getInt("realizado");
                                if((obs != null)||(obs.equals(""))||(obs.equals("0")));
                                    obs = getResources().getString(R.string.nenhumaObs);
                                if(!fonteDados.verify(idObj,hora)){
                                    Lembrete l1 = fonteDados.criarLembrete(
                                            jMedicamento.getJSONObject(i).getString("id_pac"),
                                            jMedicamento.getJSONObject(i).getString("paciente"),
                                            idObj, nomeObj, hora,
                                            jMedicamento.getJSONObject(i).getString("hora_real"),
                                            obs, realizado,0);
                                    if (jMedicamento.getJSONObject(i).getInt("importante")==1){
                                        fonteDados.setImportante((int) l1.atv.getId(),true);
                                    }else if (jMedicamento.getJSONObject(i).getInt("importante")==0){
                                        fonteDados.setImportante((int) l1.atv.getId(),false);
                                    }
                                    if (idProf > 0)
                                        fonteDados.setProf((int) l1.atv.getId(),
                                                jMedicamento.getJSONObject(i).getString("prof"));
                                    if (realizado == 0){
                                        Lembrete l2 = fonteDados.pegarInfo((int) l1.atv.getId());
                                        Notify.make(l2,getBaseContext(),id,key);
                                    }
                                }
                                atualizaLista();
                            }
                            for(i=0;i<jProc.length();i++){
                                idObj = jProc.getJSONObject(i).getString("id_proc");
                                hora = jProc.getJSONObject(i).getString("horario");
                                nomeObj = jProc.getJSONObject(i).getString("proced");
                                idProf = jProc.getJSONObject(i).getInt("id_prof");
                                realizado = jProc.getJSONObject(i).getInt("realizado");
                                obs = jProc.getJSONObject(i).getString("obs");
                                if((obs != null)||(obs.equals(""))||(obs.equals("0")));
                                    obs = getResources().getString(R.string.nenhumaObs);
                                if(!fonteDados.verify(idObj,hora)){
                                    Lembrete l1 = fonteDados.criarLembrete(
                                            jProc.getJSONObject(i).getString("id_pac"),
                                            jProc.getJSONObject(i).getString("paciente"),
                                            idObj, nomeObj, hora, hora,
                                            obs,realizado,1);
                                    if (idProf > 0)
                                        fonteDados.setProf((int) l1.atv.getId(),
                                                jProc.getJSONObject(i).getString("prof"));
                                    if (realizado == 0){
                                        Notify.make(l1,getBaseContext(),id,key);
                                    }
                                }
                                atualizaLista();
                            }
                            for(i=0;i<jMedicoes.length();i++){
                                idObj = jMedicoes.getJSONObject(i).getString("id_medicao");
                                hora = jMedicoes.getJSONObject(i).getString("hora");
                                nomeObj = jMedicoes.getJSONObject(i).getString(getResources().getString(R.string.medicaoLabel));
                                idProf = jMedicoes.getJSONObject(i).getInt("id_prof");
                                realizado = jMedicoes.getJSONObject(i).getInt("realizado");
                                obs = jMedicoes.getJSONObject(i).getString("obs");
                                if((obs != null)||(obs.equals(""))||(obs.equals("0")));
                                    obs = getResources().getString(R.string.nenhumaObs);
                                if(!fonteDados.verify(idObj,hora)){
                                    Lembrete l1 = fonteDados.criarLembrete(
                                            jMedicoes.getJSONObject(i).getString("id_pac"),
                                            jMedicoes.getJSONObject(i).getString("paciente"),
                                            idObj, nomeObj, hora,
                                            jMedicoes.getJSONObject(i).getString("hora_real"),
                                            obs,realizado,2);
                                    if (idProf > 0)
                                        fonteDados.setProf((int) l1.atv.getId(),
                                                jMedicoes.getJSONObject(i).getString("prof"));
                                    if (realizado == 1){
                                        fonteDados.setRes((int) l1.atv.getId(),
                                                jMedicoes.getJSONObject(i).getString("resultado"));
                                        l1.atv.setResultado(
                                                jMedicoes.getJSONObject(i).getString("resultado"));
                                    }else{
                                        Notify.make(l1,getBaseContext(),id,key);
                                    }
                                }
                                atualizaLista();
                            }
                            atualizaLista();

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
    private void initalizeViews(){
        rv = findViewById(R.id.recycler);
        id = getIntent().getStringExtra("id");
        key = getIntent().getStringExtra("key");
        fab = findViewById(R.id.fab);
        mSwipe = findViewById(R.id.SwipeRefreshLayout);
        Notify.autoRefresh(getApplicationContext(), id, key);
    }
    public void alert_sintoma(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.alert_sintoma,
                (ViewGroup) findViewById(R.id.alert_sintoma_constraint));

        descSintoma = Viewlayout.findViewById(R.id.sintoma);
        dor = Viewlayout.findViewById(R.id.dor);
        humor = Viewlayout.findViewById(R.id.humor);
        temp = Viewlayout.findViewById(R.id.temp);
        humor.setProgress(6);
        temp.setProgress(13);
        nivelDor = Viewlayout.findViewById(R.id.nivelDor);
        nivelHumor = Viewlayout.findViewById(R.id.nivelHumor);
        nivelTemp = Viewlayout.findViewById(R.id.nivelTemp);
        nivelDor.setText(getResources().getString(R.string.dor)+ "0");
        nivelHumor.setText(getResources().getString(R.string.humor)+ "\uD83D\uDE10");
        f = (float) 36.5;
        nivelTemp.setText(getResources().getString(R.string.temperatura)+f+"ºC");

        popDialog.setTitle(getResources().getString(R.string.alertSintoma));
        popDialog.setView(Viewlayout);

        dor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                nivelDor.setText(getResources().getString(R.string.dor)+progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
            }
        });
        humor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                String label = getResources().getString(R.string.humor);
                switch(progress){
                    case 0:
                        nivelHumor.setText(label + "\uD83E\uDD2E");
                        break;
                    case 1:
                        nivelHumor.setText(label + "\uD83D\uDE2A");
                        break;
                    case 2:
                        nivelHumor.setText(label + "\uD83E\uDD12");
                        break;
                    case 3:
                        nivelHumor.setText(label + "\uD83E\uDD27");
                        break;
                    case 4:
                        nivelHumor.setText(label + "\uD83D\uDE41");
                        break;
                    case 5:
                        nivelHumor.setText(label + "\uD83D\uDE15");
                        break;
                    case 6:
                        nivelHumor.setText(label + "\uD83D\uDE10");
                        break;
                    case 7:
                        nivelHumor.setText(label + "\uD83D\uDE42");
                        break;
                    case 8:
                        nivelHumor.setText(label + "\uD83D\uDE00");
                        break;
                }
            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        temp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                f = (float) 30.0 + (.5f * progress);
                nivelTemp.setText(getResources().getString(R.string.temperatura)+f+"ºC");
            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        popDialog.setNegativeButton(getResources().getString(R.string.cancelar),null);
        popDialog.setPositiveButton(getResources().getString(R.string.registrar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean valid = true;
                        if (!Validator.validateNotNull(descSintoma,""))
                            valid = false;
                        if (valid) {
                            String desc = descSintoma.getText().toString();
                            String nDor = String.valueOf(dor.getProgress());
                            String nHumor = String.valueOf(humor.getProgress());
                            String nTemp = String.valueOf(f);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date data = new Date();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(data);
                            Date data_atual = cal.getTime();
                            String data_completa = dateFormat.format(data_atual);
                            JSONObject params = new JSONObject();
                            try {
                                params.put("pac", id);
                                params.put("sintoma", desc);
                                params.put("dor", nDor);
                                params.put("humor", nHumor);
                                params.put("temp", nTemp);
                                params.put("hora", data_completa);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Log.v("param",String.valueOf(params));
                            enviaSintoma(params);
                        } else {
                            alert_sintoma();
                            descSintoma.setError(getResources().getString(R.string.erroSintoma));
                            descSintoma.setFocusable(true);
                            descSintoma.requestFocus();
                        }
                    }

                });
        popDialog.create();
        popDialog.show();
    }
    public void alert_resMedicao(final Lembrete l1){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.alert_res_medicao,
                (ViewGroup) findViewById(R.id.alert_resMedicao_constraint));

        final EditText res = Viewlayout.findViewById(R.id.res);

        popDialog.setTitle(getResources().getString(R.string.medicaoDe)+l1.atv.getNome_obj());
        popDialog.setView(Viewlayout);

        popDialog.setNeutralButton(getResources().getString(R.string.adiar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Notify.make(l1,getBaseContext(),id,key,15);
                Toast.makeText(getBaseContext(),getResources().getString(R.string.adiado),Toast.LENGTH_SHORT).show();
                atualizaLista();
            }
        });
        popDialog.setNegativeButton(getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fonteDados.setDone((int) l1.atv.getId(),false,"");
                        atualizaLista();
                    }
                });
        popDialog.setPositiveButton(getResources().getString(R.string.registrar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean valid = true;
                        if (!Validator.validateNotNull(res,getResources().getString(R.string.erroMedicao)))
                            valid = false;
                        if (valid) {
                            fonteDados.setDone((int) l1.atv.getId(),true,res.getText().toString());
                        } else {
                            alert_resMedicao(l1);
                            res.setError(getResources().getString(R.string.erroMedicao));
                            res.setFocusable(true);
                            res.requestFocus();
                        }
                    }

                });
        popDialog.create();
        popDialog.show();
    }
    public void alertLembrete(final Lembrete l1){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        popDialog.setTitle(l1.atv.getNome_obj());
        popDialog.setNegativeButton(getResources().getString(R.string.adiar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fonteDados.adiar((int) l1.atv.getId(),2);
                        //atualizaLista();
                        Notify.make(l1,getBaseContext(),id,key,15);
                        Toast.makeText(getBaseContext(),getResources().getString(R.string.adiado),Toast.LENGTH_SHORT).show();
                        if ((l1.atv.getImportante() != null)&&(l1.atv.getImportante().equals("1")))
                            mp.release();
                    }
                });
        popDialog.setPositiveButton(getResources().getString(R.string.fiz),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fonteDados.setDone((int) l1.atv.getId(),true);
                        atualizaLista();
                        if ((l1.atv.getImportante() != null)&&(l1.atv.getImportante().equals("1")))
                            mp.release();
                    }
                });
        popDialog.create();
        popDialog.show();
    }
    public void enviaSintoma(JSONObject params){
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        final String url = "****/sintomas/";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            AlertDialog.Builder build = new AlertDialog.Builder(Home.this);
                            final DialogInterface.OnClickListener alert =
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alert_sintoma();
                                }
                            };
                            if (response.getString("status").equals("true")){
                                build.setMessage(response.getString(getResources().getString(R.string.succcess)))
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }else{
                                build.setMessage(getResources().getString(R.string.ocorreu)+response.getString("erro"))
                                        .setPositiveButton(getResources().getString(R.string.tentar), alert)
                                        .setPositiveButton(getResources().getString(R.string.fechar), null)
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
    private void atualizaLista() {
        //val = fonteDados.pegarProximosLembretes();
        val = fonteDados.pegarProximosLembretes();
        adaptador = new Adaptador(this, R.layout.main_line_view, val);
        rv.setAdapter(adaptador);
        mSwipe.setRefreshing(false);
    }
    private void getMedicoes(String url, final Spinner desc){
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
                            ArrayList<String> medicoes = new ArrayList<>();
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
                                    int idMedicao = position;
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
    public void updateNovaMedicao(final JSONObject params, final AlertDialog.Builder build){
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        final String url = "****/medicoes/"+id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        Log.v("aqui","response");
                        try {
                            if (response.getString("status").equals("true")){
                                build.setMessage(response.getString(getResources().getString(R.string.succcess)))
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        mSwipe.setRefreshing(true);
                                                        requisicaoVolley();
                                                        atualizaLista();
                                                    }
                                                })
                                        .show();
                            }else {
                                build.setMessage(getResources().getString(R.string.ocorreu )+ response.getString("erro"))
                                        .setNegativeButton(getResources().getString(R.string.tentar), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                alert_novaMedicao();
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
    public void alert_novaMedicao(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.alert_nova_medicao,
                (ViewGroup) findViewById(R.id.alert_novaMedicao_constraint));
        final Spinner desc = Viewlayout.findViewById(R.id.descricaoAlertM);
        final String url = getResources().getString(R.string.baseUrl)+"/medicoes";
        getMedicoes(url, desc);
        final EditText obs = Viewlayout.findViewById(R.id.obsAlertM);
        final EditText res = Viewlayout.findViewById(R.id.respAlertM);

        popDialog.setTitle(getResources().getString(R.string.alertNovaMedicao));
        popDialog.setView(Viewlayout);

        popDialog.setNegativeButton(getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String obsSt = obs.getText().toString();
                        String aux = "delete";
                        if((desc.getSelectedItemPosition() == 4) && aux.equals(obsSt)){
                            fonteDados.excluirLembretes();
                            atualizaLista();
                        }
                    }
                });
        popDialog.setPositiveButton(getResources().getString(R.string.registrar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean valid = true;
                        if (!Validator.validateNotNull(res,""+getResources().getString(R.string.erroMedicao)))
                            valid = false;
                        if (valid) {
                            RequestQueue mRequestQueue;
                            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                            Network network = new BasicNetwork(new HurlStack());
                            mRequestQueue = new RequestQueue(cache,network);
                            mRequestQueue.start();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date data = new Date();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(data);
                            cal.set(Calendar.SECOND,0);
                            Date data_atual = cal.getTime();
                            String data_c = dateFormat.format(data_atual);
                            String hora_c = horaFormat.format(data_atual);
                            final String date_time = dateTimeFormat.format(data_atual);

                            final JSONObject params = new JSONObject();
                            try {
                                String obsStr = obs.getText().toString();
                                int d = desc.getSelectedItemPosition() + 1;
                                params.put("pac",id);
                                params.put("medicao",d);
                                params.put("idProf",0);
                                if(obsStr.isEmpty())
                                    params.put("obs","0");
                                else
                                    params.put("obs",obsStr);
                                params.put("hora",hora_c);
                                params.put("data",data_c);
                                params.put("vezes",1);
                                params.put("tratContinuo","0");
                                params.put("duracao","1");
                                params.put("dias","Dias");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest request = new JsonObjectRequest(
                                    Request.Method.POST,
                                    url.substring(0,36),
                                    params,
                                    new Response.Listener<JSONObject>(){

                                        @Override
                                        public void onResponse(final JSONObject response) {
                                            try {
                                                AlertDialog.Builder build = new AlertDialog.Builder(Home.this);
                                                if (response.getString("status").equals("true")){
                                                    JSONObject paramsResp = new JSONObject();

                                                    int d = desc.getSelectedItemPosition() + 1;
                                                    paramsResp.put("res", res.getText().toString());
                                                    paramsResp.put("medicao", d);
                                                    paramsResp.put("hora", date_time);
                                                    paramsResp.put("hora_real", date_time);
                                                    paramsResp.put("realizado", 1);

                                                    updateNovaMedicao(paramsResp,build);
                                                }else{
                                                    build.setMessage(getResources().getString(R.string.ocorreu)+response.getString("erro"))
                                                            .setNegativeButton(getResources().getString(R.string.tentar), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    alert_novaMedicao();
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

                        } else {
                            alert_novaMedicao();
                            res.setError(getResources().getString(R.string.erroMedicao)+"");
                            res.setFocusable(true);
                            res.requestFocus();
                        }
                    }

                });
        popDialog.create();
        popDialog.show();
    }

}
