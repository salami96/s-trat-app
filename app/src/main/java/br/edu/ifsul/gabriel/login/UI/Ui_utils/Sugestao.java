package br.edu.ifsul.gabriel.login.UI.Ui_utils;

/**
 * Created by gabriel on 13/06/18.
 */

public class Sugestao{
    public String nome,dose,id;
    public Sugestao(String nome,String dose,String id){
        this.nome = nome;
        this.dose = dose;
        this.id = id;
    }
    public String toString() {
        return this.nome+" - "+this.dose;
    }

}
