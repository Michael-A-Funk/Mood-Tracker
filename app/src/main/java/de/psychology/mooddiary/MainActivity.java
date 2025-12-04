package de.psychology.mooddiary;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;

import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MoodMemoDataSource dataSourceMood;

    private TherapyMemoDataSource dataSource;

    private TakeMedicationMemoDataSource dataSourceTakeMedication;

    private DrinksMemoDataSource  dataSourceDrink;

    private ListView mMedicationMemosListView; //Deklarieren des ListViews als Membervariabl

    private boolean [] deletedMedication ;

    private List<TherapyMemo> therapyMemoList;


    @Override
    protected void onStart() {
        super.onStart();
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // Initialisierung der Activity
        setContentView(R.layout.activity_main); // UI Layout aus der erstellen XML-Layout geladen

        //Activate Notification to register Mood
        createNotificationChannel();
        activateAlarm();

        dataSourceMood = new MoodMemoDataSource(this);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        // Verbindung zu Data Acess Object der Datenbank
        dataSource = new TherapyMemoDataSource(this);
        dataSourceTakeMedication = new TakeMedicationMemoDataSource(this);


        initializeMedicationMemosListView();
        activateMedicationTakenButton();
        initializeContextualActionBar();



    }
   // Die neu angelegten Callback-Methoden onResume() und onPause(), durch die wir die Verbindung
   // zur SQLite Datenbank verwalten lassen.
    @Override
    protected void onResume() {
        super.onResume();

       // Teil der Mood anzeige im Graphic
        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSourceMood.open();// Öffnung der Datenquelle mit der Hilfe des dataSource Objekt vom Typ
        //TherapyMemoDataSource

        //Graph
        //if (dataSourceMood.moodFirstRegistered==true) {
            GraphView graph = (GraphView) findViewById(R.id.graph);
            List<MoodMemo> MoodMemoList = dataSourceMood.getIntervalMoodMemos(Zeit.today(),Zeit.today());
            Log.d(LOG_TAG, "Wurde schon ein Wert in Mood eingegeben? >>>>>> " + dataSourceMood.moodFirstRegistered);
            DataPoint[] dataPoints = new DataPoint[MoodMemoList.size()];
            for (int i = 0; i < MoodMemoList.size(); i++) {
                //new DataPoint(MoodMemoList.get(i).getDate(), MoodMemoList.get(i).getMood());
                dataPoints[i] = new DataPoint (i, MoodMemoList.get(i).getMood());
                Log.d(LOG_TAG, "Daten wurden an Graphic abgegeben " + i);
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
            graph.addSeries(series);
        //}
        Log.d(LOG_TAG, "Wurde schon ein Wert in Mood eingegeben? " + dataSourceMood.moodFirstRegistered);
        //End Graph


        //Teil der Take Medication
        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();// Öffnung der Datenquelle mit der Hilfe des dataSource Objekt vom Typ
        //TherapyMemoDataSource
        dataSourceTakeMedication.open();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");

        showAllListEntries();// Schließung der Datenquelle mit der Hilfe des dataSource Objekt vom
        // Typ TherapyMemoDataSource

    }
    @Override
    protected void onPause() {
        super.onPause();
        //Teil der Mood Graphic Anzeige
        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSourceMood.close();

        //Teil der Take Medication

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
        dataSourceTakeMedication.close();
    }




    public void activateAlarm() {
        //SET THE ALARM darf leider nicht in eine Klasse ohne onCreate ()

        final String KEY_PREFS_FIRST_LAUNCH = "first_launch";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean(KEY_PREFS_FIRST_LAUNCH, true)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 9);

            AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, BroadcastReceiverMood.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis()), AlarmManager.INTERVAL_DAY, pendingIntent);

            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 20);

            alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            intent = new Intent(this, BroadcastReceiverMood.class);
            pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 400, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis()), AlarmManager.INTERVAL_DAY, pendingIntent);

            prefs.edit().putBoolean(KEY_PREFS_FIRST_LAUNCH,false).commit();
        }
    }


    private void createNotificationChannel(){

        CharSequence name = "MoodDiary";
        String description = "Channel for Mood Reminder";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("notifyMood", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    // TEIL für Take Medication
    private void initializeMedicationMemosListView() {
        List<TherapyMemo> emptyListForInitialization = new ArrayList<>();

        mMedicationMemosListView = (ListView) findViewById(R.id.listview_take_medication_memos);

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
// N E X T !!!

        if (therapyMemoList==null) {
            List<TherapyMemo> nextTherapyMemoList = getNotTakenMedicationToday(); // Get all Medications of the therapy not taken today
            therapyMemoList = getNotDeletedMedicationToday(nextTherapyMemoList); // Get all Medication not deleted for today by user
        }
        else if (therapyMemoList!=null){therapyMemoList = getNotDeletedMedicationToday(therapyMemoList);}

        ArrayAdapter<TherapyMemo> adapter = (ArrayAdapter<TherapyMemo>) mMedicationMemosListView.getAdapter();


        adapter.clear();
        adapter.addAll(therapyMemoList);
        adapter.notifyDataSetChanged();
    }

    // PROBLEM : Wenn ich drauf drück und die Liste ist schon Lehr gibt er mit Fehler beim showAllListEntries
    private void activateMedicationTakenButton() {
        Button buttonTakenMedication = findViewById(R.id.button_medication_taken);

        buttonTakenMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final long lastId = therapyMemoList.size();
                if (lastId>0) {
                    Log.d(LOG_TAG, "Index is: " + lastId);

                    for (int i = 0; i <= lastId - 1; i++) {
                        final TakeMedicationMemo takeMedicationMemo = dataSourceTakeMedication.createTakeMedicationMemo(therapyMemoList.get(i).getId());
                        Log.d(LOG_TAG, "Folgende Werte wurden in der Mood Datenbank angelegt." + takeMedicationMemo.toString());
                    }

                    InputMethodManager inputMethodManager;
                    inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null) {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    showAllListEntries();
                    Intent formular = new Intent(getApplicationContext(), MainActivity.class); // define the Intent formular
                    startActivity(formular);                        // To coll a new activity with this Intent

                }
            }
        });


    }

    // Diese Methode ermöglicht dass man ein Medikament löscht wenn man in
    private void initializeContextualActionBar() {
        final ListView medicationMemosListView = (ListView) findViewById(R.id.listview_take_medication_memos);
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
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar_take_medication, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return true;
            }

            // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
            // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedMedicationMemosPositions = medicationMemosListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete_medication:
                        if (therapyMemoList.size()==0){therapyMemoList = getNotTakenMedicationToday();}    // Get all Medications of the therapy
                        deletedMedication = new boolean [therapyMemoList.size()];
                        for (int i=0; i<deletedMedication.length;i++){deletedMedication[i]=false;}

                        for (int i = 0; i < touchedMedicationMemosPositions.size(); i++) {
                            boolean isChecked = touchedMedicationMemosPositions.valueAt(i);
                            if (isChecked) {
                                int positionInListView = touchedMedicationMemosPositions.keyAt(i);
                                Log.d(LOG_TAG, "key " + positionInListView + "element is " + therapyMemoList.get(positionInListView).getMedication());
                                deletedMedication[positionInListView] = true;
                                Log.d(LOG_TAG, "value of deletedMedication is " + deletedMedication[positionInListView]);
                            }
                        }
                        showAllListEntries();
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

    public List<TherapyMemo> getNotTakenMedicationToday() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
        long date = Long.parseLong(sdfDate.format(new Date().getTime ()));          // Datepattern for today

        List<TherapyMemo> therapyMemoList = dataSource.getNextMedicationMemos();    // Get all Medications of the therapy
        List<TakeMedicationMemo> medicationMemoList = dataSourceTakeMedication.getAllTakeMedicationMemos(date); // Get all Medications alway taken today
        // Hier gibt es Probleme
        Log.d(LOG_TAG, "Size of medicationMemoList: " + medicationMemoList.size());
        // Reduce the medication taken today in the medication list of the therapy
        if (medicationMemoList.size()>0 && therapyMemoList.size()>0){
            for (int i = 0; i < therapyMemoList.size(); i++) {
                for (int j = 0; j < medicationMemoList.size(); j++) {
                    if (therapyMemoList.get(i).getId() == medicationMemoList.get(j).getIdMedication() ) {
                        therapyMemoList.remove(i);

                    }
                }
            }
        }
        return therapyMemoList;
    }

    public List<TherapyMemo> getNotDeletedMedicationToday(List <TherapyMemo> therapyMemoList){
        //verifizieren ob es gelöschte Medikamente gibt, und diese löschen
        for (int i = therapyMemoList.size()-1; i >= 0 ; i--) {
            if (deletedMedication!=null){
                if (deletedMedication[i]==true){
                    therapyMemoList.remove(i);
                }
            }
        }

        return therapyMemoList;
    }

    // Teil für Menü Leiste
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