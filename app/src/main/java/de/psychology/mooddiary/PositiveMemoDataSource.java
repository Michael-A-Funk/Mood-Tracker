package de.psychology.mooddiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PositiveMemoDataSource {

    private static final String LOG_TAG = PositiveMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private PositiveMemoDbHelper dbHelper;

    private String[] columns = {
            PositiveMemoDbHelper.COLUMN_ID,
            PositiveMemoDbHelper.COLUMN_DATE,
            PositiveMemoDbHelper.COLUMN_TIME,
            PositiveMemoDbHelper.COLUMN_SPORT,
            PositiveMemoDbHelper.COLUMN_MINDFULLNESS,
            PositiveMemoDbHelper.COLUMN_JOURNALING,
            PositiveMemoDbHelper.COLUMN_DIET,
            PositiveMemoDbHelper.COLUMN_CHECKED
    };

    public PositiveMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new PositiveMemoDbHelper(context);
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


    public void deletePositiveMemo(PositiveMemo positiveMemo) {
        long id = positiveMemo.getId();

        database.delete(PositiveMemoDbHelper.TABLE_POSITIVE_LIST,
                PositiveMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + positiveMemo.toString() + "[=yyyyMMdd]");
    }

    public PositiveMemo createPositiveMemo(boolean sport, int diet, boolean mindfullness, boolean journaling) {
        ContentValues values = new ContentValues();
        String time = Zeit.now();
        long date = Zeit.today();

        values.put(PositiveMemoDbHelper.COLUMN_DATE, date);
        values.put(PositiveMemoDbHelper.COLUMN_TIME, time);
        values.put(PositiveMemoDbHelper.COLUMN_SPORT, sport);
        values.put(PositiveMemoDbHelper.COLUMN_DIET, diet);
        values.put(PositiveMemoDbHelper.COLUMN_MINDFULLNESS, mindfullness);
        values.put(PositiveMemoDbHelper.COLUMN_JOURNALING, journaling);



        long insertId = database.insert(PositiveMemoDbHelper.TABLE_POSITIVE_LIST, null, values);

        Cursor cursor = database.query(PositiveMemoDbHelper.TABLE_POSITIVE_LIST,
                columns, PositiveMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        PositiveMemo positiveMemo = cursorToPositiveMemo(cursor);
        cursor.close();

        return positiveMemo;
    }

    public PositiveMemo updatePositiveMemo(long id, long newDate, String newTime, boolean newSport, int newDiet, boolean newMindfullness, boolean newJournaling,  boolean newChecked) {
        int intValueChecked = (newChecked)? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(PositiveMemoDbHelper.COLUMN_SPORT, newSport);
        values.put(PositiveMemoDbHelper.COLUMN_DIET, newDiet);
        values.put(PositiveMemoDbHelper.COLUMN_TIME, newTime);
        values.put(PositiveMemoDbHelper.COLUMN_DATE, newDate);
        values.put(PositiveMemoDbHelper.COLUMN_MINDFULLNESS, newMindfullness);
        values.put(PositiveMemoDbHelper.COLUMN_JOURNALING, newJournaling);
        values.put(PositiveMemoDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(PositiveMemoDbHelper.TABLE_POSITIVE_LIST,
                values,
                PositiveMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(PositiveMemoDbHelper.TABLE_POSITIVE_LIST,
                columns, PositiveMemoDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        PositiveMemo positiveMemo = cursorToPositiveMemo(cursor);
        cursor.close();

        return positiveMemo;
    }

    private PositiveMemo cursorToPositiveMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_ID);
        int idSport = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_SPORT);
        int idTime   = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_TIME);
        int idDate   = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_DATE);
        int idDiet = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_DIET);
        int idChecked = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_CHECKED);
        int idMindfullness = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_MINDFULLNESS);
        int idJournaling = cursor.getColumnIndex(PositiveMemoDbHelper.COLUMN_JOURNALING);

        boolean sport = cursor.getInt(idSport) > 0;
        int diet = cursor.getInt(idDiet);
        boolean mindfullness = cursor.getInt(idMindfullness) > 0;
        boolean journaling = cursor.getInt(idJournaling) > 0;
        String time     = cursor.getString (idTime);
        long date       = cursor.getLong(idDate);
        long id = cursor.getLong(idIndex);
