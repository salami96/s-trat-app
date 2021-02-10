package br.edu.ifsul.gabriel.login.UI;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifsul.gabriel.login.Notify;
import br.edu.ifsul.gabriel.login.R;

public class Login extends AppCompatActivity {
    private static final int uniqueID = 45612;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btEntrar = findViewById(R.id.btEntrar);
        pingEnglishVersion();
        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requisicaoVolley();
            }
        });
        Button btRegistro = findViewById(R.id.btRegister);
        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Register = new Intent(Login.this, br.edu.ifsul.gabriel.login.UI.Register.class);
                startActivity(Register);
            }
        });
    }
    private void requisicaoVolley(){
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        String email =
                ((EditText)findViewById(R.id.etEmail)).getText().toString();
        String senha =
                ((EditText)findViewById(R.id.etSenha)).getText().toString();
        JSONObject params = new JSONObject();
        try{
            params.put("email",email);
            params.put("senha",senha);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url = "****/pacientes/login";


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")){
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.logado), Toast.LENGTH_SHORT).show();
                                Intent Home = new Intent(Login.this, br.edu.ifsul.gabriel.login.UI.Home.class);
                                Home.putExtra("id",response.getString("id"));
                                Home.putExtra("key",response.getString("access_key"));
                                startActivity(Home);
                            }else{
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.falha), Toast.LENGTH_LONG).show();
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
    private void pingEnglishVersion(){
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache,network);
        mRequestQueue.start();
        String email =
                ((EditText)findViewById(R.id.etEmail)).getText().toString();
        String senha =
                ((EditText)findViewById(R.id.etSenha)).getText().toString();
        JSONObject params = new JSONObject();
        try{
            params.put("email",email);
            params.put("senha",senha);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url = getResources().getString(R.string.baseUrl) + "/time";
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Log.v("ping response",response.toString());
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erro volley", error.toString());
                    }
                }
        );

        mRequestQueue.add(request);
    }

}
