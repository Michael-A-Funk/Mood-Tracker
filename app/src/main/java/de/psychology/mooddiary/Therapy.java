package de.psychology.mooddiary;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.util.Log; // Somit können wir die Logcat Tool Window verwenden

// Die benötigten Import-Anweisungen zum Sichtbarmachen der Klassen bzw. Interfaces ArrayAdapter
// ListView und List
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
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
import android.content.Intent;
import java.util.Calendar;


public class Therapy extends AppCompatActivity implements Serializable {

    public static final String LOG_TAG = Therapy.class.getSimpleName();

    private TherapyMemoDataSource dataSource;

    private ListView mMedicationMemosListView; //Deklarieren des ListViews als Membervariable.


    @Override
    protected void onStart() {
        super.onStart();
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


    // Die onCreate() Methode wurde um den Testcode bereinigt.
    // Durch sie wird nun nur noch das Layout unserer App geladen, das Datenbank-Objekt angelegt
    // und der Add-Button aktiviert.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // Initialisierung der Activity
        setContentView(R.layout.therapy); // UI Layout aus der erstellen XML-Layout geladen

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        // Verbindung zu Data Acess Object der Datenbank
        dataSource = new TherapyMemoDataSource(this);

        initializeMedicationMemosListView();

        activateAddButton();
        initializeContextualActionBar();


        //Pass object dataSource to TakeMedication.class
        //Intent intent = new Intent (getApplicationContext(), TakeMedication.class);
        //intent.putExtra(, dataSource);
    }


    // Die neu angelegten Callback-Methoden onResume() und onPause(), durch die wir die Verbindung
    // zur SQLite Datenbank verwalten lassen.
    @Override
    protected void onResume()  {
        super.onResume();


        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open(); // Öffnung der Datenquelle mit der Hilfe des dataSource Objekt vom Typ
        //TherapyMemoDataSource

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");

        //!!!!!! Die folgende Linie gibt Fehler. Warum????
        showAllListEntries();// Schließung der Datenquelle mit der Hilfe des dataSource Objekt vom
        // Typ TherapyMemoDataSource




    }
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();

    }


    //  Definieren der initializeMedicationMemosListView() Methode,
    //  durch die unser ListView initialisiert wird.


    private void initializeMedicationMemosListView() {
        List<TherapyMemo> emptyListForInitialization = new ArrayList<>();

        mMedicationMemosListView = (ListView) findViewById(R.id.listview_medication_memos);

        // Erstellen des ArrayAdapters für unseren ListView
        ArrayAdapter<TherapyMemo> medicationMemoArrayAdapter = new ArrayAdapter<TherapyMemo> (
                this,
                android.R.layout.simple_list_item_multiple_choice,
                emptyListForInitialization) {

            // Wird immer dann aufgerufen, wenn der übergeordnete ListView die Zeile neu zeichnen muss
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view =  super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                TherapyMemo memo = (TherapyMemo) mMedicationMemosListView.getItemAtPosition(position);

                // Hier prüfen, ob Eintrag abgehakt ist. Falls ja, Text durchstreichen
                if (memo.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.rgb(175,175,175));
                }
                else {
                    textView.setPaintFlags( textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }

                return view;
            }
        };

        mMedicationMemosListView.setAdapter(medicationMemoArrayAdapter);

        mMedicationMemosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TherapyMemo memo = (TherapyMemo) adapterView.getItemAtPosition(position);

                // Hier den checked-Wert des Memo-Objekts umkehren, bspw. von true auf false
                // Dann ListView neu zeichnen mit showAllListEntries()
                TherapyMemo updatedTherapyMemo = dataSource.updateMedicationMemo(memo.getId(), memo.getMedication(), memo.getDosis(), memo.getPlanTime (), (!memo.isChecked()));
                Log.d(LOG_TAG, "Checked-Status von Eintrag: " + updatedTherapyMemo.toString() + " ist: " + updatedTherapyMemo.isChecked());
                showAllListEntries();
            }
        });

    }
    // Methode showAllListEntries () sodass erzeugte ArrayAdapter wiederverwendet wird.
    private void showAllListEntries () {
        List<TherapyMemo> therapyMemoList = dataSource.getAllMedicationMemos();

        ArrayAdapter<TherapyMemo> adapter = (ArrayAdapter<TherapyMemo>) mMedicationMemosListView.getAdapter();

        adapter.clear();
        adapter.addAll(therapyMemoList);
        adapter.notifyDataSetChanged();
        dataSource.getNextMedicationMemos();
    }

// Activate Add Button  -  Here the elements are added to the database.
// Essential for the adding of Entries on DataBase