// ==========================================================
// WARUM IST DIES INT????? SOLLTE DIES NICHT BOOLEAN SEIN???
        int StringValueChecked = cursor.getInt(idChecked);
// ==========================================================

        boolean isChecked = (StringValueChecked != 0);

        PositiveMemo positiveMemo = new PositiveMemo(id, date, time, sport, diet, mindfullness, journaling, isChecked);

        return positiveMemo;
    }

    public List<PositiveMemo> getIntervalPositiveMemos(long first, long last) {
        List<PositiveMemo> positiveMemoList = new ArrayList<>();

        Cursor cursor = database.query(PositiveMemoDbHelper.TABLE_POSITIVE_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        PositiveMemo positiveMemo;

        while(!cursor.isAfterLast()) {
            positiveMemo = cursorToPositiveMemo(cursor);
            if (first <= positiveMemo.getDate() && last >= positiveMemo.getDate()) positiveMemoList.add(positiveMemo);
            Log.d(LOG_TAG, "ID: " + positiveMemo.getId() + ", Inhalt: " + positiveMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return positiveMemoList;
    }
    /*  To get from the Mood Database an crescent ordered list of all Memos between the dates
    "first" and "last"
    ==========================================================================================
 */
    public List<PositiveMemo> getSortIntervalPositiveMemos(long first, long last) {
        // Space for colecting the MoodMemo List
        List<PositiveMemo> positiveMemoList = new ArrayList<>();

        // The dynamic cursor on the Database
        Cursor cursor = database.query(PositiveMemoDbHelper.TABLE_POSITIVE_LIST,
                columns, null, null, null, null, null);
        // Initialisize the cursor on the beginning of the data base (Mood)
        cursor.moveToFirst();

        // Getting the variable moodMembo to having an actual memo-struct element in the hand
        PositiveMemo positiveMemo;

        // While the database has still an element
        while (!cursor.isAfterLast()) {
            // Identify the actual Mood elemento by the first cursor element
            positiveMemo = cursorToPositiveMemo(cursor);
            // If this dataelement inside the Intervall between the time intervall [first,last]
            if (first <= positiveMemo.getDate() && last >= positiveMemo.getDate()) {

                if (positiveMemoList.isEmpty())
                    // If the list is empty
                    positiveMemoList.add(positiveMemo);                       // Add as first element
                else { // If not empty compare sucessively with the existing elements
                    int i = 0;                                        // Initialize the counter
                    int Stamp = Zeit.timeStamp(positiveMemo.getTime());   // determine the time value of inserting element
                    while (i < positiveMemoList.size()) {
                        if (positiveMemo.getDate() < positiveMemoList.get(i).getDate()) // When inserting element is on earlier day as actual one
                            break;                                              // We are at right point to insert the element
                        if (positiveMemo.getDate() == positiveMemoList.get(i).getDate() && Stamp < Zeit.timeStamp(positiveMemoList.get(i).getTime()))
                            // When both on same day but the inserting on earlier hour
                            break;                                              // We are at right point to insert the element
                        ++i;                                                    // In all other cases proceede
                    }
                    positiveMemoList.add(i, positiveMemo);                     // Insert the data at the point where we stopped to pass
                }
            }
            // Log Data
            Log.d(LOG_TAG, "ID: " + positiveMemo.getId() + ", Inhalt: " + positiveMemo.toString());
            // Go to the next element in the data base
            cursor.moveToNext();
        }
        // There are no more elemente, so we close the data base
        cursor.close();
        // Return the ordered List of Memos in the date between "first" and "last"
        return positiveMemoList;
    }
    // ==========================================================================================
}