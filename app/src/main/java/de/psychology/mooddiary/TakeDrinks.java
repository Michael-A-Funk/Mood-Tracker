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


public class TakeDrinks extends AppCompatActivity{

    public static final String LOG_TAG = TakeDrinks.class.getSimpleName();

    private DrinksMemoDataSource dataSource;

    private ListView mDrinksMemosListView; //Deklarieren des ListViews als Membervariable.

    // Die onCreate() Methode wurde um den Testcode bereinigt.
    // Durch sie wird nun nur noch das Layout unserer App geladen, das Datenbank-Objekt angelegt
    // und der Add-Button aktiviert.

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
        setContentView(R.layout.take_drinks); // UI Layout aus der erstellen XML-Layout geladen
        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        // Verbindung zu Data Acess Object der Datenbank
        dataSource = new DrinksMemoDataSource(this);

        initializeDrinksMemosListView();
        activateAddButton();
        initializeContextualActionBar();
    }


    // Die neu angelegten Callback-Methoden onResume() und onPause(), durch die wir die Verbindung
    // zur SQLite Datenbank verwalten lassen.
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open(); // Öffnung der Datenquelle mit der Hilfe des dataSource Objekt vom Typ
        //DrinksMemoDataSource

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();// Schließung der Datenquelle mit der Hilfe des dataSource Objekt vom
        // Typ DrinksMemoDataSource
    }
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }


    //  Definieren der initializeDrinksMemosListView() Methode,
    //  durch die unser ListView initialisiert wird.


    private void initializeDrinksMemosListView() {
        List<DrinksMemo> emptyListForInitialization = new ArrayList<>();

        mDrinksMemosListView = (ListView) findViewById(R.id.listview_drinks_memos);

        // Erstellen des ArrayAdapters für unseren ListView
        ArrayAdapter<DrinksMemo> drinksMemoArrayAdapter = new ArrayAdapter<DrinksMemo> (
                this,
                android.R.layout.simple_list_item_multiple_choice,
                emptyListForInitialization) {

            // Wird immer dann aufgerufen, wenn der übergeordnete ListView die Zeile neu zeichnen muss
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view =  super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                DrinksMemo memo = (DrinksMemo) mDrinksMemosListView.getItemAtPosition(position);

                // Hier prüfen, ob Eintrag abgehakt ist. Falls ja, Text durchstreichen
                if (memo.isChecked()) {
                   textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.rgb(175,175,175));
                    Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
                    showAllListEntries();// Schließung der Datenquelle mit der Hilfe des dataSource Objekt vom
                    // Typ DrinksMemoDataSource
                }
                else {
                    textView.setPaintFlags( textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }

                return view;
            }
        };

        mDrinksMemosListView.setAdapter(drinksMemoArrayAdapter);

        mDrinksMemosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DrinksMemo memo = (DrinksMemo) adapterView.getItemAtPosition(position);

                // Hier den checked-Wert des Memo-Objekts umkehren, bspw. von true auf false
                // Dann ListView neu zeichnen mit showAllListEntries()
                DrinksMemo updatedDrinksMemo = dataSource.updateDrinksMemo(memo.getId(), memo.getDrinks(), memo.getQuantity(), memo.getTime(), memo.getDate (), (!memo.isChecked()));
                Log.d(LOG_TAG, "Checked-Status von Eintrag: " + updatedDrinksMemo.toString() + " ist: " + updatedDrinksMemo.isChecked());
                showAllListEntries();
            }
        });

    }
    // Methode showAllListEntries () sodass erzeugte ArrayAdapter wiederverwendet wird.
    private void showAllListEntries () {
        List<DrinksMemo> drinksMemoList = dataSource.getIntervalDrinksMemos(Zeit.today(),Zeit.today());

        ArrayAdapter<DrinksMemo> adapter = (ArrayAdapter<DrinksMemo>) mDrinksMemosListView.getAdapter();

        adapter.clear();
        adapter.addAll(drinksMemoList);
        adapter.notifyDataSetChanged();
    }

