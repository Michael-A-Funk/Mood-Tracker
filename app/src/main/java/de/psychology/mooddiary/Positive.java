package de.psychology.mooddiary;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log; // Somit können wir die Logcat Tool Window verwenden

// Die benötigten Import-Anweisungen zum Sichtbarmachen der Klassen bzw. Interfaces ArrayAdapter
// ListView und List
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//Die benötigten Import-Anweisungen zum Sichtbarmachen der Klassen bzw. Interfaces
// InputMethodManager, TextUtils, View, Button und EditText.
import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//Die benötigten Import-Anweisungen zum Sichtbarmachen der Klassen
// bzw. Interfaces SparseBooleanArray, ActionMode, Menu, MenuItem und AbsListView.
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;


//Die benötigten Import-Anweisungen zum Sichtbarmachen der Klassen bzw. Interfaces DialogInterface,
// AlertDialog und LayoutInflater.
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

//Importieren der benötigten Klassen.
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import java.util.ArrayList;
import android.widget.Spinner;
import java.lang.Integer;

public class Positive extends AppCompatActivity  {

    public static final String LOG_TAG = Positive.class.getSimpleName();

    private PositiveMemoDataSource dataSource;

    private ListView mPositiveMemosListView; //Deklarieren des ListViews als Membervariable.

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // Initialisierung der Activity
        setContentView(R.layout.positive); // UI Layout aus der erstellen XML-Layout geladen
        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        // Verbindung zu Data Acess Object der Datenbank
        dataSource = new PositiveMemoDataSource(this);

        activateAddButton();
    }
// Die neu angelegten Callback-Methoden onResume() und onPause(), durch die wir die Verbindung
        // zur SQLite Datenbank verwalten lassen.
        @Override
        protected void onResume() {
            super.onResume();

            Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
            dataSource.open(); // Öffnung der Datenquelle mit der Hilfe des dataSource Objekt vom Typ
            //MoodMemoDataSource
            //List<MoodMemo> drinksMemoList = dataSource.getAllMoodMemos();


        }
        @Override
        protected void onPause() {
            super.onPause();

            Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
            dataSource.close();
        }



// Activate Add Button  -  Here the elements are added to the database.
// Essential for the adding of Entries on DataBase

//Die Methode activateAddButton(), durch die wir den OnClickListener für den Add-Button registrieren
//und die Inhalte der beiden Textfelder auslesen und in die Datenbank schreiben lassen.

        private void activateAddButton() {
            Button buttonAddPositive = findViewById(R.id.button_add_positive);
            buttonAddPositive.setText(R.string.button_finished);
            final Spinner spinnerDiet = (Spinner)findViewById(R.id.spinnerDiet);
            final CheckBox checkBoxSport = findViewById(R.id.checkBoxSport);
            final CheckBox checkBoxMindfullness = findViewById(R.id.checkBoxMindfullness);
            final CheckBox checkBoxJournaling = findViewById(R.id.checkBoxJournaling);

            //create lists of items for the spinners.
            CharSequence selectCharDiet = getText(R.string.spinner_diet);
            String selectDiet= selectCharDiet.toString();
            final String[] dietString = new String[]{"-1","0","1", selectDiet};
            final int listsizeDiet= dietString.length - 1;
            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
            //There are multiple variations of this, but this is the basic variant.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dietString){
            @Override
            public int getCount() {
                return(listsizeDiet); // Truncate the list
               }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //set the spinners adapter to the previously created one.
            spinnerDiet.setAdapter(adapter);
            //spinner_Diet.setPrompt("* Diet");
            spinnerDiet.setSelection(listsizeDiet);

            buttonAddPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence selectCharDiet = getText(R.string.spinner_irritability);
                    String selectDiet = selectCharDiet.toString();
                    if (spinnerDiet.getSelectedItem().toString() != selectDiet) {

                        boolean sport = checkBoxSport.isChecked();
                        String moodString = spinnerDiet.getSelectedItem().toString();
                        int diet = Integer.parseInt(moodString);
                        boolean mindfullness = checkBoxMindfullness.isChecked();
                        boolean journaling = checkBoxJournaling.isChecked();

                        final PositiveMemo positiveMemo = dataSource.createPositiveMemo(sport, diet, mindfullness, journaling);
                        Log.d(LOG_TAG, "Folgende Werte wurden in der Mood Datenbank angelegt." + positiveMemo.toString());

                        InputMethodManager inputMethodManager;
                        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (getCurrentFocus() != null) {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }

                        Intent formular = new Intent(getApplicationContext(), MainActivity.class); // define the Intent formular
                        startActivity(formular);                        // To coll a new activity with this Intent


                    }
                }
            });


        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        switch(item.getItemId()) {
            case R.id.action_main:
                Intent formular_main = new Intent(getApplicationContext(), MainActivity.class); // define the Intent formular
                startActivity(formular_main);
                return true;
            case R.id.action_take_medication:
                Intent formular_take_medication = new Intent(getApplicationContext(), TakeMedication.class); // define the Intent formular
                startActivity(formular_take_medication);
                return true;
            case R.id.action_take_drinks:
                Intent formular_drinks = new Intent(getApplicationContext(), TakeDrinks.class); // define the Intent formular
                startActivity(formular_drinks);                        // To coll a new activity with this Intent
                return true;
            case R.id.action_insert_medication:
                Intent formular_therapy = new Intent(getApplicationContext(), Therapy.class); // define the Intent formular
                startActivity(formular_therapy);                        // To coll a new activity with this Intent
                return true;
            case R.id.action_mood_main:
                Intent formular_mood = new Intent(getApplicationContext(), Mood.class); // define the Intent formular
                startActivity(formular_mood);                        // To coll a new activity with this Intent
                return true;
            case R.id.action_positive_main:
                Intent formular_positive = new Intent(getApplicationContext(), Positive.class); // define the Intent formular
                startActivity(formular_positive);                        // To coll a new activity with this Intent
                return true;
            case R.id.action_pdf_main:
                Intent formular_print_pdf = new Intent(getApplicationContext(), PrintPdf.class); // define the Intent formular
                startActivity(formular_print_pdf);                        // To coll a new activity with this Intent
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    }
