package br.edu.ifsul.gabriel.login;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gabriel Salami on 28/05/2018.
 */
public class MeuDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_LEMBRETES = "lembretes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_PAC = "idPac";
    public static final String COLUMN_NOME_PAC = "nomePac";
    public static final String COLUMN_ID_OBJ = "idObj";
    public static final String COLUMN_NOME_OBJ = "nomeObj";
    public static final String COLUMN_HORA = "hora";
    public static final String COLUMN_HORA_REAL = "horaReal";
    public static final String COLUMN_REALIZADO = "realizado";
    public static final String COLUMN_OBS = "obs";
    public static final String COLUMN_ID_PROF = "idProf";
    public static final String COLUMN_NOME_PROF = "nomeProf";
    public static final String COLUMN_IMPORTANTE = "importante";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "logitude";
    public static final String COLUMN_REAGENDADO = "reagendado";
    public static final String COLUMN_EDITADO = "editado";
    public static final String COLUMN_RESULTADO = "resultado";
    public static final String COLUMN_TYPE = "type";





    public static final String DATABASE_NAME = "arquivodobanco.bd";
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_CREATE = "create table "
            + TABLE_LEMBRETES + "("
            + COLUMN_ID + " integer primary key autoincrement,  "
            + COLUMN_ID_PAC + " text not null, "
            + COLUMN_NOME_PAC + " text not null, "
            + COLUMN_ID_OBJ + " text, "
            + COLUMN_NOME_OBJ + " text, "
            + COLUMN_HORA + " text not null, "
            + COLUMN_HORA_REAL + " text not null, "
            + COLUMN_OBS + " text, "
            + COLUMN_ID_PROF + " text, "
            + COLUMN_NOME_PROF + " text, "
            + COLUMN_REALIZADO + " text not null, "
            + COLUMN_IMPORTANTE + " text, "
            + COLUMN_LATITUDE + " text, "
            + COLUMN_LONGITUDE + " text, "
            + COLUMN_REAGENDADO + " text, "
            + COLUMN_EDITADO + " text, "
            + COLUMN_RESULTADO + " text, "
            + COLUMN_TYPE + " text);";

    public MeuDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEMBRETES);
        onCreate(db);
    }
}
