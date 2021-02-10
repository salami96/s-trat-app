package br.edu.ifsul.gabriel.login.UI;

import java.util.ArrayList;

import br.edu.ifsul.gabriel.login.Atividade;
import br.edu.ifsul.gabriel.login.R;

/**
 * Created by gabriel on 29/06/18.
 */

public class ClasseMedicao extends Atividade{
    public ClasseMedicao(long id, String id_pac, String nome_pac, String id_obj, String nome_obj,
                         String hora, String hora_real, String obs, String id_prof,String nome_prof,
                         int realizado) {
        super(id, id_pac, nome_pac, id_obj, nome_obj, hora, hora_real, obs, id_prof, nome_prof,
                realizado);
    }

    @Override
    public ArrayList<String> getMoreInfo() {
        ArrayList<String> strings = super.getMoreInfo();
        strings.add(super.context.getResources().getString(R.string.medicaoInfo)+getNome_obj());
        if((getResultado() != null)&&(!getResultado().equals("")))
            strings.add(super.context.getResources().getString(R.string.resultado)+": "+getResultado());
        return strings;
    }
}
