package br.edu.ifsul.gabriel.login;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.sql.SQLException;

import br.edu.ifsul.gabriel.login.UI.Home;

/**
 * Created by gabriel on 08/06/18.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context contexto, Intent intent) {
        final String LABEL_ARCHIVE = "\uD83D\uDC4D FIZ AGORA";
        final String LABEL_REPLY = "\u23F0 ME LEMBRE DEPOIS";
        Intent newIntent;

        int notifyId = intent.getIntExtra("id",0);
        String title = intent.getStringExtra("title");
        String obs = intent.getStringExtra("obs");
        boolean isMedicao = intent.getBooleanExtra("medicao",false);
        String idPac = intent.getStringExtra("idPac");
        String key = intent.getStringExtra("key");
        Log.v("nReceiver:idPac=",idPac);
        String importante = intent.getStringExtra("importante");

        if (intent.getBooleanExtra("autoRefresh",false)){
            newIntent = new Intent(contexto, Home.class);
            newIntent.putExtra("autoRefresh", true);
            newIntent.putExtra("id", idPac);
            newIntent.putExtra("key", key);
            contexto.startActivity(newIntent);
        } else {
            LembreteDAO fonteDados = new LembreteDAO(contexto, idPac);
            try {
                fonteDados.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Lembrete l = fonteDados.pegarInfo(notifyId);
            if (l.atv.getRealizado() == 0) {
                String name = "s-trat_channel";
                String id = "s-trat_channel_" + notifyId; // The user-visible name of the channel.
                String description = "s-trat_first_channel"; // The user-visible description of the channel.

                PendingIntent pendingIntent;
                NotificationCompat.Builder builder;

                NotificationManager notifManager =
                        (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = notifManager.getNotificationChannel(id);
                    if (mChannel == null) {
                        mChannel = new NotificationChannel(id, name, importance);
                        mChannel.setDescription(description);
                        mChannel.enableVibration(true);
                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        notifManager.createNotificationChannel(mChannel);
                    }

                    builder = new NotificationCompat.Builder(contexto, id);
                    //builder.setGroup(name);
                    newIntent = new Intent(contexto, Home.class);

                    newIntent.putExtra("isFromNotify", true);
                    newIntent.putExtra("title", title);
                    newIntent.putExtra("idLemb", notifyId);
                    newIntent.putExtra("id", idPac);
                    newIntent.putExtra("key", key);
                    newIntent.putExtra("isMedicao", isMedicao);

                    if (importante.equals("1")) {
                        newIntent.putExtra("importante", true);
                        contexto.startActivity(newIntent);
                        Log.v("receiver", String.valueOf(importante));
                    }

        /*            newIntent.putExtra("realizado",false);
                    newIntent.putExtra("adiado",true);

                    //newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //newIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                    pendingIntent = PendingIntent.getActivity(contexto, notifyId, newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Action depois =
                            new NotificationCompat.Action.Builder(R.mipmap.ncheck,
                                    LABEL_REPLY, pendingIntent)
                                    .build();

                    newIntent.putExtra("realizado",true);
                    newIntent.putExtra("adiado",false);
                    pendingIntent = PendingIntent.getActivity(contexto, notifyId, newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Action tomei =
                            new NotificationCompat.Action.Builder(R.mipmap.check,
                                    LABEL_ARCHIVE, pendingIntent)
                                    .build();
        */
                    else {
                        pendingIntent = PendingIntent.getActivity(contexto, notifyId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        builder.setContentTitle(title)  // required
                                .setSmallIcon(R.drawable.new_logo) // required
                                .setContentText(obs)  // required
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setTicker(title)
                                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        //            if(!isMedicao)
                        //              builder.addAction(depois).addAction(tomei);
                        showNotif(builder, notifManager, notifyId);
                    }
                } else {

                    builder = new NotificationCompat.Builder(contexto);

                    newIntent = new Intent(contexto, Home.class);

                    newIntent.putExtra("isFromNotify", true);
                    newIntent.putExtra("title", title);
                    newIntent.putExtra("idLemb", notifyId);
                    newIntent.putExtra("id", idPac);
                    newIntent.putExtra("key", key);
                    newIntent.putExtra("isMedicao", isMedicao);

                    if (importante.equals("1")) {
                        newIntent.putExtra("importante", true);
                        contexto.startActivity(newIntent);
                        Log.v("receiver", String.valueOf(importante));
                    }

                    //newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //            newIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    else {
                        pendingIntent = PendingIntent.getActivity(contexto, notifyId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        /*
                        NotificationCompat.Action depois =
                                new NotificationCompat.Action.Builder(R.mipmap.ncheck,
                                        LABEL_REPLY, pendingIntent)
                                        .build();
                        NotificationCompat.Action tomei =
                                new NotificationCompat.Action.Builder(R.mipmap.check,
                                        LABEL_ARCHIVE, pendingIntent)
                                        .build();
        */
                        builder.setContentTitle(title)                           // required
                                .setSmallIcon(R.drawable.new_logo) // required
                                .setContentText(obs)  // required
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setTicker(title)
                                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                        showNotif(builder, notifManager, notifyId);
                    }
                } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            }
        }
    }
    public void showNotif(NotificationCompat.Builder builder, NotificationManager nM, int id){

        Notification notification = builder.build();
        nM.notify(id, notification);
    }
}