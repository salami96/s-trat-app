package br.edu.ifsul.gabriel.login;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import br.edu.ifsul.gabriel.login.UI.Ui_utils.Sugestao;

public class NossoViewHolder extends RecyclerView.ViewHolder {

    final TextView title,date,time;
    final ImageButton info;
    final Switch done;
    final LinearLayout label_done;
    final CardView cd;

    public NossoViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        info = view.findViewById(R.id.info);
        done = view.findViewById(R.id.done);
        label_done = view.findViewById(R.id.label_done);
        cd = view.findViewById(R.id.cardView);
    }
}
