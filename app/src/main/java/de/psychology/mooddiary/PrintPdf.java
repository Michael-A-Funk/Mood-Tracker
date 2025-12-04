package de.psychology.mooddiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintManager;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;


import java.util.Calendar;

import android.widget.EditText;
import android.widget.Spinner;

public class PrintPdf extends AppCompatActivity {


    EditText dateStart;
    DatePickerDialog dateStartPickerDialog;
    EditText dateFinish;
    DatePickerDialog dateFinishPickerDialog;
    CheckBox cBoxEmotions;
    CheckBox cBoxSleepDrugs;
    CheckBox cBoxPosOccurences;
    CheckBox cBoxMemo;
    CheckBox cBoxMedication;
    CheckBox cBoxDrinks;
    private Button buttonContinue;
    private long firstDay;
    private long lastDay;
    private long setYear;
    private boolean[]  chapters={false,false,false,false,false,false};

    // Dispose the month name in the diferent Languages
    public static String jan_String="Jan";
    public static String feb_String="Feb";
    public static String mar_String="Mar";
    public static String abr_String="Abr";
    public static String may_String="May";
    public static String jun_String="Jun";
    public static String jul_String="Jul";
    public static String aug_String="Aug";
    public static String sep_String="Sep";
    public static String oct_String="Oct";
    public static String nov_String="Nov";
    public static String dec_String="Dec";

    // Give back the String for a month from the number of the month
    public static String monthString(long date){
        int monthNumber=(int)((date%10000)/100);
        if (monthNumber==1) return(jan_String);
        if (monthNumber==2) return(feb_String);
        if (monthNumber==3) return(mar_String);
        if (monthNumber==4) return(abr_String);
        if (monthNumber==5) return(may_String);
        if (monthNumber==6) return(jun_String);
        if (monthNumber==7) return(jul_String);
        if (monthNumber==8) return(aug_String);
        if (monthNumber==9) return(sep_String);
        if (monthNumber==10) return(oct_String);
        if (monthNumber==11) return(nov_String);
        if (monthNumber==12) return(dec_String);
        return("");
    }

    // Dispose the mood factors in the diferent languages
    public static String noCompareString;
    public static String fearString;
    public static String moodString;
    public static String irritabilityString;
    public static String stressString;
    public static String sleepTimeString;
    public static String sleepQualityString;
    public static String sleepInterruptionString;
    public static String sleepInterruption1String;
    public static String sleepInterruption2String;
    public static String delusionString;
    public static String alcoholString;
    public static String drugsString;
    public static String dietString;
    public static String sportString;
    public static String mindfullnessString;
    public static String journalingString;
    public static String memoString;
    public static String monthString;
    public static String dayString;

    public static String emoString="Emotions";
    public static String sleepConsumString="Sleep/Consumption";
    public static String posString="Positive Occurrencies";
    public static String memString="Memory";
    public static String medString="Medication";
    public static String drinkString="Liquids";
    public static String comparingString="(Comparing)";


