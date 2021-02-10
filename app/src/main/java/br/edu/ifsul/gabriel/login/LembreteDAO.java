package br.edu.ifsul.gabriel.login;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import br.edu.ifsul.gabriel.login.UI.ClasseMedicao;
import br.edu.ifsul.gabriel.login.UI.Home;
import br.edu.ifsul.gabriel.login.UI.Medicamento;


public class LembreteDAO {
    private SQLiteDatabase database;
    private MeuDBHelper dbHelper;
    private Context contexto;
    private String idPac;
    private String[] todasColunas ={MeuDBHelper.COLUMN_ID, MeuDBHelper.COLUMN_ID_PAC,
            MeuDBHelper.COLUMN_NOME_PAC, MeuDBHelper.COLUMN_ID_OBJ, MeuDBHelper.COLUMN_NOME_OBJ,
            MeuDBHelper.COLUMN_HORA, MeuDBHelper.COLUMN_HORA_REAL, MeuDBHelper.COLUMN_REALIZADO,
            MeuDBHelper.COLUMN_ID_PROF, MeuDBHelper.COLUMN_NOME_PROF, MeuDBHelper.COLUMN_IMPORTANTE,
            MeuDBHelper.COLUMN_LATITUDE,MeuDBHelper.COLUMN_LONGITUDE, MeuDBHelper.COLUMN_REAGENDADO,
            MeuDBHelper.COLUMN_EDITADO, MeuDBHelper.COLUMN_OBS, MeuDBHelper.COLUMN_RESULTADO,
            MeuDBHelper.COLUMN_TYPE};

    public LembreteDAO(Context context, String id){
        dbHelper = new MeuDBHelper(context);
        contexto = context;
        idPac = id;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Lembrete criarLembrete(String idPac, String nomePac, String idObj, String nomeObj,
                                     String hora,String horaReal,String obs,int realizado,int type){
        ContentValues values = new ContentValues();
        values.put(MeuDBHelper.COLUMN_ID_PAC, idPac);
        values.put(MeuDBHelper.COLUMN_NOME_PAC, nomePac);
        values.put(MeuDBHelper.COLUMN_ID_OBJ, idObj);
        values.put(MeuDBHelper.COLUMN_NOME_OBJ, nomeObj);
        values.put(MeuDBHelper.COLUMN_HORA, hora);
        values.put(MeuDBHelper.COLUMN_HORA_REAL, horaReal);
        values.put(MeuDBHelper.COLUMN_OBS, obs);
        values.put(MeuDBHelper.COLUMN_REALIZADO, realizado);
        values.put(MeuDBHelper.COLUMN_TYPE, type);


        long insertId = database.insert(MeuDBHelper.TABLE_LEMBRETES, null, values);

        Cursor cursor = database.query(MeuDBHelper.TABLE_LEMBRETES, todasColunas,
                MeuDBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        Lembrete novoLembrete = cursor2Lembrete(cursor,type);
        cursor.close();
        return novoLembrete;
    }
    public boolean verify(String idObj, String hora){
        Cursor cursor = database.query(
                MeuDBHelper.TABLE_LEMBRETES,
                todasColunas,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();

        String cIdObj;
        String cHora;
        while (!cursor.isAfterLast()){
            cHora = cursor.getString(cursor.getColumnIndex(MeuDBHelper.COLUMN_HORA));
            cIdObj = cursor.getString(cursor.getColumnIndex(MeuDBHelper.COLUMN_ID_OBJ));
            if ((idObj.equals(cIdObj))&&(hora.equals(cHora)))
                return true;
            cursor.moveToNext();
        }
        return false;
    }
    public List<Lembrete> pegarTodosLembretes(){
        List<Lembrete> lembretes = new ArrayList<Lembrete>();
        Cursor cursor = database.query(MeuDBHelper.TABLE_LEMBRETES, todasColunas, null, null, null,
                null, "hora");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Lembrete lembrete = cursor2Lembrete(cursor, cursor.getInt(17));
            lembretes.add(lembrete);
            cursor.moveToNext();
        }

        cursor.close();
        return lembretes;
    }
    public List<Lembrete> pegarProximosLembretes(){
        List<Lembrete> lembretes = new ArrayList<Lembrete>();
        Cursor cursor = database.query(MeuDBHelper.TABLE_LEMBRETES, todasColunas,
                "hora >= date('now','-1 day')", null, null, null, "hora");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Lembrete lembrete = cursor2Lembrete(cursor, cursor.getInt(17));
            lembretes.add(lembrete);
            cursor.moveToNext();
        }

        cursor.close();
        return lembretes;
    }
    public void setDone(int id, boolean done){
        ContentValues cv = new ContentValues();
        int r;
        if (done)
            r = 1;
        else
            r = 0;
        cv.put(MeuDBHelper.COLUMN_REALIZADO,r);
        cv.put(MeuDBHelper.COLUMN_HORA_REAL,currentDateTime());
        database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(id)});

