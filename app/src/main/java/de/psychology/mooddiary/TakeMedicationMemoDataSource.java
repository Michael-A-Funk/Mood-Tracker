package de.psychology.mooddiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class TakeMedicationMemoDataSource {

    private static final String LOG_TAG = TakeMedicationMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private TakeMedicationMemoDbHelper dbHelper;

    private String[] columns = {
            TakeMedicationMemoDbHelper.COLUMN_ID,
            TakeMedicationMemoDbHelper.COLUMN_ID_MEDICATION,
            TakeMedicationMemoDbHelper.COLUMN_TIME,
            TakeMedicationMemoDbHelper.COLUMN_DATE,
            TakeMedicationMemoDbHelper.COLUMN_CHECKED
    };

    public TakeMedicationMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new TakeMedicationMemoDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }


    public void deleteTakeMedicationMemo(TakeMedicationMemo takeMedicationMemo) {
        long id = takeMedicationMemo.getId();

        database.delete(TakeMedicationMemoDbHelper.TABLE_TAKE_MEDICATION_LIST,
                TakeMedicationMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + takeMedicationMemo.toString() + "[=yyyyMMdd]");
    }

    public TakeMedicationMemo createTakeMedicationMemo(long idMedication) {
        ContentValues values = new ContentValues();

        String time = Zeit.now();
        long date = Zeit.today();

        values.put(TakeMedicationMemoDbHelper.COLUMN_ID_MEDICATION, idMedication);
        values.put(TakeMedicationMemoDbHelper.COLUMN_TIME, time);
        values.put(TakeMedicationMemoDbHelper.COLUMN_DATE, date);

        long insertId = database.insert(TakeMedicationMemoDbHelper.TABLE_TAKE_MEDICATION_LIST, null, values);

        Cursor cursor = database.query(TakeMedicationMemoDbHelper.TABLE_TAKE_MEDICATION_LIST,
                columns, TakeMedicationMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        TakeMedicationMemo takeMedicationMemo = cursorToTakeMedicationMemo(cursor);
        cursor.close();

        return takeMedicationMemo;
    }

    public TakeMedicationMemo updateTakeMedicationMemo(long newIdMedication, long id,  String newTime, long newDate, boolean newChecked) {
        int intValueChecked = (newChecked)? 1 : 0;


        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        //String newTime = sdf.format(new Date());
        //SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
        //long date = Long.parseLong(sdfDate.format(new Date().getTime ()));


        ContentValues values = new ContentValues();
        values.put(TakeMedicationMemoDbHelper.COLUMN_ID_MEDICATION, newIdMedication);
        values.put(TakeMedicationMemoDbHelper.COLUMN_TIME, newTime);
        values.put(TakeMedicationMemoDbHelper.COLUMN_DATE, newDate);
        values.put(TakeMedicationMemoDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(TakeMedicationMemoDbHelper.TABLE_TAKE_MEDICATION_LIST,
                values,
                TakeMedicationMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(TakeMedicationMemoDbHelper.TABLE_TAKE_MEDICATION_LIST,
                columns, TakeMedicationMemoDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        TakeMedicationMemo takeMedicationMemo = cursorToTakeMedicationMemo(cursor);
        cursor.close();

        return takeMedicationMemo;
    }

    private TakeMedicationMemo cursorToTakeMedicationMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(TakeMedicationMemoDbHelper.COLUMN_ID);
        int idIdMedication = cursor.getColumnIndex(TakeMedicationMemoDbHelper.COLUMN_ID_MEDICATION);
        int idTime   = cursor.getColumnIndex(TakeMedicationMemoDbHelper.COLUMN_TIME);
        int idDate   = cursor.getColumnIndex(TakeMedicationMemoDbHelper.COLUMN_DATE);
        int idChecked = cursor.getColumnIndex(TakeMedicationMemoDbHelper.COLUMN_CHECKED);

        long idMedication = cursor.getLong(idIdMedication);
        String time     = cursor.getString (idTime);
        long date       = cursor.getLong(idDate);
        long id = cursor.getLong(idIndex);
// ==========================================================
// WARUM IST DIES INT????? SOLLTE DIES NICHT BOOLEAN SEIN???
        int StringValueChecked = cursor.getInt(idChecked);
// ==========================================================

        boolean isChecked = (StringValueChecked != 0);

        TakeMedicationMemo takeMedicationMemo = new TakeMedicationMemo(idMedication, id, time, date, isChecked);

        return takeMedicationMemo;
    }
    // selecet all Dateformat as long number yyyyMMdd
    public List<TakeMedicationMemo> getAllTakeMedicationMemos(long date) {
        List<TakeMedicationMemo> takeMedicationMemoList = new ArrayList<>();

        Cursor cursor = database.query(TakeMedicationMemoDbHelper.TABLE_TAKE_MEDICATION_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        TakeMedicationMemo takeMedicationMemo;

        while(!cursor.isAfterLast()) {
            takeMedicationMemo = cursorToTakeMedicationMemo(cursor);
            if (takeMedicationMemo.getDate()==date){takeMedicationMemoList.add(takeMedicationMemo);}
            Log.d(LOG_TAG, "ID: " + takeMedicationMemo.getId() + ", Inhalt: " + takeMedicationMemo.toString());
            cursor.moveToNext();
        }
        cursor.close();

        return takeMedicationMemoList;
    }
    // selecet all Dateformat as long number yyyyMMdd

    public List<TakeMedicationMemo> getSortIntervalMedicationMemos(long first,long last) {
        List<TakeMedicationMemo> takeMedicationMemoList = new ArrayList<>();

        Cursor cursor = database.query(TakeMedicationMemoDbHelper.TABLE_TAKE_MEDICATION_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        TakeMedicationMemo takeMedicationMemo;

        while(!cursor.isAfterLast()) {
            takeMedicationMemo = cursorToTakeMedicationMemo(cursor);
            if (takeMedicationMemo.getDate()>=first&&takeMedicationMemo.getDate()<=last){takeMedicationMemoList.add(takeMedicationMemo);}
            Log.d(LOG_TAG, "ID: " + takeMedicationMemo.getId() + ", Inhalt: " + takeMedicationMemo.toString());
            cursor.moveToNext();
        }
        cursor.close();

        return takeMedicationMemoList;
    }

}