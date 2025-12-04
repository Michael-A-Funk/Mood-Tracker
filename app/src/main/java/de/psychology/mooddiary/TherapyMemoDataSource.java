package de.psychology.mooddiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TherapyMemoDataSource  extends AppCompatActivity{

    private static final String LOG_TAG = TherapyMemoDataSource.class.getSimpleName();
    public long nextAlarm=-1;
    private SQLiteDatabase database;
    private TherapyMemoDbHelper dbHelper;

    public long lastId;

    private String[] columns = {
            TherapyMemoDbHelper.COLUMN_ID,
            TherapyMemoDbHelper.COLUMN_MEDICATION,
            TherapyMemoDbHelper.COLUMN_DOSIS,
            TherapyMemoDbHelper.COLUMN_PLAN_TIME,
            TherapyMemoDbHelper.COLUMN_CHECKED
    };

    public TherapyMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new TherapyMemoDbHelper(context);
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

    public TherapyMemo createMedicationMemo(String medication, String dosis, String plan_time) {
        ContentValues values = new ContentValues();

        values.put(TherapyMemoDbHelper.COLUMN_MEDICATION, medication);
        values.put(TherapyMemoDbHelper.COLUMN_DOSIS, dosis);
        values.put(TherapyMemoDbHelper.COLUMN_PLAN_TIME, plan_time);

        long insertId = database.insert(TherapyMemoDbHelper.TABLE_MEDICATION_LIST, null, values);

        Cursor cursor = database.query(TherapyMemoDbHelper.TABLE_MEDICATION_LIST,
                columns, TherapyMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        TherapyMemo therapyMemo = cursorToMedicationMemo(cursor);
        cursor.close();

        return therapyMemo;
    }

    public void deleteMedicationMemo(TherapyMemo therapyMemo) {
        long id = therapyMemo.getId();
        lastId = id;

        Log.d(LOG_TAG, "Alarm wird abgestelt von Uhrzeit: " + therapyMemo.getPlanTime());

        database.delete(TherapyMemoDbHelper.TABLE_MEDICATION_LIST,
                TherapyMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + therapyMemo.toString());
    }

    public TherapyMemo updateMedicationMemo(long id, String newMedication, String newDosis, String newPlanTime, boolean newChecked) {
        int intValueChecked = (newChecked) ? 1 : 0;
        ContentValues values = new ContentValues();
        values.put(TherapyMemoDbHelper.COLUMN_MEDICATION, newMedication);
        values.put(TherapyMemoDbHelper.COLUMN_DOSIS, newDosis);
        values.put(TherapyMemoDbHelper.COLUMN_PLAN_TIME, newPlanTime);
        values.put(TherapyMemoDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(TherapyMemoDbHelper.TABLE_MEDICATION_LIST,
                values,
                TherapyMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(TherapyMemoDbHelper.TABLE_MEDICATION_LIST,
                columns, TherapyMemoDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        TherapyMemo therapyMemo = cursorToMedicationMemo(cursor);
        cursor.close();

        return therapyMemo;
    }

    private TherapyMemo cursorToMedicationMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(TherapyMemoDbHelper.COLUMN_ID);
        int idMedication = cursor.getColumnIndex(TherapyMemoDbHelper.COLUMN_MEDICATION);
        int idDosis = cursor.getColumnIndex(TherapyMemoDbHelper.COLUMN_DOSIS);
        int idPlanTime = cursor.getColumnIndex(TherapyMemoDbHelper.COLUMN_PLAN_TIME);
        int idChecked = cursor.getColumnIndex(TherapyMemoDbHelper.COLUMN_CHECKED);

        String medication = cursor.getString(idMedication);
        String dosis = cursor.getString(idDosis);
        long id = cursor.getLong(idIndex);
        int StringValueChecked = cursor.getInt(idChecked);
        String planTime = cursor.getString(idPlanTime);

        boolean isChecked = (StringValueChecked != 0);

        TherapyMemo therapyMemo = new TherapyMemo(medication, dosis, id, planTime, isChecked);
        int this_time = Zeit.timeStamp(Zeit.now());

        lastId = id;
        nextAlarm = Zeit.timeStamp(planTime);
        Log.d(LOG_TAG, "nextAlarm = " + nextAlarm + " ; this_time = " + this_time);
        if (nextAlarm>this_time){ nextAlarm = Zeit.timeDayMillisec(nextAlarm) - Zeit.timeDayMillisec(this_time);}
        else  { nextAlarm = Zeit.timeDayMillisec(this_time) -  Zeit.timeDayMillisec(nextAlarm) + 24*3600*1000;}
        Log.d(LOG_TAG, "nextAlarm ---> After substraction = " + nextAlarm);
        //return
        return therapyMemo;
    }

    public List<TherapyMemo> getAllMedicationMemos() {
        List<TherapyMemo> therapyMemoList = new ArrayList<>();

        Cursor cursor = database.query(TherapyMemoDbHelper.TABLE_MEDICATION_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        TherapyMemo therapyMemo;

        while (!cursor.isAfterLast()) {
            therapyMemo = cursorToMedicationMemo(cursor);
            therapyMemoList.add(therapyMemo);
            cursor.moveToNext();
        }

        cursor.close();

        return therapyMemoList;
    }

    public long returnIndex() {

        return lastId;

    }

    // RESOLVED PROBLEM: GET ALL MEDICATION OF TODAY UNTIL NOW
        public List<TherapyMemo> getNextMedicationMemos() {
        int this_time = Zeit.timeStamp(Zeit.now());                            // Find the Medication for take after this momennt
        List<TherapyMemo> therapyMemoList = new ArrayList<>();                 // Empty list of founded Medications
        TherapyMemo therapyMemo;                                               // Medication Form

        // Define Cursor of DB-Access


            Cursor cursor = database.query(TherapyMemoDbHelper.TABLE_MEDICATION_LIST,
                    columns, null, null, null, null, null);
            cursor.moveToFirst();
            // In this condition the moment we search the earliest time for the medication of tomorrow
            while (!cursor.isAfterLast()) {
                therapyMemo = cursorToMedicationMemo(cursor);
                /*if ((nextAlarm<0 && timeStamp(therapyMemo.getPlanTime()) > this_time)
                    ||(nextAlarm>=0 && timeStamp(therapyMemo.getPlanTime())>this_time && timeStamp(therapyMemo.getPlanTime())<nextAlarm))
                {
                    nextAlarm=timeStamp(therapyMemo.getPlanTime());

                }*/

                // Only the medication of today until now (this_time)
                if (Zeit.timeStamp(therapyMemo.getPlanTime()) <= this_time) {
                    therapyMemoList.add(therapyMemo);

                }
                cursor.moveToNext();
            }

            cursor.close();

            Log.d(LOG_TAG, "nextAlarm = " + nextAlarm);
            if (nextAlarm!=-1) {

                // Log.d(LOG_TAG, "Funktion von Papa" + millisec_since_1970_until_today_hour(this_time) + millisec_since_1970_until_today_hour(nextAlarm - this_time));
                /*nextAlarm = timeDayMillisec(nextAlarm) - timeDayMillisec(this_time);
                /*Log.d(LOG_TAG, "nextAlarm = " + nextAlarm);*/
            }
            return therapyMemoList;
    }


    //Alarm Manager funktioniert nicht in eine Klasse ohne onCreate()
    /*private void activateAlarm() {

        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        int this_time = timeStamp(sdfTime.format(new Date()));
        List<TherapyMemo> therapyMemoList = getNextMedicationMemos();    // Get all Medications of the therapy

        if (therapyMemoList.size() > 0) {
            for (int i = 0; i < therapyMemoList.size(); i++) {
                if (timeStamp(therapyMemoList.get(i).getPlanTime()) == this_time) {
                    // With setInexactRepeating(), you have to use one of the AlarmManager interval
                    // constants--in this case, AlarmManager.INTERVAL_DAY.
                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent);
                    Intent formular = new Intent(getApplicationContext(), TakeMedication.class); // define the Intent formular
                    startActivity(formular);                        // To coll a new activity with this Intent

                }
            }
        }
    }*/
}