//Die Methode activateAddButton(), durch die wir den OnClickListener für den Add-Button registrieren
//und die Inhalte der beiden Textfelder auslesen und in die Datenbank schreiben lassen.

    private void activateAddButton() {
        Button buttonAddMedication = (Button) findViewById(R.id.button_add_medication);
        final EditText editTextDosis = (EditText) findViewById(R.id.editText_dosis);
        final EditText editTextMedication = (EditText) findViewById(R.id.editText_medication);
        final EditText editTextPlanTime = (EditText) findViewById(R.id.editText_time);

        buttonAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dosis = editTextDosis.getText().toString();
                String medication = editTextMedication.getText().toString();
                String planTime = editTextPlanTime.getText().toString();

                if(TextUtils.isEmpty(dosis)) {
                    editTextDosis.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if(TextUtils.isEmpty(medication)) {
                    editTextMedication.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if(TextUtils.isEmpty(planTime)) {
                    editTextPlanTime.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (Zeit.checkTime(planTime)==false){
                    editTextPlanTime.setError("Geben sie ein gültige Uhrzeit, im Format HH:MM");
                    return;
                }

                editTextDosis.setText("");
                editTextMedication.setText("");
                editTextPlanTime.setText("");
                dataSource.createMedicationMemo(medication,dosis,planTime);

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                long nextAlarm = dataSource.nextAlarm;
                Log.d(LOG_TAG, "nextAlarm ---> After adding new medication = " + nextAlarm);
                activateAlarm();
                showAllListEntries();
            }
        });

    }

    // Die Methode initializeContextualActionBar(), mit der wir den MultiChoiceModeListener für den
    // ListView registrieren und die Contextual Action Bar initialisieren.

    private void initializeContextualActionBar() {
        final ListView medicationMemosListView = (ListView) findViewById(R.id.listview_medication_memos);
        medicationMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        medicationMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            int selCount = 0;

            // In dieser Callback-Methode zählen wir die ausgewählen Listeneinträge mit
            // und fordern ein Aktualisieren der Contextual Action Bar mit invalidate() an
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                String cabTitle = selCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            // In dieser Callback-Methode legen wir die CAB-Menüeinträge an
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar_therapy, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                return true;
            }

            // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
            // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedMedicationMemosPositions = medicationMemosListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedMedicationMemosPositions.size(); i++) {
                            boolean isChecked = touchedMedicationMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedMedicationMemosPositions.keyAt(i);
                                TherapyMemo therapyMemo = (TherapyMemo) medicationMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + therapyMemo.toString());
                                dataSource.deleteMedicationMemo(therapyMemo);
                            }
                        }
                        deactivateAlarm();
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        Log.d(LOG_TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedMedicationMemosPositions.size(); i++) {
                            boolean isChecked = touchedMedicationMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedMedicationMemosPositions.keyAt(i);
                                TherapyMemo therapyMemo = (TherapyMemo) medicationMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + therapyMemo.toString());

                                AlertDialog editMedicationMemoDialog = createEditMedicationMemoDialog(therapyMemo);
                                editMedicationMemoDialog.show();
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + therapyMemo.toString());

                            }
                        }
                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }

                return returnValue;
            }

            // In dieser Callback-Methode reagieren wir auf das Schließen der CAB
            // Wir setzen den Zähler auf 0 zurück
            @Override
            public void onDestroyActionMode(ActionMode mode) {

                selCount = 0;

            }


        });


    }
    // Die Methode createEditMedicationMemoDialog(), durch die wir einen AlertDialog erzeugen lassen.
    // Mit Hilfe dieses Dialogs können die Benutzer Änderungen an den Datenbankeinträge vornehmen.

    private AlertDialog createEditMedicationMemoDialog(final TherapyMemo therapyMemo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_therapy_memo, null);

        final EditText editTextNewDosis = (EditText) dialogsView.findViewById(R.id.editText_new_dosis);
        editTextNewDosis.setText(String.valueOf(therapyMemo.getDosis()));

        final EditText editTextNewMedication = (EditText) dialogsView.findViewById(R.id.editText_new_medication);
        editTextNewMedication.setText(therapyMemo.getMedication());

        final EditText editTextNewPlanTime = (EditText) dialogsView.findViewById(R.id.editText_new_plan_time);
        editTextNewPlanTime.setText(therapyMemo.getPlanTime());

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String dosis = editTextNewDosis.getText().toString();
                        String medication = editTextNewMedication.getText().toString();
                        String plan_time = editTextNewPlanTime.getText().toString();

                        if ((TextUtils.isEmpty(dosis)) || (TextUtils.isEmpty(medication))) {
                            Log.d(LOG_TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }


                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        //Anpassen der createEditMedicationMemoDialog() Methode an die Tabellenstruktur
                        TherapyMemo updatedTherapyMemo = dataSource.updateMedicationMemo(therapyMemo.getId(), medication, dosis, plan_time, therapyMemo.isChecked());

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + therapyMemo.getId() + " Inhalt: " + therapyMemo.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedTherapyMemo.getId() + " Inhalt: " + updatedTherapyMemo.toString());

                        deactivateAlarm();
                        activateAlarm();
                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }



    // Geht nicht in den Zyklus rein. Warum?
    public void activateAlarm() {
        //SET THE ALARM darf leider nicht in eine Klasse ohne onCreate ()

        if (dataSource.nextAlarm!=-1) {

            Calendar calendar = Calendar.getInstance();
            AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, BroadcastReceiverMedication.class);
            long longLastId = dataSource.lastId;
            int lastId = (int) longLastId;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), lastId , intent,  PendingIntent.FLAG_UPDATE_CURRENT);
            long nextAlarm= dataSource.nextAlarm;
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis() + nextAlarm), AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.d(LOG_TAG, "nextAlarm after entering Activate Alarm = " + calendar.getTimeInMillis() + nextAlarm);
        }
    }
    // Deaktiviert nicht mit der lastId
    public void deactivateAlarm() {
        Intent intent = new Intent(this, BroadcastReceiverMedication.class);
        long longLastId = dataSource.lastId;
        int lastId = (int) longLastId;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), lastId, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d(LOG_TAG, "Alarm wurde abgebrochen");
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


