package br.edu.ifsul.gabriel.login;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by gabriel on 29/06/18.
 */

public class Atividade {
    long id;
    String id_pac;
    String nome_pac;
    String id_obj;
    String nome_obj;
    String hora;
    String hora_real;
    String obs;
    String id_prof;
    String nome_prof;
    String resultado;
    int realizado;
    String importante;
    public Context context;

    public Atividade(long id, String id_pac, String nome_pac, String id_obj, String nome_obj,
                     String hora, String hora_real, String obs, String id_prof, String nome_prof,
                     int realizado) {
        this.id = id;
        this.id_pac = id_pac;
        this.nome_pac = nome_pac;
        this.id_obj = id_obj;
        this.nome_obj = nome_obj;
        this.hora = hora;
        this.hora_real = hora_real;
        this.obs = obs;
        this.id_prof = id_prof;
        this.nome_prof = nome_prof;
        this.realizado = realizado;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getId_pac() {
        return id_pac;
    }
    public void setId_pac(String id_pac) {
        this.id_pac = id_pac;
    }
    public String getNome_pac() {
        return nome_pac;
    }
    public void setNome_pac(String nome_pac) {
        this.nome_pac = nome_pac;
    }
    public String getId_obj() {
        return id_obj;
    }
    public void setId_obj(String id_obj) {
        this.id_obj = id_obj;
    }
    public String getNome_obj() {
        return nome_obj;
    }
    public void setNome_obj(String nome_obj) {
        this.nome_obj = nome_obj;
    }
    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }
    public String getHora_real() {
        return hora_real;
    }
    public void setHora_real(String hora_real) {
        this.hora_real = hora_real;
    }
    public String getObs() {
        return obs;
    }
    public void setObs(String obs) {
        this.obs = obs;
    }
    public String getId_prof() {
        return id_prof;
    }
    public void setId_prof(String id_prof) {
        this.id_prof = id_prof;
    }
    public String getNome_prof() {
        return nome_prof;
    }
    public void setNome_prof(String nome_prof) {
        this.nome_prof = nome_prof;
    }
    public int getRealizado() {
        return realizado;
    }
    public void setRealizado(int realizado) {
        this.realizado = realizado;
    }
    public String getResultado() {
        return resultado;
    }
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    public String getImportante() {
        return importante;
    }
    public void setImportante(String importante) {
        this.importante = importante;
    }
    public void setContext(Context c){
        this.context = c;
    }
    public ArrayList<String> getCardInfo(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add(getNome_obj());
        strings.add(getHora().substring(11,16));
        strings.add(getHora().substring(8,10)+"/"+getHora().substring(5,7)+
                "/"+getHora().substring(0,4));
        strings.add(String.valueOf(getRealizado()));
        if(getRealizado()==1)

            strings.add(context.getResources().getString(R.string.sim));
        else
            strings.add(context.getResources().getString(R.string.nao));
        return strings;
    }
    public ArrayList<String> getMoreInfo(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add(context.getResources().getString(R.string.paciente)+getNome_pac());
        if(getObs() != null)
            strings.add(context.getResources().getString(R.string.obs)+": "+getObs());
        strings.add(context.getResources().getString(R.string.horaInfo)+getHora().substring(11,16));
        strings.add(context.getResources().getString(R.string.dataInfo)+getHora().substring(8,10)+"/"+getHora().substring(5,7)+
                "/"+getHora().substring(0,4));
        if(getRealizado()==1)
            strings.add(context.getResources().getString(R.string.realizado)+context.getResources().getString(R.string.sim));
        else
            strings.add(context.getResources().getString(R.string.realizado)+context.getResources().getString(R.string.nao));
        if(getNome_prof() != null)
            strings.add(context.getResources().getString(R.string.indicado)+this.nome_prof);
        if((getImportante() != null)&&(getImportante().equals("1")))
            strings.add(context.getResources().getString(R.string.importante));
        return strings;
    }
}
