package br.edu.ifsul.gabriel.login;

import java.util.ArrayList;

/**
 * Created by gabriel on 29/06/18.
 */

public class ClasseMedicacao extends Atividade{
    String latitude, longitude, reagendado, editado;

    public ClasseMedicacao(long id, String id_pac, String nome_pac, String id_obj, String nome_obj,
                           String hora, String hora_real, String obs, String id_prof,
                           String nome_prof, int realizado) {
        super(id, id_pac, nome_pac, id_obj, nome_obj, hora, hora_real, obs, id_prof, nome_prof,
                realizado);
    }

    @Override
    public ArrayList<String> getMoreInfo() {
        ArrayList<String> strings = super.getMoreInfo();
        strings.add(super.context.getResources().getString(R.string.medicamentoInfo)+getNome_obj());
        return strings;
    }

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getReagendado() {
        return reagendado;
    }
    public void setReagendado(String reagendado) {
        this.reagendado = reagendado;
    }
    public String getEditado() {
        return editado;
    }
    public void setEditado(String editado) {
        this.editado = editado;
    }
}
