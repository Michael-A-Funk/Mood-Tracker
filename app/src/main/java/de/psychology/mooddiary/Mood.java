package de.psychology.mooddiary;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log; // Somit können wir die Logcat Tool Window verwenden

// Die benötigten Import-Anweisungen zum Sichtbarmachen der Klassen bzw. Interfaces ArrayAdapter
// ListView und List
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

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


public class Mood extends AppCompatActivity{

    public static final String LOG_TAG = Mood.class.getSimpleName();

    private MoodMemoDataSource dataSource;

    private ListView mMoodMemosListView; //Deklarieren des ListViews als Membervariable.

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
        setContentView(R.layout.mood); // UI Layout aus der erstellen XML-Layout geladen
              Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.>>>>");

        // Verbindung zu Data Acess Object der Datenbank
        dataSource = new MoodMemoDataSource(this);

        /*Log.d(LOG_TAG, "moodFirstRegistered = " + dataSource.moodFirstRegistered);
        if (dataSource.moodFirstRegistered==true)
        {   List<MoodMemo> moodMemoList = dataSource.getAllMoodMemos();

        }*/

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

        dataSource.getIntervalMoodMemos(Zeit.today(),Zeit.today());


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
        Button buttonAddMood = (Button) findViewById(R.id.button_add_mood);
        buttonAddMood.setText(R.string.button_finished);
        final Spinner spinnerMood = findViewById(R.id.spinnerMood);
        final Spinner spinnerFear = findViewById(R.id.spinnerFear);
        final Spinner spinnerIrritability = findViewById(R.id.spinnerIrritability);
        final CheckBox checkBosDelusion = findViewById(R.id.checkBoxDelusion);
        final Spinner spinnerStress = findViewById(R.id.spinnerStress);
        final EditText editTextSleepTime = (EditText) findViewById(R.id.editTextSleepTime);
        final Spinner spinnerSleepQuality = findViewById(R.id.spinnerSleepQuality);
        final CheckBox checkBoxSleepInterruptions = findViewById(R.id.checkBoxSleepInterruptions);
        final CheckBox checkBoxAlcohol = findViewById(R.id.checkBoxAlcohol);
        final CheckBox checkBoxDrugs = findViewById(R.id.checkBoxDrugs);
        final EditText editTextMemo = (EditText) findViewById(R.id.editTextMemo);

