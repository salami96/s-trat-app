package br.edu.ifsul.gabriel.login;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import br.edu.ifsul.gabriel.login.UI.ClasseMedicao;
import br.edu.ifsul.gabriel.login.UI.Home;

/**
 * Created by gabriel on 08/06/18.
 */

public class Notify {
    public static void make(Context contexto){
        AlarmManager alarmManager = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(contexto,NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);

    }

    public static void make(String title, String date, String time, Context contexto) {
        //A partir de uma String recebida com o formato hh:mm é o horário da notificação é criado
//        Toast.makeText(contexto,time,Toast.LENGTH_LONG).show();
        String partes[] = time.split(":");
        String datePartes[] = date.split("/");
        int hour = Integer.parseInt(partes[0]);
        int min = Integer.parseInt(partes[1]);
//        int day = Integer.parseInt(datePartes[0]);
        //      int month = Integer.parseInt(datePartes[1]);
        //    int year = Integer.parseInt(datePartes[2]);

        //Em seguida criamos uma instância da classe Alarm Mangager e uma Intent indicando quem
        //deve ser chamado quando o momento chegar, nesse casso a classe MyNotificationSystem
        AlarmManager alarmMgr = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(contexto, NotificationReceiver.class);
        intent.putExtra("title", title);

        //PendingIntent alarmIntent = PendingIntent.getBroadcast(contexto, 0, intent, PendingIntent.);

        //Verificamos se o evento já está agendado
        boolean isWorking = (PendingIntent.getBroadcast(contexto, 0, intent,
                PendingIntent.FLAG_NO_CREATE) != null);

        //Se o evento não está agendado precisamos agendar
        if (!isWorking) {
            //Criamos uma Pending Intente
            PendingIntent alarmIntent = PendingIntent.getBroadcast(contexto, 0, intent, 0);

            //Definimos o agendamento de acordo com o que recebemos
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            /*calendar.set(Calendar.DAY_OF_MONTH, 12);
            calendar.set(Calendar.MONTH, 6);
            calendar.set(Calendar.YEAR, 2018);*/

            //Verificamos se o evento foi agendado para uma data no futuro e, se não,
            //colocamos mais um dia no agendamento
            //Nunca se deve agendar um evento num tempo passado! (coisas estranhas acontecem)
            if (!calendar.after(Calendar.getInstance()))
                calendar.roll(Calendar.DATE, true);

            //Criamos nosso agendamento
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            //Log.v("notif","AlarmManager");
        }
    }
    /* revisar substituir instanceof por essa solucao + OO
    private static Intent ajusta(ClasseMedicao cm, Intent i){
        i.putExtra("title", "MEDIÇÃO DE "+cm.getNome_obj());
        i.putExtra("medicao", true);
        return i;
    }

    private static Intent ajusta(ClasseMedicacao cm, Intent i){
        i.putExtra("title", "MEDIÇÃO DE "+cm.getNome_obj());
        i.putExtra("medicao", true);
        return i;
    }
    */

