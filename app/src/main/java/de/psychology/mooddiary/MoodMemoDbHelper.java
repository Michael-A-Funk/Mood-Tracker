package de.psychology.mooddiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;                  // SQLite Data basefunctions
import android.database.sqlite.SQLiteOpenHelper;                // SQLite Data base auxiliary functions
import android.util.Log;

/* ==================================================================================================
CLASS:  DrinksMemoDbHelper
UTILITY: Create the data table for the MoodMemo record and has will update the version if there is a newer one
   ==================================================================================================*/
public class MoodMemoDbHelper extends SQLiteOpenHelper{
    //LOG_Tags: Facility to insert Log-protocols
    private static final String LOG_TAG = MoodMemoDbHelper.class.getSimpleName();

    // File name of the database
    public static final String DB_NAME = "mood_list.db";
    // Version number of the SQLight
    public static final int DB_VERSION = 2;

    // Table name of the database
    public static final String TABLE_MOOD_LIST = "mood_list";

    // The columns of the database
    public static final String COLUMN_ID = "_id";                           // Identifier
    public static final String COLUMN_DATE = "date";                        // Integer =yyyy*1000+MM*100+dd of the date "yyyyMMdd" when the mood was introduced
    public static final String COLUMN_TIME = "time";                        // HH:mm daytime when the mood was introduced
    public static final String COLUMN_MOOD = "mood";                        // Value for the Mood
    public static final String COLUMN_FEAR = "fear";                        // Value for the Fear
    public static final String COLUMN_IRRITABILITY= "irritability";         // Value for the Irritability
    public static final String COLUMN_DELUSION = "delusion";                // Value for the Delusion
    public static final String COLUMN_STRESS = "stress";                    // Value for the Stress
    public static final String COLUMN_SLEEP_TIME = "sleep_time";            // Value for the hours of sleeping
    public static final String COLUMN_SLEEP_QUALITY = "sleep_quality";      // Value for the quality of sleep
    public static final String COLUMN_SLEEP_INTERRUPTIONS = "sleep_interruptions"; // Boolean if the sleep was interrupted
    public static final String COLUMN_ALCOHOL = "alcohol";                  // Boolean if was taken alcohol
    public static final String COLUMN_DRUGS = "drugs";                      // Boolean if was taken drugs
    public static final String COLUMN_MEMO = "memo";                        // Text for making a memorandum
    public static final String COLUMN_CHECKED = "checked";                  // Auxiliary for handling the Menuetable of XML



    public static final String SQL_CREATE;                                  // String for a SQL Command to create the table

    static {
        SQL_CREATE = "CREATE TABLE " + TABLE_MOOD_LIST +                    // String concat for the table creating command
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Keyword by auto-incrementing primary key
                COLUMN_DATE + " INTEGER NOT NULL, " +                       // Integer obligatory column for "date"
                COLUMN_TIME + " TEXT NOT NULL, " +                          // Text obligatory column for "time"
                COLUMN_MOOD + " INTEGER NOT NULL, " +                       // Integer obligatory for "mood"
                COLUMN_FEAR + " INTEGER NOT NULL, " +                       // Integer obligatory for "fear"
                COLUMN_IRRITABILITY + " INTEGER NOT NULL, " +               // Integer obligatory for "irritability"
                COLUMN_DELUSION + " BOOLEAN NOT NULL DEFAULT 0, " +         // Boolean obligatory for "delution"
                COLUMN_STRESS + " INTEGER NOT NULL, " +                     // Integer obligatory for "stress"
                COLUMN_SLEEP_TIME + " INTEGER NOT NULL, " +                 // Integer obligatory for "sleep_time"
                COLUMN_SLEEP_QUALITY + " INTEGER NOT NULL, " +              // Integer obligatory for "sleep_quantity"
                COLUMN_SLEEP_INTERRUPTIONS + " BOOLEAN NOT NULL DEFAULT 0, " + // Boolean obligatory for "sleep_interruptions"
                COLUMN_ALCOHOL + " BOOLEAN NOT NULL DEFAULT 0, " +          // Boolean obligatory for "alcohol"
                COLUMN_DRUGS  + " BOOLEAN NOT NULL DEFAULT 0, " +           // Boolean obligatory for "drugs"
                COLUMN_MEMO + " TEXT NOT NULL, " +                          // Text obligatory column for "memo"
                COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT 0);";           // Boolean obligatory for "checked"
    }

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_MOOD_LIST; // String for a SQL Command to drop the table if it exists


    public MoodMemoDbHelper(Context context) {
        //super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);              // Initialize by the superclass
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // onCreate-Method will create the data table if it still not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);                                     // Create the table
        }
        catch (Exception ex) {                                          // Exception message
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    // onUpgrade-Method will be automatically called if the by changing the version an update will be needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Die Tabelle mit Versionsnummer " + oldVersion + " wird entfernt.");
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

}
