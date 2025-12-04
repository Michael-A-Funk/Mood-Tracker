// REVISION

package de.psychology.mooddiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* ==================================================================================================
CLASS:  PositiveMemoDbHelper
UTILITY: Create the data table for the PositiveMemo record and has will update the version if there is a newer one
   ==================================================================================================*/

public class PositiveMemoDbHelper extends SQLiteOpenHelper{

    //LOG_Tags: Facility to insert Log-protocols
    private static final String LOG_TAG = PositiveMemoDbHelper.class.getSimpleName();

    // File name of the database
    public static final String DB_NAME = "positive_list.db";

    // Version number of the SQLight
    public static final int DB_VERSION = 2;

    // Table name of the database
    public static final String TABLE_POSITIVE_LIST = "positive_list";

    // The columns of the database
    public static final String COLUMN_ID = "_id";                           // Identifier
    public static final String COLUMN_SPORT = "sport";                      // Boolean for sport
    public static final String COLUMN_DIET = "diet";                        // Boolean for diet
    public static final String COLUMN_TIME = "time";                        // HH:mm daytime when the mood was introduced
    public static final String COLUMN_DATE = "date";                        // Integer =yyyy*1000+MM*100+dd of the date "yyyyMMdd" when the mood was introduced
    public static final String COLUMN_MINDFULLNESS = "mindfullness";        // Boolean for mindfullness
    public static final String COLUMN_JOURNALING = "journaling";            // Boolean for journaling
    public static final String COLUMN_CHECKED = "checked";                  // Auxiliary for handling the Menuetable of XML



    public static final String SQL_CREATE;                                  // String for a SQL Command to create the table

    static {
        SQL_CREATE = "CREATE TABLE " + TABLE_POSITIVE_LIST +                // String concat for the table creating command
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Keyword by auto-incrementing primary key
                COLUMN_DATE + " INTEGER NOT NULL, " +                       // Integer obligatory column for "Date"
                COLUMN_TIME + " TEXT NOT NULL, " +                          // Text obligatory column for "Time"
                COLUMN_SPORT + " BOOLEAN NOT NULL DEFAULT 0, " +            // Boolean obligatory column for "Sport"
                COLUMN_DIET + " TEXT NOT NULL, " +                          // Text obligatory column for "Diet"
                COLUMN_MINDFULLNESS + " BOOLEAN NOT NULL DEFAULT 0, " +     // Boolean obligatory column for "Mindfullness"
                COLUMN_JOURNALING + " BOOLEAN NOT NULL DEFAULT 0, " +       // Boolean obligatory column for "Journaling"
                COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT 0);";           // Boolean obligatory column for "Checked"
    }

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_POSITIVE_LIST;  // String for a SQL Command to drop the table if it exists

    //super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
    public PositiveMemoDbHelper(Context context) {
        //super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);   // Initialize by the superclass
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // onCreate-Method will create the data table if it still not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);                             // Create the table
        }
        catch (Exception ex) {                                  // Exception message
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