    //    public static String for the diferent scale labels
    public static String quality3[]={"","",""};
    public static String quality4[]={"","","",""};
    public static String quality5[]={"","","","",""};
    public static String quality7[]={"","","","","","",""};


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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_pdf);
        cBoxEmotions=findViewById(R.id.checkBoxEmotions);
        cBoxSleepDrugs=findViewById(R.id.checkBoxSleepDrugs);
        cBoxPosOccurences=findViewById(R.id.checkBoxPositiveOccurences);
        cBoxMemo=findViewById(R.id.checkMemo);
        cBoxMedication=findViewById(R.id.checkBoxMedication);
        cBoxDrinks=findViewById(R.id.checkBoxDrinks);


        // initiate the date picker START and a button
        dateStart = (EditText) findViewById(R.id.dateStart);
        // perform click event on edit text
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                dateStartPickerDialog = new DatePickerDialog(PrintPdf.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                dateStart.setText(dayOfMonth + "/"
                                        + (monthOfYear+1) + "/" + year);
                                firstDay=year*10000+100*(monthOfYear+1)+dayOfMonth;

                            }
                        }, mYear, mMonth, mDay);
                dateStartPickerDialog.show();
            }
        });


        //declare the spinner
        final Spinner spinnerEmotions = (Spinner)findViewById(R.id.spinnerMoodVariables);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        //create lists of items for the spinners.
        //ToDO noCompareString = getText(R.string.spinner_emotions_no_compare).toString();
        moodString= getText(R.string.spinner_mood).toString();
        fearString = getText(R.string.spinner_fear).toString();
        irritabilityString= getText(R.string.spinner_irritability).toString();
        stressString= getText(R.string.spinner_stress).toString();
        sleepTimeString=getText(R.string.editText_sleep_time).toString();
        sleepQualityString=getText(R.string.spinner_sleep_quality).toString();
        sleepInterruptionString=getText(R.string.checkBox_sleep_interruptions).toString();
        sleepInterruption1String=getText(R.string.sleep_interruptions1).toString();
        sleepInterruption2String=getText(R.string.sleep_interruptions2).toString();
        delusionString=getText(R.string.checkBox_delusions).toString();
        alcoholString=getText(R.string.checkBox_alcohol).toString();
        drugsString=getText(R.string.checkBox_drugs).toString();
        dietString=getText(R.string.spinner_diet).toString();
        sportString=getText(R.string.checkBox_sport).toString();
        mindfullnessString=getText(R.string.checkBox_mindfullness).toString();
        journalingString=getText(R.string.checkBox_journaling).toString();
        memoString=getText(R.string.checkBox_memo).toString();
        monthString=getText(R.string.month_label).toString();
        dayString=getText(R.string.day_label).toString();

        // Month in diferent languages
        jan_String=getText(R.string.month_jan).toString();;
        feb_String=getText(R.string.month_feb).toString();
        mar_String=getText(R.string.month_mar).toString();
        abr_String=getText(R.string.month_abr).toString();
        may_String=getText(R.string.month_may).toString();
        jun_String=getText(R.string.month_jun).toString();
        jul_String=getText(R.string.month_jul).toString();
        aug_String=getText(R.string.month_aug).toString();
        sep_String=getText(R.string.month_sep).toString();
        oct_String=getText(R.string.month_oct).toString();
        nov_String=getText(R.string.month_nov).toString();
        dec_String=getText(R.string.month_dec).toString();

        emoString=getText(R.string.emoLabel).toString();
        sleepConsumString=getText(R.string.sleepConsumLabel).toString();
        memString=getText(R.string.memLabel).toString();
        medString=getText(R.string.medLabel).toString();
        drinkString=getText(R.string.drinkLabel).toString();
        comparingString=getText(R.string.comparingLabel).toString();

        String selectEmotions= getText(R.string.spinner_emotions).toString();
        final String[] emotionsSpinnerString = new String[]{noCompareString,moodString,fearString,irritabilityString,stressString, selectEmotions};
        final int listEmotions = emotionsSpinnerString.length - 1;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterEmotions = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, emotionsSpinnerString){
            @Override
            public int getCount() {
                return(listEmotions); // Truncate the list
            }
        };
        adapterEmotions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        spinnerEmotions.setAdapter(adapterEmotions);
        spinnerEmotions.setSelection(listEmotions);


        //declare the spinner
        final Spinner spinnerTimeIntervall = (Spinner)findViewById(R.id.spinnerTimeIntervall);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        //create lists of items for the spinners.
        String selectTimeIntervall= getText(R.string.spinner_time_intervall).toString();
        final String[] timeIntervallString = new String[]{"1","2","3","4", selectTimeIntervall};
        final int listTimeIntervall = timeIntervallString.length - 1;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterTimeIntervall = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, timeIntervallString){
            @Override
            public int getCount() {
                return(listTimeIntervall); // Truncate the list
            }
        };
        adapterTimeIntervall.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        spinnerTimeIntervall.setAdapter(adapterTimeIntervall);
        spinnerTimeIntervall.setSelection(listTimeIntervall);


        // Defining Strings for the Graphic Axes which descbribe the values
        String badString = getText(R.string.graphic_bad).toString();
        String normalString = getText(R.string.graphic_normal).toString();
        String goodString = getText(R.string.graphic_good).toString();
        quality3[0]=badString;
        quality3[1]=normalString;
        quality3[2]=goodString;

        CharSequence noneChar = getText(R.string.graphic_none);
        String noneString = noneChar.toString();
        CharSequence littleChar = getText(R.string.graphic_little);
        String littleString = getText(R.string.graphic_bad).toString();
        CharSequence middleChar = getText(R.string.graphic_middle);
        String middleString = middleChar.toString();
        CharSequence veryChar = getText(R.string.graphic_very);
        String veryString = veryChar.toString();
        quality4[0]=noneString;
        quality4[1]=littleString;
        quality4[2]=middleString;
        quality4[3]=veryString;

        CharSequence moderateChar = getText(R.string.graphic_moderate);
        String moderateString = moderateChar.toString();
        CharSequence greatChar = getText(R.string.graphic_great);
        String greatString = greatChar.toString();
        quality5[0]=badString;
        quality5[1]=moderateString;
        quality5[2]=normalString;
        quality5[3]=goodString;
        quality5[4]=greatString;

        CharSequence worstChar = getText(R.string.graphic_worst);
        String worstString = worstChar.toString();
        CharSequence uncomfortableChar = getText(R.string.graphic_uncomfortable);
        String uncomfortableString = uncomfortableChar.toString();
        CharSequence wellChar = getText(R.string.graphic_well);
        String wellString = wellChar.toString();
        CharSequence topChar = getText(R.string.graphic_top);
        String topString = topChar.toString();
        quality7[0]=worstString;
        quality7[1]=badString;
        quality7[2]=uncomfortableString;
        quality7[3]=normalString;
        quality7[4]=wellString;
        quality7[5]=goodString;
        quality7[6]=topString;


        //Set the button to continue making a PDF
        buttonContinue = findViewById(R.id.button_continue);
        buttonContinue.setText(R.string.button_finished);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get String text for Spinner
                CharSequence selectCharEmotions = getText(R.string.spinner_emotions);
                String selectEmotions= selectCharEmotions.toString();
                CharSequence selectCharTimeIntervall = getText(R.string.spinner_time_intervall);
                String selectTimeIntervall= selectCharTimeIntervall.toString();



                if (spinnerEmotions.getSelectedItem().toString()!=selectEmotions && spinnerTimeIntervall.getSelectedItem().toString()!=selectTimeIntervall && firstDay!=0) {
                    PrintManager printManager =
                            getSystemService(PrintManager.class);
                    if (printManager != null) {
                        //get the value from the spinner
                        String moodSpinnerString = spinnerEmotions.getSelectedItem().toString();
                        int moodVariable=-1;
                        if (moodSpinnerString ==fearString) moodVariable=0;
                        if (moodSpinnerString ==irritabilityString) moodVariable=1;
                        if (moodSpinnerString ==moodString) moodVariable=2;
                        if (moodSpinnerString ==stressString) moodVariable=3;

                        String stringTimeInterval = spinnerTimeIntervall.getSelectedItem().toString();
                        int valueTimeInterval = Integer.parseInt(stringTimeInterval);
                        String jobName = getString(R.string.app_name) + " Document";
                        chapters[0]=cBoxEmotions.isChecked();
                        chapters[1]=cBoxSleepDrugs.isChecked();
                        chapters[2]=cBoxPosOccurences.isChecked();
                        chapters[3]=cBoxMemo.isChecked();
                        chapters[4]=cBoxMedication.isChecked();
                        chapters[5]=cBoxDrinks.isChecked();

                        //get the lastDay as differential from firstDay and the given timeIntervall
                        lastDay = firstDay;
                        for (int i = 0; i < valueTimeInterval * 7; i++)
                            lastDay = Zeit.nextDay(lastDay);
                        // Pass to the print Job
                        printManager.print(jobName,
                                new DemoPrintDocumentAdapter(PrintPdf.this,firstDay,valueTimeInterval,moodVariable,chapters), null);

                    }
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

    /* -----------------------------------------------------------------------
             Funktion: intCriteria
             Get the index in the criteria String table abouve from by its String value
     */
    public static String attributeStrings(int value) {
        if (value==0) return(fearString);
        if (value==1) return(irritabilityString);
        if (value==2) return(moodString);
        if (value==3) return(stressString);
        if (value==4) return(sleepTimeString);
        if (value==5) return(sleepQualityString);
        if (value==6) return(sleepInterruptionString);
        if (value==7) return(delusionString);
        if (value==8) return(alcoholString);
        if (value==9) return(drugsString);
        if (value==10) return(drinkString);
        return ("");
    }

    public boolean compareBoolean(int value) {
        if (value == 0 || value == 1 || value == 3) {
            return (false);
        } else {
            return (true);
        }
    }

    public static String title (int chapter, boolean compare) {
        String title="";
        //Get Strings for passing to titleGraph function
        if (chapter == 0) title = emoString;
        if (chapter == 1) title = sleepConsumString;
        if (chapter == 2) title = posString;
        if (chapter == 3) title = memString;
        if (chapter == 4) title = medString;
        if (chapter == 5) title = drinkString;
        if (compare) title=title+comparingString;
        return title;
    }
}