        Lembrete l1 = pegarInfo(id);

        JSONObject params = new JSONObject();
        try {
            params.put("type",0);
            if (l1.atv instanceof ClasseProcedimento)
                params.put("type",1);
            params.put("idLemb",id);
            params.put("idObj", l1.atv.getId_obj());
            params.put("hora", l1.atv.getHora());
            params.put("hora_real", currentDateTime());
            params.put("realizado", r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("params", String.valueOf(params));
        updateAPI(params);
    }
    public void setDone(int id, boolean done, String res){
        ContentValues cv = new ContentValues();
        int r;
        if (done)
            r = 1;
        else
            r = 0;
        cv.put(MeuDBHelper.COLUMN_REALIZADO,r);
        cv.put(MeuDBHelper.COLUMN_RESULTADO,res);
        cv.put(MeuDBHelper.COLUMN_HORA_REAL,currentDateTime());
        database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(id)});

        Lembrete l1 = pegarInfo(id);

        JSONObject params = new JSONObject();
        try {
            params.put("idLemb",id);
            params.put("res", res);
            params.put("realizado", r);
            params.put("medicao", l1.atv.getId_obj());
            params.put("hora", l1.atv.getHora());
            params.put("hora_real", currentDateTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("paramsMedicao", String.valueOf(params));
        updateMedicaoAPI(params);
    }
    public void setRes(int id, String res){
        ContentValues cv = new ContentValues();
        cv.put(MeuDBHelper.COLUMN_RESULTADO,res);
        database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(id)});
    }
    public void setProf(int id, String prof){
        ContentValues cv = new ContentValues();
        cv.put(MeuDBHelper.COLUMN_NOME_PROF,prof);
        database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(id)});
    }
    public void setImportante(int id, boolean importante){
        ContentValues cv = new ContentValues();
        cv.put(MeuDBHelper.COLUMN_IMPORTANTE,importante);
        Log.v("setImportante", String.valueOf(importante)+String.valueOf(id));
        database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(id)});
    }

    /*
    public void adiar(int id, int minutes){
        ContentValues cv = new ContentValues();
        Lembrete l1 = pegarInfo(id);
        String hora[] = l1.atv.getHora().substring(11,16).split(":");
        String data[] = l1.atv.getHora().substring(0,10).split("-");

        int hour = Integer.parseInt(hora[0]);
        int min = Integer.parseInt(hora[1]);
        int day = Integer.parseInt(data[2]);
        int month = Integer.parseInt(data[1]);
        int year = Integer.parseInt(data[0]);
        month -= 1;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        calendar.add(Calendar.MINUTE, minutes);
        cv.put(MeuDBHelper.COLUMN_HORA,currentDateTime(calendar));
        database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(id)});
    }
*/
    public Lembrete pegarInfo(int id){
        Cursor cursor = database.query(MeuDBHelper.TABLE_LEMBRETES, todasColunas,
                "_id=?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        Lembrete lembrete = cursor2Lembrete(cursor,cursor.getInt(17));
        return lembrete;
    }
    /*public List<Lembrete> buscaLembrete(String nome, String email, String telefone){
        String filtro;
        filtro = MeuDBHelper.COLUMN_NOME + " like '%" + nome + "%' AND " +
                MeuDBHelper.COLUMN_TELEFONE + " like '%" + telefone + "%' AND " +
                MeuDBHelper.COLUMN_EMAIL + " like '%" + email + "%'";

        List<Contato> contatos = new ArrayList<Contato>();



        Cursor cursor = database.query(MeuDBHelper.TABLE_CONTATOS, todasColunas, filtro, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Contato contato = cursor2Contato(cursor);
            contatos.add(contato);
            cursor.moveToNext();
        }

        cursor.close();
        return contatos;
    }


    */
    public void excluirLembretes(){
        List<Lembrete> lembretes = pegarProximosLembretes();
        AlarmManager alarmMgr = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(contexto, NotificationReceiver.class);
        for (Lembrete l : lembretes) {
            alarmMgr.cancel(PendingIntent.getBroadcast(contexto, (int) l.atv.getId(), intent, 0));
        }
        database.execSQL("DROP TABLE IF EXISTS " + MeuDBHelper.TABLE_LEMBRETES);
        database.execSQL(MeuDBHelper.DATABASE_CREATE);

    }

    private Lembrete cursor2Lembrete(Cursor cursor,int type) {
        Lembrete lembrete = null;
        if (type == 0){
            lembrete = new Lembrete(new ClasseMedicacao(
                    cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),
                    cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(15),
                    cursor.getString(8),cursor.getString(9), cursor.getInt(7)));
            lembrete.atv.setImportante(cursor.getString(10));
        }
        if (type == 1){
            lembrete = new Lembrete(new ClasseProcedimento(
                    cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),
                    cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(15),
                    cursor.getString(8),cursor.getString(9), cursor.getInt(7)
            ));
        }
        if (type == 2){
            lembrete = new Lembrete(new ClasseMedicao(
                    cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),
                    cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(15),
                    cursor.getString(8),cursor.getString(9), cursor.getInt(7)
            ));
            lembrete.atv.setResultado(cursor.getString(16));
        }
        lembrete.atv.setContext(contexto);
        return lembrete;
    }

    public String currentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        data_atual.setHours(0);
        data_atual.setMinutes(0);
        data_atual.setSeconds(0);
        String data_completa = dateFormat.format(data_atual);
        return data_completa;
    }
    public String currentDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String data_completa = dateFormat.format(data_atual);
        return data_completa;
    }
    public String currentDateTime(Calendar calendar){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data_atual = calendar.getTime();
        String data_completa = dateFormat.format(data_atual);
        return data_completa;
    }

    public void updateMedicaoAPI(final JSONObject params){
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(contexto.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        final String url = "****"+idPac;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                params,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            AlertDialog.Builder build = new AlertDialog.Builder(contexto);
                            if (response.getString("status").equals("true")){
                                build.setMessage(contexto.getResources().getString(R.string.succcess))
                                        .setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent i = new Intent(contexto,Home.class);
                                                        i.putExtra("id",idPac);
                                                        i.putExtra("atualizar",true);
                                                        contexto.startActivity(i);
                                                    }
                                                })
                                        .show();
                            }else{
//                                build.setMessage("Houve um problema, tente novamente mais tarde!")
                                build.setMessage(contexto.getResources().getString(R.string.hasError))
                                        .setPositiveButton(contexto.getResources().getString(R.string.fechar),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        try {
                                                            ContentValues cv = new ContentValues();
                                                            cv.put(MeuDBHelper.COLUMN_REALIZADO,0);
                                                            cv.put(MeuDBHelper.COLUMN_RESULTADO,"");
                                                            database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(params.getInt("idLemb"))});

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Intent i = new Intent(contexto,Home.class);
                                                        i.putExtra("atualizar",true);
                                                        i.putExtra("id",idPac);
                                                        contexto.startActivity(i);
                                                    }
                                                })
                                        .show();
                                Log.v("erro","params "+response.getString("msg"));
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
    public void updateAPI(final JSONObject params){
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(contexto.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        final String url = "****"+idPac;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                params,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            AlertDialog.Builder build = new AlertDialog.Builder(contexto);
                            if (response.getString("status").equals("true")){
                                build.setMessage(contexto.getResources().getString(R.string.succcess))
                                        .setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent i = new Intent(contexto,Home.class);
                                                        i.putExtra("id",idPac);
                                                        i.putExtra("atualizar",true);
                                                        contexto.startActivity(i);
                                                    }
                                                })
                                        .show();
                            }else{
//                                build.setMessage("Houve um problema, tente novamente mais tarde!")
                                build.setMessage(contexto.getResources().getString(R.string.hasError))
                                        .setPositiveButton(contexto.getResources().getString(R.string.fechar),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        try {
                                                            ContentValues cv = new ContentValues();
                                                            cv.put(MeuDBHelper.COLUMN_REALIZADO,0);
                                                            database.update(MeuDBHelper.TABLE_LEMBRETES,cv,"_id=?",new String[]{String.valueOf(params.getInt("idLemb"))});
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Intent i = new Intent(contexto,Home.class);
                                                        i.putExtra("atualizar",true);
                                                        i.putExtra("id",idPac);
                                                        contexto.startActivity(i);
                                                    }
                                                })
                                        .show();
                                Log.v("erro","params "+response.getString("msg"));
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
