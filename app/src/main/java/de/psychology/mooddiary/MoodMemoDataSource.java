package de.psychology.mooddiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class MoodMemoDataSource {

    private static final String LOG_TAG = MoodMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private MoodMemoDbHelper dbHelper;

    private String[] columns = {
            MoodMemoDbHelper.COLUMN_ID,
            MoodMemoDbHelper.COLUMN_TIME,
            MoodMemoDbHelper.COLUMN_DATE,
            MoodMemoDbHelper.COLUMN_MOOD,
            MoodMemoDbHelper.COLUMN_FEAR,
            MoodMemoDbHelper.COLUMN_CHECKED,
            MoodMemoDbHelper.COLUMN_IRRITABILITY,
            MoodMemoDbHelper.COLUMN_DELUSION,
            MoodMemoDbHelper.COLUMN_STRESS,
            MoodMemoDbHelper.COLUMN_SLEEP_TIME,
            MoodMemoDbHelper.COLUMN_SLEEP_QUALITY,
            MoodMemoDbHelper.COLUMN_SLEEP_INTERRUPTIONS,
            MoodMemoDbHelper.COLUMN_ALCOHOL,
            MoodMemoDbHelper.COLUMN_DRUGS,
            MoodMemoDbHelper.COLUMN_MEMO,


    };

    public static boolean moodFirstRegistered = false;

    public MoodMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new MoodMemoDbHelper(context);
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


    public void deleteMoodMemo(MoodMemo moodMemo) {
        long id = moodMemo.getId();

        database.delete(MoodMemoDbHelper.TABLE_MOOD_LIST,
                MoodMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + moodMemo.toString() + "[=yyyyMMdd]");
    }

    public MoodMemo createMoodMemo(int mood, int fear, int irritability, boolean delusion, int stress, int sleepTime, int sleepQuality, boolean sleepInterruptions,
                                   boolean alcohol, boolean drugs, String memo) {
        ContentValues values = new ContentValues();
        String time = Zeit.now();
        long date = Zeit.today();

        values.put(MoodMemoDbHelper.COLUMN_TIME, time);
        values.put(MoodMemoDbHelper.COLUMN_DATE, date);
        values.put(MoodMemoDbHelper.COLUMN_MOOD, mood);
        values.put(MoodMemoDbHelper.COLUMN_FEAR, fear);
        values.put(MoodMemoDbHelper.COLUMN_IRRITABILITY, irritability);
        values.put(MoodMemoDbHelper.COLUMN_DELUSION, delusion);
        values.put(MoodMemoDbHelper.COLUMN_STRESS, stress);
        values.put(MoodMemoDbHelper.COLUMN_SLEEP_TIME, sleepTime);
        values.put(MoodMemoDbHelper.COLUMN_SLEEP_QUALITY, sleepQuality);
        values.put(MoodMemoDbHelper.COLUMN_SLEEP_INTERRUPTIONS, sleepInterruptions);
        values.put(MoodMemoDbHelper.COLUMN_ALCOHOL, alcohol);
        values.put(MoodMemoDbHelper.COLUMN_DRUGS, drugs);
        values.put(MoodMemoDbHelper.COLUMN_MEMO, memo);


        long insertId = database.insert(MoodMemoDbHelper.TABLE_MOOD_LIST, null, values);

        Cursor cursor = database.query(MoodMemoDbHelper.TABLE_MOOD_LIST,
                columns, MoodMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        MoodMemo moodMemo = cursorToMoodMemo(cursor);
        cursor.close();

        moodFirstRegistered = true;
        Log.d(LOG_TAG, "Wurde schon ein Wert in Mood eingegeben? " + moodFirstRegistered);

        return moodMemo;
    }

    public MoodMemo updateMoodMemo(String newTime, long newDate, long id, int newMood, int newFear, int newIrritability, boolean newDelusion, int newStress, int newSleepTime,
                                   int newSleepQuality, boolean newSleepInterruptions, boolean newAlcohol, boolean newDrugs, String newMemo, boolean newChecked) {

        int intValueChecked = (newChecked) ? 1 : 0;
        ContentValues values = new ContentValues();

        values.put(MoodMemoDbHelper.COLUMN_TIME, newTime);
        values.put(MoodMemoDbHelper.COLUMN_DATE, newDate);
        values.put(MoodMemoDbHelper.COLUMN_MOOD, newMood);
        values.put(MoodMemoDbHelper.COLUMN_FEAR, newFear);
        values.put(MoodMemoDbHelper.COLUMN_IRRITABILITY, newIrritability);
        values.put(MoodMemoDbHelper.COLUMN_DELUSION, newDelusion);
        values.put(MoodMemoDbHelper.COLUMN_STRESS, newStress);
        values.put(MoodMemoDbHelper.COLUMN_SLEEP_TIME, newSleepTime);
        values.put(MoodMemoDbHelper.COLUMN_SLEEP_QUALITY, newSleepQuality);
        values.put(MoodMemoDbHelper.COLUMN_SLEEP_INTERRUPTIONS, newSleepInterruptions);
        values.put(MoodMemoDbHelper.COLUMN_ALCOHOL, newAlcohol);
        values.put(MoodMemoDbHelper.COLUMN_DRUGS, newDrugs);
        values.put(MoodMemoDbHelper.COLUMN_MEMO, newMemo);
        values.put(MoodMemoDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(MoodMemoDbHelper.TABLE_MOOD_LIST,
                values,
                MoodMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(MoodMemoDbHelper.TABLE_MOOD_LIST,
                columns, MoodMemoDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        MoodMemo moodMemo = cursorToMoodMemo(cursor);
        cursor.close();


        return moodMemo;

    }

    private MoodMemo cursorToMoodMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_ID);
        int idTime = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_TIME);
        int idDate = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_DATE);
        int idMood = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_MOOD);
        int idFear = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_FEAR);
        int idIrritability = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_IRRITABILITY);
        int idDelusion = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_DELUSION);
        int idStress = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_STRESS);
        int idSleepTime = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_SLEEP_TIME);
        int idSleepQuality = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_SLEEP_QUALITY);
        int idSleepInterruptions = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_SLEEP_INTERRUPTIONS);
        int idAlcohol = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_ALCOHOL);
        int idDrugs = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_DRUGS);
        int idMemo = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_MEMO);
        int idChecked = cursor.getColumnIndex(MoodMemoDbHelper.COLUMN_CHECKED);

        String time = cursor.getString(idTime);
        long date = cursor.getLong(idDate);
        long id = cursor.getLong(idIndex);
        int mood = cursor.getInt(idMood);
        int fear = cursor.getInt(idFear);
        int irritability = cursor.getInt(idIrritability);
        boolean delusion = cursor.getInt(idDelusion) > 0;
        int stress = cursor.getInt(idStress);
        int sleepTime = cursor.getInt(idSleepTime);
        int sleepQuality = cursor.getInt(idSleepQuality);
        boolean sleepInterruptions = cursor.getInt(idSleepInterruptions) > 0;
        boolean alcohol = cursor.getInt(idAlcohol) > 0;
        boolean drugs = cursor.getInt(idDrugs) > 0;
        String memo = cursor.getString(idMemo);


