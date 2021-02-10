package br.edu.ifsul.gabriel.login.UI.Ui_utils;

/**
 * Created by gabriel on 21/05/18.
 */
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class Dialog {
    private static Calendar myCalendar = Calendar.getInstance();

    public static void data(final TextView v, Context classe){
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        new DatePickerDialog(classe, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(year,month,dayOfMonth);
                v.setText(sdf.format(myCalendar.getTime()));
            }
        },myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    public static void hora(final TextView v, Context classe){
        new TimePickerDialog(classe, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedHour < 10){
                    if (selectedMinute <10)
                        v.setText("0"+selectedHour+":0"+selectedMinute);
                    else
                        v.setText("0"+selectedHour+":"+selectedMinute);
                }else{
                    if (selectedMinute <10)
                        v.setText(selectedHour+":0"+selectedMinute);
                    else
                        v.setText(selectedHour+":"+selectedMinute);
                }
            }
        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();//Yes 24 hour time
    }
}
