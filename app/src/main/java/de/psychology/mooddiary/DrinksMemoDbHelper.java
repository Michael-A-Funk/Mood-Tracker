package de.psychology.mooddiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;                  // SQLite Data basefunctions
import android.database.sqlite.SQLiteOpenHelper;                // SQLite Data base auxiliary functions
import android.util.Log;

/* ==================================================================================================
CLASS:  DrinksMemoDbHelper
UTILITY: Create the datatable for the DrinksMemo record and has will update the version if there is a newer one
   ==================================================================================================*/
public class DrinksMemoDbHelper extends SQLiteOpenHelper{

    //LOG_Tags: Facility to insert Log-protocols
    private static final String LOG_TAG = DrinksMemoDbHelper.class.getSimpleName();

    // File name of the database
    public static final String DB_NAME = "drinks_list.db";
    // Version number of the SQLight
    public static final int DB_VERSION = 2;

    // Table name of the database
    public static final String TABLE_DRINKS_LIST = "drinks_list";

    // The columns of the database
    public static final String COLUMN_ID = "_id";                   // Identifier
    public static final String COLUMN_DRINKS = "drinks";            // Name of the liquid was taken
    public static final String COLUMN_QUANTITY = "quantity";        // Quantity of the liquid was taken
    public static final String COLUMN_TIME = "time";                // HH:mm daytime when the liquid is taken
    public static final String COLUMN_DATE = "date";                // Integer =yyyy*1000+MM*100+dd of the date "yyyyMMdd" when the liquid was taken
    public static final String COLUMN_CHECKED = "checked";          // Auxiliary for handling the Menuetable of XML


    public static final String SQL_CREATE;                          // String for a SQL Command to create the table

    static {
        SQL_CREATE = "CREATE TABLE " + TABLE_DRINKS_LIST +                  // String concat for the table creating command
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Keyword by auto-incrementing primary key
                COLUMN_DRINKS + " TEXT NOT NULL, " +                        // Text obligatory column for "drinks"
                COLUMN_QUANTITY + " TEXT NOT NULL, " +                      // Text obligatory column for "quantity"
                COLUMN_TIME + " TEXT NOT NULL, " +                          // Text obligatory column for "time"
                COLUMN_DATE + " INTEGER NOT NULL, " +                       // Integer obligatory column for "date"
                COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT 0);";           // Boolean for "checked"
    }

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_DRINKS_LIST; // String for a SQL Command to drop the table if it exists


    public DrinksMemoDbHelper(Context context) {
        //super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);                   // Initialize by the superclass
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // onCreate-Method will create the data table if it still not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);                   // Create the table
        }
        catch (Exception ex) {                        // Exception message
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    // onUpgrade-Method will be automatically called if the by changing the version an update will be needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Die Tabelle mit Versionsnummer " + oldVersion + " wird entfernt.");
        db.execSQL(SQL_DROP);                         // Drop the
        onCreate(db);
    }
}
