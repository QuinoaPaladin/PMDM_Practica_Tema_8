package es.studium.pasitosapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper
{
    private  static final int BD_version = 1;
    public static final String TABLE_NOMBRE = "SQLiteGPS";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUD = "latitud";
    public static final String COLUMN_LONGITUD = "longitud";
    public static final String COLUMN_BATERIA = "batería";

    public DatabaseHelper( Context context)
    {
        super(context, TABLE_NOMBRE, null, BD_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = " CREATE TABLE " + TABLE_NOMBRE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + COLUMN_LATITUD + " VARCHAR, " + COLUMN_LONGITUD + " VARCHAR, " + COLUMN_BATERIA + " VARCHAR); ";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS SQLiteGPS";
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean addData (String insertarlatitud, String insertarlongitud, String insertarbateria)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUD, insertarlatitud);
        contentValues.put(COLUMN_LONGITUD, insertarlongitud);
        contentValues.put(COLUMN_BATERIA, insertarbateria);

        long result = db.insert(TABLE_NOMBRE, null, contentValues);
        if (result == -1)
        {
            return false;

        }
        else
        {
            return true;
        }
    }
    public Cursor getListaContenidos()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NOMBRE, null);
        return data;
    }
}
