package br.edu.ifsul.gabriel.login.UI.Ui_utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter<Sugestao> implements Filterable {
    private ArrayList<String> data;
    public ArrayList<Sugestao> sugestoes;
    private final String server = "http://173.199.117.116:8081/medicamentos/parcial/";

    public AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.data = new ArrayList<>();
        this.sugestoes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public Sugestao getSugestao(int position){
        return sugestoes.get(position);
    }

    @Nullable
    @Override
    public Sugestao getItem(int position) {
        return sugestoes.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    HttpURLConnection conn = null;
                    InputStream input = null;
                    try {
                        URL url = new URL(server + constraint.toString());
                        conn = (HttpURLConnection) url.openConnection();
                        input = conn.getInputStream();
                        InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                        BufferedReader buffer = new BufferedReader(reader, 8192);
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = buffer.readLine()) != null) {
                            builder.append(line);
                        }
                        JSONArray terms = new JSONArray(builder.toString());
                        ArrayList<String> nomes = new ArrayList<>();
                        sugestoes.clear();
                        for (int ind = 0; ind < terms.length(); ind++) {
                            JSONObject obj = terms.getJSONObject(ind);
                            String term =
                                    obj.getString("NO_PRODUTO");
                            nomes.add(term);
                            sugestoes.add(
                                    new Sugestao(obj.getString("NO_PRODUTO"),
                                    obj.getString("DS_APRESENTACAO"),
                                    obj.getString("idMed"))
                            );
                        }
                        results.values = nomes;
                        results.count = nomes.size();
                        data = nomes;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (input != null) {
                            try {
                                input.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (conn != null) conn.disconnect();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
    }
}
