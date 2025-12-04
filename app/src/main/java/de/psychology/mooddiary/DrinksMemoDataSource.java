package de.psychology.mooddiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DrinksMemoDataSource {

    private static final String LOG_TAG = DrinksMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DrinksMemoDbHelper dbHelper;

    private String[] columns = {
            DrinksMemoDbHelper.COLUMN_ID,
            DrinksMemoDbHelper.COLUMN_DRINKS,
            DrinksMemoDbHelper.COLUMN_QUANTITY,
            DrinksMemoDbHelper.COLUMN_TIME,
            DrinksMemoDbHelper.COLUMN_DATE,
            DrinksMemoDbHelper.COLUMN_CHECKED
    };

    public DrinksMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new DrinksMemoDbHelper(context);
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


    public void deleteDrinksMemo(DrinksMemo drinksMemo) {
        long id = drinksMemo.getId();

        database.delete(DrinksMemoDbHelper.TABLE_DRINKS_LIST,
                DrinksMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + drinksMemo.toString() + "[=yyyyMMdd]");
    }

    public DrinksMemo createDrinksMemo(String drinks, String quantity) {
        ContentValues values = new ContentValues();
        String time = Zeit.now();
        long date = Zeit.today();

        values.put(DrinksMemoDbHelper.COLUMN_DRINKS, drinks);
        values.put(DrinksMemoDbHelper.COLUMN_QUANTITY, quantity);
        values.put(DrinksMemoDbHelper.COLUMN_TIME, time);
        values.put(DrinksMemoDbHelper.COLUMN_DATE, date);

        long insertId = database.insert(DrinksMemoDbHelper.TABLE_DRINKS_LIST, null, values);

        Cursor cursor = database.query(DrinksMemoDbHelper.TABLE_DRINKS_LIST,
                columns, DrinksMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        DrinksMemo drinksMemo = cursorToDrinksMemo(cursor);
        cursor.close();

        return drinksMemo;
    }

    public DrinksMemo updateDrinksMemo(long id, String newDrinks, String newQuantity, String newTime, long newDate, boolean newChecked) {
        int intValueChecked = (newChecked)? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(DrinksMemoDbHelper.COLUMN_DRINKS, newDrinks);
        values.put(DrinksMemoDbHelper.COLUMN_QUANTITY, newQuantity);
        values.put(DrinksMemoDbHelper.COLUMN_TIME, newTime);
        values.put(DrinksMemoDbHelper.COLUMN_DATE, newDate);
        values.put(DrinksMemoDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(DrinksMemoDbHelper.TABLE_DRINKS_LIST,
                values,
                DrinksMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(DrinksMemoDbHelper.TABLE_DRINKS_LIST,
                columns, DrinksMemoDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        DrinksMemo drinksMemo = cursorToDrinksMemo(cursor);
        cursor.close();

        return drinksMemo;
    }

    private DrinksMemo cursorToDrinksMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DrinksMemoDbHelper.COLUMN_ID);
        int idDrinks = cursor.getColumnIndex(DrinksMemoDbHelper.COLUMN_DRINKS);
        int idTime   = cursor.getColumnIndex(DrinksMemoDbHelper.COLUMN_TIME);
        int idDate   = cursor.getColumnIndex(DrinksMemoDbHelper.COLUMN_DATE);
        int idQuantity = cursor.getColumnIndex(DrinksMemoDbHelper.COLUMN_QUANTITY);
        int idChecked = cursor.getColumnIndex(DrinksMemoDbHelper.COLUMN_CHECKED);

        String drinks = cursor.getString(idDrinks);
        String quantity = cursor.getString(idQuantity);
        String time     = cursor.getString (idTime);
        long date       = cursor.getLong(idDate);
        long id = cursor.getLong(idIndex);
// ==========================================================
// WARUM IST DIES INT????? SOLLTE DIES NICHT BOOLEAN SEIN???
        int StringValueChecked = cursor.getInt(idChecked);
// ==========================================================

        boolean isChecked = (StringValueChecked != 0);

        DrinksMemo drinksMemo = new DrinksMemo(drinks, quantity, id, time, date, isChecked);

        return drinksMemo;
    }

    public List<DrinksMemo> getIntervalDrinksMemos(long first,long last) {
        List<DrinksMemo> drinksMemoList = new ArrayList<>();

        Cursor cursor = database.query(DrinksMemoDbHelper.TABLE_DRINKS_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        DrinksMemo drinksMemo;

        while(!cursor.isAfterLast()) {
            drinksMemo = cursorToDrinksMemo(cursor);
            if (first <= drinksMemo.getDate()&&last >= drinksMemo.getDate()) {drinksMemoList.add(drinksMemo);}
            Log.d(LOG_TAG, "ID: " + drinksMemo.getId() + ", Inhalt: " + drinksMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return drinksMemoList;
    }

}
