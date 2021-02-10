package br.edu.ifsul.gabriel.login;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class Adaptador extends RecyclerView.Adapter {

    private List<Lembrete> lembretes;
    private Context context;
    private int resourceId;
    private LayoutInflater inflater;

    public Adaptador(Context context, int resource, List<Lembrete> lembretes) {
        this.lembretes = lembretes;
        this.context = context;
        this.resourceId = resource;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.main_line_view, parent, false);
        NossoViewHolder holder = new NossoViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, int position) {
        NossoViewHolder holder = (NossoViewHolder) vHolder;

        Lembrete l1 = lembretes.get(position);
        holder.title.setText(l1.getCard().get(0));
        holder.date.setText(l1.getCard().get(2));
        holder.time.setText(l1.getCard().get(1));
        holder.cd.setId((int) l1.atv.getId());
        int r = Integer.parseInt(l1.getCard().get(3));
        if(r == 1){
            holder.done.setChecked(true);
            holder.done.setText(context.getResources().getString(R.string.jaFiz));
            holder.label_done.setBackgroundColor(ContextCompat.getColor(context,R.color.medicao));
            holder.done.setThumbResource(R.drawable.check);
        } else {
            holder.done.setChecked(false);
            holder.done.setText(context.getResources().getString(R.string.nFiz));
            holder.label_done.setBackgroundColor(ContextCompat.getColor(context,R.color.medicamento));
            holder.done.setThumbResource(R.drawable.ncheck);
        }
        //holder.done.;
    }

    @Override
    public int getItemCount() {
        return lembretes.size();
    }
}

/*public class Adaptador extends ArrayAdapter<Lembrete> {

    public Adaptador(Context context, int resource, List<Lembrete> objects){
        super(context, resource, objects);
        this.resourceId = resource;
        this.inflater = LayoutInflater.from(context);

    }
    public void setLocalBD(){

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Contato contato = getItem(position);
        convertView = inflater.inflate(resourceId, null);
        TextView tvNome = (TextView) convertView.findViewById(R.id.tvNome);
        TextView tvTelefone = (TextView) convertView.findViewById(R.id.tvTelefone);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
        tvNome.setText(contato.nome);
        tvTelefone.setText(contato.telefone);
        tvEmail.setText(contato.email);
        return convertView;
    }
}*/