// Activate Add Button  -  Here the elements are added to the database.
// Essential for the adding of Entries on DataBase

//Die Methode activateAddButton(), durch die wir den OnClickListener für den Add-Button registrieren
//und die Inhalte der beiden Textfelder auslesen und in die Datenbank schreiben lassen.

    private void activateAddButton() {
        Button buttonAddDrinks = (Button) findViewById(R.id.button_add_drinks);
        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editTextDrinks = (EditText) findViewById(R.id.editText_drinks);

        buttonAddDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantity = editTextQuantity.getText().toString();
                String drinks = editTextDrinks.getText().toString();

                if(TextUtils.isEmpty(quantity)) {
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if(TextUtils.isEmpty(drinks)) {
                    editTextDrinks.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                editTextQuantity.setText("");
                editTextDrinks.setText("");
                dataSource.createDrinksMemo(drinks,quantity);

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                showAllListEntries();
            }
        });

    }

    // Die Methode initializeContextualActionBar(), mit der wir den MultiChoiceModeListener für den
    // ListView registrieren und die Contextual Action Bar initialisieren.

    private void initializeContextualActionBar() {
        final ListView drinksMemosListView = (ListView) findViewById(R.id.listview_drinks_memos);
        drinksMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        drinksMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar_drinks, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change_drinks);
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
                SparseBooleanArray touchedDrinksMemosPositions = drinksMemosListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete_drinks:
                        for (int i = 0; i < touchedDrinksMemosPositions.size(); i++) {
                            boolean isChecked = touchedDrinksMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedDrinksMemosPositions.keyAt(i);
                                DrinksMemo drinksMemo = (DrinksMemo) drinksMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + drinksMemo.toString());
                                dataSource.deleteDrinksMemo(drinksMemo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change_drinks:
                        Log.d(LOG_TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedDrinksMemosPositions.size(); i++) {
                            boolean isChecked = touchedDrinksMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedDrinksMemosPositions.keyAt(i);
                                DrinksMemo drinksMemo = (DrinksMemo) drinksMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + drinksMemo.toString());

                                AlertDialog editDrinksMemoDialog = createEditDrinksMemoDialog(drinksMemo);
                                editDrinksMemoDialog.show();
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

    // Die Methode createEditDrinksMemoDialog(), durch die wir einen AlertDialog erzeugen lassen.
    // Mit Hilfe dieses Dialogs können die Benutzer Änderungen an den Datenbankeinträge vornehmen.

    private AlertDialog createEditDrinksMemoDialog(final DrinksMemo drinksMemo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_drinks_memo, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(drinksMemo.getQuantity()));

        final EditText editTextNewDrinks = (EditText) dialogsView.findViewById(R.id.editText_new_drinks);
        editTextNewDrinks.setText(drinksMemo.getDrinks());

        final EditText editTextNewTime = dialogsView.findViewById(R.id.editText_new_time);
        editTextNewTime.setText(drinksMemo.getTime());

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantity = editTextNewQuantity.getText().toString();
                        String drinks = editTextNewDrinks.getText().toString();
                        String time = editTextNewTime.getText().toString();


                        if ((TextUtils.isEmpty(quantity)) || (TextUtils.isEmpty(drinks))) {
                            Log.d(LOG_TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }


                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        //Anpassen der createEditDrinksMemoDialog() Methode an die Tabellenstruktur
                        DrinksMemo updatedDrinksMemo = dataSource.updateDrinksMemo(drinksMemo.getId(), drinks, quantity, time, drinksMemo.getDate(), drinksMemo.isChecked());

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + drinksMemo.getId() + " Inhalt: " + drinksMemo.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedDrinksMemo.getId() + " Inhalt: " + updatedDrinksMemo.toString());

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
// ???????????????????????????????????????????????????????????????????????????????????????????????????
// Wir sollten die destroy oder finish Metode nutzen, um die not checked elemente der DB zu löschen!!!
// Ausserdem sollten wir nur die Datensätze der ausgewählten Tages angeben!
// ???????????????????????????????????????????????????????????????????????????????????????????????????