        //create lists of items for the spinners.
        //final String defaultStringMood =
        CharSequence selectCharMood = getText(R.string.spinner_mood);
        String selectMood= selectCharMood.toString();
        //create lists of items for the spinners.
        final String[] moodString = new String[]{"-3","-2","-1","0","1","2","3",selectMood}; // Wenn man mehrere Sprachen in der Liste im Handy hat wird der moodSelect nur bein Englisch angezeigt
        final int listsizeMood = moodString.length - 1;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterMood = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, moodString){
            @Override
            public int getCount() {
                return(listsizeMood); // Truncate the list
            }
        };
        adapterMood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        spinnerMood.setAdapter(adapterMood);
        //spinnerMood.setPrompt(getText(R.string.spinner_mood));
        spinnerMood.setSelection(listsizeMood);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        CharSequence selectCharFear = getText(R.string.spinner_fear);
        String selectFear= selectCharFear.toString();
        //create lists of items for the spinners.
        final String[] fearString = new String[]{"0","1","2","3",selectFear};
        final int listsizeFear = fearString.length - 1;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterFear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fearString){
            @Override
            public int getCount() {
                return(listsizeFear); // Truncate the list
            }
        };
        adapterFear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        spinnerFear.setAdapter(adapterFear);
        //spinnerFear.setPrompt("* Fear");
        spinnerFear.setSelection(listsizeFear);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        CharSequence selectCharIrritability = getText(R.string.spinner_irritability);
        String selectIrritability= selectCharIrritability.toString();
        //create lists of items for the spinners.
        final String[] irritabilityString = new String[]{"0","1","2","3",selectIrritability};
        final int listsizeIrritability = irritabilityString.length - 1;
        ArrayAdapter<String> adapterIrritability = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, irritabilityString){
            @Override
            public int getCount() {
                return(listsizeIrritability); // Truncate the list
            }
        };
        adapterIrritability.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        spinnerIrritability.setAdapter(adapterIrritability);
        //spinnerIrritability.setPrompt("* Irritability");
        spinnerIrritability.setSelection(listsizeIrritability);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        CharSequence selectCharStress = getText(R.string.spinner_stress);
        String selectStress= selectCharStress.toString();
        //create lists of items for the spinners.
        final String[] stressString = new String[]{"0","1","2","3",selectStress};
        final int listSizeStress = stressString.length - 1;
        ArrayAdapter<String> adapterStress = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stressString){
            @Override
            public int getCount() {
                return(listSizeStress); // Truncate the list
            }
        };
        adapterStress.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        spinnerStress.setAdapter(adapterStress);
        //spinnerStress.setPrompt("* Stress");
        spinnerStress.setSelection(listSizeStress);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        //create lists of items for the spinners.
        CharSequence selectCharSleepQuality = getText(R.string.spinner_sleep_quality);
        String selectSleepQuality= selectCharSleepQuality.toString();
        final String[] stringSleepQuality = new String[]{"-2","-1","0","1","2",selectSleepQuality};
        final int listSleepQuality = stringSleepQuality.length - 1;
        ArrayAdapter<String> adapterSleepQuality = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stringSleepQuality){
            @Override
            public int getCount() {
                return(listSleepQuality); // Truncate the list
            }
        };
        adapterSleepQuality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        spinnerSleepQuality.setAdapter(adapterSleepQuality);
        //spinnerSleepQuality.setPrompt("* Sleep Quality");
        spinnerSleepQuality.setSelection(listSleepQuality);


        buttonAddMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence selectCharMood = getText(R.string.spinner_mood);
                String selectMood= selectCharMood.toString();
                CharSequence selectCharFear = getText(R.string.spinner_fear);
                String selectFear= selectCharFear.toString();
                CharSequence selectCharIrritability = getText(R.string.spinner_irritability);
                String selectIrritability= selectCharIrritability.toString();
                CharSequence selectCharStress = getText(R.string.spinner_stress);
                String selectStress= selectCharStress.toString();
                CharSequence selectCharSleepQuality = getText(R.string.spinner_sleep_quality);
                String selectSleepQuality= selectCharSleepQuality.toString();

                if ((spinnerMood.getSelectedItem().toString() != selectMood) && (spinnerFear.getSelectedItem().toString() != selectFear) &&
                        (spinnerIrritability.getSelectedItem().toString() != selectIrritability) && (spinnerStress.getSelectedItem().toString() !=selectStress) &&
                        (spinnerSleepQuality.getSelectedItem().toString() !=selectSleepQuality)) {
                    String moodString = spinnerMood.getSelectedItem().toString();
                    int mood = Integer.parseInt(moodString);
                    String fearString = spinnerFear.getSelectedItem().toString();
                    int fear = Integer.parseInt(fearString);
                    String irritabilityString = spinnerIrritability.getSelectedItem().toString();
                    int irritability = Integer.parseInt(irritabilityString);
                    boolean delusion = checkBosDelusion.isChecked();
                    String stressString = spinnerStress.getSelectedItem().toString();
                    int stress = Integer.parseInt(stressString);
                    String sleepQualityString = spinnerSleepQuality.getSelectedItem().toString();
                    int sleepQuality = Integer.parseInt(sleepQualityString);
                    boolean sleepInterruptions = checkBoxSleepInterruptions.isChecked();
                    boolean alcohol = checkBoxAlcohol.isChecked();
                    boolean drugs = checkBoxDrugs.isChecked();

                    String sleepTimeString = editTextSleepTime.getText().toString();
                    int sleepTime;
                    if (sleepTimeString.length() != 0) {
                        sleepTime = Integer.parseInt(sleepTimeString);
                    } else {
                        sleepTime = -1;
                    }

                    String memo = editTextMemo.getText().toString();

                    editTextSleepTime.setText("");
                    editTextMemo.setText("");

                    // Muss hier diferenzieren zwischen erster Eintrag und nächste, und bei nächsten muss ich updateMoodMemo verwenden!!!!
                    final MoodMemo moodMemo = dataSource.createMoodMemo(mood, fear, irritability, delusion, stress, sleepTime, sleepQuality, sleepInterruptions, alcohol, drugs, memo);
                    Log.d(LOG_TAG, "Folgende Werte wurden in der Mood Datenbank angelegt." + moodMemo.toString());

                    InputMethodManager inputMethodManager;
                    inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null) {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                    dataSource.getIntervalMoodMemos(Zeit.today(), Zeit.today());

                    Intent formular = new Intent(getApplicationContext(), Positive.class); // define the Intent formular
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