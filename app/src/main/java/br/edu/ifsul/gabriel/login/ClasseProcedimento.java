package br.edu.ifsul.gabriel.login;

import java.util.ArrayList;

/**
 * Created by gabriel on 29/06/18.
 */

public class ClasseProcedimento extends Atividade {

    public ClasseProcedimento(long id, String id_pac, String nome_pac, String id_obj,
                              String nome_obj, String hora, String hora_real, String obs,
                              String id_prof, String nome_prof, int realizado) {
        super(id, id_pac, nome_pac, id_obj, nome_obj, hora, hora_real, obs, id_prof, nome_prof,
                realizado);
    }

    @Override
    public ArrayList<String> getMoreInfo() {
        ArrayList<String> strings = super.getMoreInfo();
        strings.add(super.context.getResources().getString(R.string.procedInfo)+getNome_obj());
        return strings;
    }
}