    public static void make(Lembrete l1, Context contexto, String idPac, String key){
        //A partir de uma String recebida com o formato hh:mm é o horário da notificação é criado
//        Toast.makeText(contexto,time,Toast.LENGTH_LONG).show();
        String hora[] = l1.atv.getHora().substring(11,16).split(":");
        String data[] = l1.atv.getHora().substring(0,10).split("-");


        int hour = Integer.parseInt(hora[0]);
        int min = Integer.parseInt(hora[1]);
        int day = Integer.parseInt(data[2]);
        int month = Integer.parseInt(data[1]);
        int year = Integer.parseInt(data[0]);
        month -= 1;


        //Em seguida criamos uma instância da classe Alarm Mangager e uma Intent indicando quem
        //deve ser chamado quando o momento chegar, nesse casso a classe MyNotificationSystem
        AlarmManager alarmMgr = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(contexto, NotificationReceiver.class);
        int id = (int) l1.atv.getId();
        Log.v("notify:idPac=",idPac);
        intent.putExtra("idPac", idPac);
        intent.putExtra("key", key);
        intent.putExtra("id", id);
        intent.putExtra("obs", l1.atv.getObs());
        Log.v("notify",l1.atv.getId()+" imp "+l1.atv.getImportante());
        intent.putExtra("importante", "0");
        if (l1.atv.importante != null)
            intent.putExtra("importante", l1.atv.getImportante());
        if (l1.atv instanceof ClasseMedicao) {
            intent.putExtra("title", contexto.getResources().getString(R.string.medicaoInfo)+l1.atv.getNome_obj());
            intent.putExtra("medicao", true);
        }
        //intent = ajusta(l1.atv,intent);
        if (l1.atv instanceof ClasseMedicacao){
            intent.putExtra("title", l1.atv.getNome_obj());
        }
        if (l1.atv instanceof ClasseProcedimento){
            intent.putExtra("title", l1.atv.getNome_obj());
        }

        //PendingIntent alarmIntent = PendingIntent.getBroadcast(contexto, 0, intent, PendingIntent.);

        //Verificamos se o evento já está agendado
        boolean isWorking = (PendingIntent.getBroadcast(contexto, id, intent,
                PendingIntent.FLAG_NO_CREATE) != null);

        //Se o evento não está agendado precisamos agendar
        //if (!isWorking) {
        if(true){
            //Criamos uma Pending Intent
            PendingIntent alarmIntent = PendingIntent.getBroadcast(contexto, id, intent, 0);


            //Definimos o agendamento de acordo com o que recebemos
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);


            //Verificamos se o evento foi agendado para uma data no futuro e, se não,
            //colocamos mais um dia no agendamento
            //Nunca se deve agendar um evento num tempo passado! (coisas estranhas acontecem)
            if(!calendar.after(Calendar.getInstance()))
                calendar.roll(Calendar.DATE, true);

            //Criamos nosso agendamento
            alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmIntent);
            Log.v("notif","AlarmManager");
        }

    }
    public static void make(Lembrete l1, Context contexto, String idPac, String key, int minLater){
        //A partir de uma String recebida com o formato hh:mm é o horário da notificação é criado
//        Toast.makeText(contexto,time,Toast.LENGTH_LONG).show();
        //Em seguida criamos uma instância da classe Alarm Mangager e uma Intent indicando quem
        //deve ser chamado quando o momento chegar, nesse casso a classe MyNotificationSystem
        AlarmManager alarmMgr = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(contexto, NotificationReceiver.class);
        int id = (int) l1.atv.getId();
        intent.putExtra("idPac", idPac);
        intent.putExtra("key", key);
        intent.putExtra("id", id);
        intent.putExtra("obs", l1.atv.getObs());
        Log.v("notify",l1.atv.getId()+" imp "+l1.atv.getImportante());
        intent.putExtra("importante", "0");
        if (l1.atv.importante != null)
            intent.putExtra("importante", l1.atv.getImportante());
        if (l1.atv instanceof ClasseMedicao) {
            intent.putExtra("title", contexto.getResources().getString(R.string.medicaoInfo)+l1.atv.getNome_obj());
            intent.putExtra("medicao", true);
        }
        //intent = ajusta(l1.atv,intent);
        if (l1.atv instanceof ClasseMedicacao){
            intent.putExtra("title", l1.atv.getNome_obj());
        }
        if (l1.atv instanceof ClasseProcedimento){
            intent.putExtra("title", l1.atv.getNome_obj());
        }

        //PendingIntent alarmIntent = PendingIntent.getBroadcast(contexto, 0, intent, PendingIntent.);

        //Verificamos se o evento já está agendado
        boolean isWorking = (PendingIntent.getBroadcast(contexto, id, intent,
                PendingIntent.FLAG_NO_CREATE) != null);

        //Se o evento não está agendado precisamos agendar
        //if (!isWorking) {
        if(l1.atv.getRealizado() != 1){
            //Criamos uma Pending Intente
            PendingIntent alarmIntent = PendingIntent.getBroadcast(contexto, id, intent, 0);


            //Definimos o agendamento de acordo com o que recebemos
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.MINUTE, minLater);

            //Verificamos se o evento foi agendado para uma data no futuro e, se não,
            //colocamos mais um dia no agendamento
            //Nunca se deve agendar um evento num tempo passado! (coisas estranhas acontecem)
            if(!calendar.after(Calendar.getInstance()))
                calendar.roll(Calendar.DATE, true);

            //Criamos nosso agendamento
            alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmIntent);
            Log.v("notif","AlarmManager");
        }

    }
    public static void autoRefresh(Context contexto, String idPac, String key){


        //Em seguida criamos uma instância da classe Alarm Mangager e uma Intent indicando quem
        //deve ser chamado quando o momento chegar, nesse casso a classe MyNotificationSystem
        AlarmManager alarmMgr = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(contexto, NotificationReceiver.class);

        intent.putExtra("idPac", idPac);
        intent.putExtra("key", key);
        intent.putExtra("autoRefresh", true);

        //Definimos o agendamento de acordo com o que recebemos
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //Criamos uma Pending Intent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(contexto,
                (int) calendar.getTimeInMillis(), intent, 0);


        //Verificamos se o evento foi agendado para uma data no futuro e, se não,
        //colocamos mais um dia no agendamento
        //Nunca se deve agendar um evento num tempo passado! (coisas estranhas acontecem)
        if(!calendar.after(Calendar.getInstance()))
            calendar.roll(Calendar.DATE, true);

        Log.d("data agendada",calendar.get(Calendar.DATE)+" "+calendar.get(Calendar.MINUTE));

        //Criamos nosso agendamento
        alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmIntent);
    }

}