// ==========================================================
// WARUM IST DIES INT????? SOLLTE DIES NICHT BOOLEAN SEIN???
        int StringValueChecked = cursor.getInt(idChecked);
// ==========================================================

        boolean isChecked = (StringValueChecked != 0);

        MoodMemo moodMemo = new MoodMemo(id, date, time, mood, fear, irritability, delusion, stress, sleepTime, sleepQuality, sleepInterruptions, alcohol, drugs, memo, isChecked);

        return moodMemo;
    }

    public List<MoodMemo> getIntervalMoodMemos(long first, long last) {
        List<MoodMemo> moodMemoList = new ArrayList<>();

        Cursor cursor = database.query(MoodMemoDbHelper.TABLE_MOOD_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        MoodMemo moodMemo;

        while (!cursor.isAfterLast()) {
            moodMemo = cursorToMoodMemo(cursor);
            if (first <= moodMemo.getDate() && last >= moodMemo.getDate()) moodMemoList.add(moodMemo);
            Log.d(LOG_TAG, "ID: " + moodMemo.getId() + ", Inhalt: " + moodMemo.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return moodMemoList;
    }

    /*  To get from the Mood Database an crescent ordered list of all Memos between the dates
        "first" and "last"
        ==========================================================================================
     */
    public List<MoodMemo> getSortIntervalMoodMemos(long first, long last) {
        // Space for colecting the MoodMemo List
        List<MoodMemo> moodMemoList = new ArrayList<>();

        // The dynamic cursor on the Database
        Cursor cursor = database.query(MoodMemoDbHelper.TABLE_MOOD_LIST,
                columns, null, null, null, null, null);

        // Initialisize the cursor on the beginning of the data base (Mood)
        cursor.moveToFirst();

        // Getting the variable moodMembo to having an actual memo-struct element in the hand
        MoodMemo moodMemo;

        // While the database has still an element
        while (!cursor.isAfterLast()) {
            // Identify the actual Mood elemento by the first cursor element
            moodMemo = cursorToMoodMemo(cursor);
            // If this dataelement inside the Intervall between the time intervall [first,last]
            if (first <= moodMemo.getDate() && last >= moodMemo.getDate()) {

                if (moodMemoList.isEmpty())
                    // If the list is empty
                    moodMemoList.add(moodMemo);                       // Add as first element
                else { // If not empty compare sucessively with the existing elements
                    int i = 0;                                        // Initialize the counter
                    int Stamp = Zeit.timeStamp(moodMemo.getTime());   // determine the time value of inserting element
                    while (i < moodMemoList.size()) {
                        if (moodMemo.getDate() < moodMemoList.get(i).getDate()) // When inserting element is on earlier day as actual one
                            break;                                              // We are at right point to insert the element
                        if (moodMemo.getDate() == moodMemoList.get(i).getDate() && Stamp < Zeit.timeStamp(moodMemoList.get(i).getTime()))
                            // When both on same day but the inserting on earlier hour
                            break;                                              // We are at right point to insert the element
                        ++i;                                                    // In all other cases proceede
                        }
                    moodMemoList.add(i, moodMemo);                     // Insert the data at the point where we stopped to pass
                    }
                }
            // Log Data
            Log.d(LOG_TAG, "ID: " + moodMemo.getId() + ", Inhalt: " + moodMemo.toString());
            // Go to the next element in the data base
            cursor.moveToNext();
        }
        // There are no more elemente, so we close the data base
        cursor.close();
        // Return the ordered List of Memos in the date between "first" and "last"
        return moodMemoList;
    }
    // ==========================================================================================

}