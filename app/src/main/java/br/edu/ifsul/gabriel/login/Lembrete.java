package br.edu.ifsul.gabriel.login;

import java.util.ArrayList;

/**
 * Created by PC on 08/06/2015.
 */
public class Lembrete {

    public Atividade atv;

    public Lembrete(Atividade atv){
        this.atv = atv;
    }
    public ArrayList<String> getStrings(){
        ArrayList<String> strings = atv.getMoreInfo();
        return strings;
    }
    public ArrayList<String> getCard(){
        ArrayList<String> strings = atv.getCardInfo();
        return strings;
    }
}
