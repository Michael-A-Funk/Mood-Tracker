package de.psychology.mooddiary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
                                  INTERFACE of the CLASS: DemoPrintDocumentAdapter
  The CLASS "DemoPrintDocumentAdapter" define how we extract the Date in the databses of "Mood", "Medications" and "Drinks"
  acording the wishes of the user (collected in the Activity "PrintPdf") into a PDF-File
*/
class DemoPrintDocumentAdapter extends PrintDocumentAdapter {
    /*    Therefore we use the following static Attributes static:
        - quality3a   --> the integer values for the integer messure on the y-scale of the criterias "Diet"
        - quality4a   --> the numerical values for the integer messure on the y-scale of the criterias "Fear", "Irritability" and "Stress"
        - quality7a   --> the numerical values for the integer messure on the y-scale of the criteria "Mood"
        - qualityL    --> the numerical liter values for the integer messure on the y-scale of the criterias "Drink"
        - topicColors --> define the colors we use to represeent the diferent criterias
    */


    public static String quality3a[]={"-1","0","1"};
    public static String quality4a[]={"0","1","2","3"};
    public static String quality5a[]={"-2","-1","0","1","2"};
    public static String quality7a[]={"-3","-2","-1","0","1","2","3"};
//    public static String qualityL[]={"250 ml", "500 ml", "750 ml", "1 l", "1.25 l", "1.5 l", "1.75 l", "2 l"};
    public static String[] drinkScale={};

    // Color for:    Fear     / Irritability/  Mood   / Stress    /    Diet    / Sleep Hour / Sleep Quality / Boolean values / BLACK
    public static int[][] topicColors={{50,50,200},{200,50,50},{50,200,50},{50,150,150},{150,150,50},{250,150,150},{250,0,200},{0,100,150},{0,0,0} };


    /* -----------------------------------------------------------------------
    /*   the following Attributes:
                    - firstDay      --> The first Day we report in the pdf Extraction
                    - lastDay       --> The last Day we report in the pdf Extraction
                    - leadTheme     --> The criteria which is choosen as comparition line in all graphics
                    - Context       --> mantain the context given by the Class "PrintPdf"
                    - pdf           --> contain the pdf document in the form of the Class "PrintedPdfDocument"
                    - numPages      --> give the number of pages we will have in the PDF-File
                    - chapters      --> indicates which chapter we report out of
                                       [Emotions, Sleep & Drugs, Positive occurrencies, Diary, Medications, Drinks]
                    - listMood      --> Contain the data extracted out of the "Mood"-Database according to the date collected in "PrintPdf"-Activity
                    - listPositive  --> Contain the data extracted out of the "Positive"-Database according to the date collected in "PrintPdf"-Activity
    */
    private long firstDay;
    private int days;
    private int leadTheme;              // -1=NONE / 0=intCriteria("Fear") / 1=intCriteria("Irratibility") / 2=intCriteria("Mood") / 3=intCriteria("Stress")
    private Context context;
    private PrintedPdfDocument pdf;
    private int numPages;
    private boolean[] chapters={false,false,false,false,false,false};
    private List<MoodMemo> listMood;
    private List<PositiveMemo> listPositive;
    private List<DrinksMemo> listDrinks;
    private List<TakeMedicationMemo> listMedication;
    private List<TherapyMemo> listTherapie;
    private DrawingGrafics drawG;



/*    and the following methods:
    - DemoPrintDocumentAdapter  --> Creat Instance of the class
    - disposePdf                --> Closing the Pdf-File
    - onFinish                  --> Define the procedure to do at the end of the call
    - onLayout                  --> Define the layout of each page
    - onWrite                   --> Define the content of each page
*/
/*                     E N D of the I N T E R F A C E of the C L A S S: DemoPrintDocumentAdapter
 CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
*/


/* CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
                            M E T H O D S of the C L A S S: DemoPrintDocumentAdapter
*/
     /* ==========================================================================================================================
        Method: "DemoPrintDocumentAdapter"  --> Creator of the Class
            - Create the instance of the Class "DemoPrintDocumentAdapter" acording to the wishes of the user (collected in the
              Activity "PrintPdf") into a PDF-File receiving the following arguments:
                * first  --> Data in the type long with 8 digits of the format pattern "yyyyMMdd" for the first day of the report
                * last   --> Data in the type long with 8 digits of the format pattern "yyyyMMdd" for the last day of the report
                * lead   --> A String
               defining as sideback the following intance-attributs:
                * listMood --> List of all "Mood"-Informations inside their database in the time intervall [first, Last]
    */
    DemoPrintDocumentAdapter(Context context,long first,int weeks,int lead,boolean[] chap)  {
        this.context=context;
        firstDay=first;
        days=7*weeks;
        leadTheme=lead;
        MoodMemoDataSource dataSourceMood = new MoodMemoDataSource(this.context);                  // Auxiliary Attribute to access the database "Mood"
        dataSourceMood.open();                                                                     // Open that database
        listMood=dataSourceMood.getSortIntervalMoodMemos(first,Zeit.later(first,7*weeks));    // Extract the Information inside the given timeline
        dataSourceMood.close();                                                                    // Close the Database

        // GET the data from the Positive Source
        PositiveMemoDataSource dataSourcePositive = new PositiveMemoDataSource(this.context);      // Auxiliary Attribute to access the database "Positive"
        dataSourcePositive.open();                                                                 // Open that database
        listPositive=dataSourcePositive.getSortIntervalPositiveMemos(first,Zeit.later(first,7*weeks));// Extract the Information inside the given timeline
        dataSourcePositive.close();                                                                // Close the Database

        // GET the data from the Drinks Source
        DrinksMemoDataSource dataSourceDrink = new DrinksMemoDataSource(this.context);             // Auxiliary Attribute to access the database "Drinks"
        dataSourceDrink.open();                                                                    // Open that database
        listDrinks=dataSourceDrink.getIntervalDrinksMemos(first,Zeit.later(first,7*weeks));  // Extract the Information inside the given timeline
        dataSourceDrink.close();                                                                   // Close the Database

        // GET the data from the Medication Source
        TakeMedicationMemoDataSource dataSourceMedication = new TakeMedicationMemoDataSource(this.context); // Auxiliary Attribute to access the database "Drinks"
        dataSourceMedication.open();                                                               // Open that database
        listMedication=dataSourceMedication.getSortIntervalMedicationMemos(first,Zeit.later(first,7*weeks));    // Extract the Information inside the given timeline
        dataSourceMedication.close();                                                              // Close the Database

        // GET the data from the Therapy Source
        TherapyMemoDataSource dataSourceTherapy = new TherapyMemoDataSource(this.context);         // Auxiliary Attribute to access the database "Drinks"
        dataSourceTherapy.open();                                                                  // Open that database
        listTherapie=dataSourceTherapy.getAllMedicationMemos();                                    // Extract the Information inside the given timeline
        dataSourceTherapy.close();                                                                 // Close the Database

        chapters=chap;
    }
    /*                                   E N D: "DemoPrintDocumentAdapter"  (Creator of the Class)
       ==========================================================================================================================
    */

    /* ==========================================================================================================================
        Method: disposePdf  --> Closing the pdf-File
    */
    private void disposePdf() {
        if (pdf != null) {
            pdf.close();
            pdf = null;
        }
    }
    /*                                   E N D: "disposePdf"
       ==========================================================================================================================
    */

    /* ==========================================================================================================================
        Method: onFinish  --> Preparing the finish of the writing actions
    */
    @Override
    public void onFinish() {
        disposePdf();                                               //closing the page
    }
    /*                                   E N D: "onFinish"
       ==========================================================================================================================
    */


    /* ==========================================================================================================================
        Method: "onLayout"  --> Creator of the Class
            - Defining the layout of the Pdf- file acording to the wishes of the user (collected in the Activity "PrintPdf")
              using the following inputs:
                * oldAttributes of the Type PrintAttributes
                * newAttributes of the Type PrintAttributes
                * cancellationSignal of the Type CancellationSignal
                * callback of the Type LayoutResultCallback
                * extras of the Type Bundle
               defining as sideeffect the following intance-attributs:
                * pdf --> The pdf document in the form of the Class "PrintedPdfDocument"
            - Recurring on the auxiliary method:
                  ----------------
                * computePageCount
                  ----------------
    */
    @Override // Has to be implemented
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {
        disposePdf();                                                           // if still exist release old Pdf-Document
        pdf = new PrintedPdfDocument(this.context, newAttributes);              // Open/create a new PDF-Document with the desired attributea
        if (cancellationSignal.isCanceled()) {                                  // React on a break-demand
            callback.onLayoutCancelled();                                               // Chancel the layout
            disposePdf();                                                               // Release the Pdf-Document
            return;
        }
        boolean[] chapters={true,true,false,false,false,false};
        numPages = computePageCount(newAttributes);                             // Calculate the expected number of pages
        if (numPages > 0) {                                                     // Pass back information to the Print-Framework
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder("moodies_grafic.pdf")                        // Indicate the name of the PDF-file
                    .setContentType(
                            PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(numPages)                                     // Indicate the number of pages
                    .build();
            callback.onLayoutFinished(info, true);
        } else {                                                                 // Or alternatively give error message
            callback.onLayoutFailed(
                    "Error during the counting of the page number");
        }
    }
    /*                                   E N D: "onLayout"
       ==========================================================================================================================
    */

    /* --------------------------------------------------------------------------------------------------------------------------
    Method: computePageCount --> Comuting the necessary number of pages
            - Calculating the number of pages necessary to fullfill the wishes of the user (collected in the Activity "PrintPdf")
              using the following inputs:
              * printAttributes of the Type PrintAttributes
            - returning:
              * number of pages:
    */

    private int computePageCount(PrintAttributes printAttributes) {  // passing information of the page size
        PrintAttributes.MediaSize size = printAttributes.getMediaSize();
        int pages=0;
        if (chapters[0]) pages+=2; // Mood(comparison)
        if (chapters[1]) pages+=1; // Sleep & Drugs
        if (chapters[2]) pages+=1; // Positive ocdurrencies
        if (chapters[3]) pages+=1; // Diary
        if (chapters[4]) pages+=1; // Medications
        if (chapters[5]) pages+=1; // Drinks
        return(pages);

    }
    /*                                   E N D: "computingPage"
       ==========================================================================================================================
    */

    /* ==========================================================================================================================
        Method: "onWrite"  --> Write on the Pdf-Document
            - Defining the writing of the Pdf- file acording to the wishes of the user (collected in the Activity "PrintPdf")
              using the following inputs:
                * PageRange[]        --> List of pages used
                * destination        --> ???
                * cancellationSignal --> ???
                * callback           --> ???
            - Writing as sideeffect the following intance-attributs:
                * pdf --> The pdf document in the form of the Class "PrintedPdfDocument"
            - Without any output data
            - Recurring on the auxiliary method:
                -----------
                * drawPage
                -----------
     */
    @Override // Need to be implemented
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        // Iteration over all pages of the document
        for (int i = 0; i < numPages; i++) {
            // If there is transmitted the signal for chancel the action
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();                                //Chancel the action
                disposePdf();                                               // Close the PDF-page
                return;
            }
            PdfDocument.Page page = pdf.startPage(i);                       // Open a new page for the i-th page of the document
            drawPage(page,i);                                               // Draw the page
            pdf.finishPage(page);                                           // Finish this page
        }
        // Try to write on the pfd document
        try {
            pdf.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        }
        try {
            destination.close();
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        }
        callback.onWriteFinished(pages);
    }
    /*                                   E N D: "onWrite"
       ==========================================================================================================================
    */



    /* --------------------------------------------------------------------------------------------------------------------------
    Method: "drawPage"  --> Write one page of the Pdf-Document, in especific the number i
        - Defining the writing of the Pdf- file acording to the wishes of the user (collected in the Activity "PrintPdf")
          using the following inputs:
            * page          --> pages used
            * nr            --> number of the actual page
        - Writing as sideeffect the following intance-attributs:
            * page --> The page of the pdf-document in the form of the Class "PrintedPdfDocument"
        - Without any output data
        - Recurring on the auxiliary method:
            -----------
            * drawMood(), drawSleep(), drawPositive(), drawDiary(), drawMedications(), drawDrinks()
            -----------
    */
    private void drawPage(PdfDocument.Page page,int nr) {
        // Set the dimension of the site with units equal to 1/72 inch
        Canvas canvas = page.getCanvas();                       // Define a new canvas to use
        drawG=new DrawingGrafics(canvas,firstDay,days,leadTheme,listMood,listPositive,listDrinks,listMedication);

        int w = 15 * canvas.getWidth() / 16;                    // Set the width of the frame
        Paint paint = new Paint();                              // Define a new paint toolbox

        // Write the common title of the pages
        paint.setColor(-700890190);                             // Set the color for the topic text
        paint.setTextSize(36);                                  // Set the size for the topic text
        canvas.drawText("MOOD", w - 160, 50, paint);   // Write the first half of the topic text
        paint.setColor(Color.BLUE);                             // Set the color for the topic text
        canvas.drawText("Diary", w - 50, 50, paint);   // Write the second half of the topic text
        // Choice of the pages for the diferent chapters
        int n=nr;

        if (chapters[0])
            if (n<2) {drawMood(canvas, nr);return;} else n-=2;
        if (chapters[1])
           if (n<1) {drawSleep(canvas, nr);return;} else n-=1;
        if (chapters[2])
            if (n<1) {drawPositive(canvas);return;} else n-=1;
        if (chapters[3])
            if (n<1) {drawDiary(canvas);return;} else n-=1;
        if (chapters[4])
            if (n<1) {drawMedications(canvas);return;} else n-=1;
        if (chapters[5]){drawDrinking(canvas);}
        return;
    }
        /*                                   E N D: "drawPage"
       ==========================================================================================================================
    */


    /* --------------------------------------------------------------------------------------------------------------------------
    */
    private void drawMood(Canvas canvas, int nr){
        // Define the two pages for the graphics of the criterias {"Fear","Irratibility","Mood","Stress"}
        // Sub-title of the Moodies
        Paint paint = new Paint();
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);
        if (leadTheme<0) canvas.drawText(PrintPdf.title(0,false), 50, 100, paint);
        else canvas.drawText(PrintPdf.title(0,true), 50, 100, paint);

        // Set the x- A X I S (with the common interval of time) and other base lines for the upper and lower grafic on this page
        int[] labelColor={0,0,255};
        drawG.drawAxisX(true,false, labelColor,Color.argb(100,0,0,0),16,2,1);
        drawG.drawAxisX(false, false,labelColor,Color.argb(100,0,0,0),16,2,1);

        // Paint the upper differential curve and the left Y-axis
        int topic=(2*nr)%4;if (leadTheme>-1&&leadTheme<4)topic=(leadTheme+2*nr)%4;// Choice the first topic to handle on this page
        String[] scale=PrintPdf.quality4;if (topic==2) scale=PrintPdf.quality7;                     // Define the scale for the upper left hand y-axis
                // Draw the upper right y-axis of this page
        drawG.drawAxisY(true,true, false,PrintPdf.attributeStrings(topic), scale,topicColors[topic], Color.LTGRAY,2,1);
                // Draw the upper differential curve of this page
        drawG.drawChart(true, false,false,PrintPdf.attributeStrings(topic),topicColors[topic]);

        // Paint the lower differential curve and the left Y-axis
        topic=(topic+1)%4;                                                       // Choice the second topic to handle on this page
        if (topic==2) scale=PrintPdf.quality7;else scale=PrintPdf.quality4;                        // Define the scale for the upper left hand y-axis
                // Draw the lower right y-axis of this page
        drawG.drawAxisY(false,true, false,PrintPdf.attributeStrings(topic), scale,topicColors[topic], Color.LTGRAY,2,1);
                // Paint the lower differential curve of this page
        drawG.drawChart(false, false, false,PrintPdf.attributeStrings(topic),topicColors[topic]);

        // Paint the common Y-axe and leading graph respect to the leading Theme at the actual page
        if (leadTheme>-1&&leadTheme<4){
            paint.setColor(Color.LTGRAY);
            if (leadTheme==2) scale=quality7a;else scale=quality4a;             // define the specific scale for "Mood"
            // Set the right y- A X I S (for the leading graph) and each base lines for the upper and lower graph for this page "nr"
            drawG.drawAxisY(true,false, false,PrintPdf.attributeStrings(leadTheme), scale,topicColors[leadTheme], Color.LTGRAY,2,1);
            drawG.drawAxisY(false,false, false,PrintPdf.attributeStrings(leadTheme), scale,topicColors[leadTheme], Color.LTGRAY,2,1);
            // Draw the grafic (for the leading graph) on the upper and lower side on this page "nr"
            drawG.drawCurve(true, false, PrintPdf.attributeStrings(leadTheme),topicColors[leadTheme],3);
            drawG.drawCurve(false, false, PrintPdf.attributeStrings(leadTheme),topicColors[leadTheme],3);
            topic=(leadTheme+1)%4;
            }
    }
    /*                                   E N D: "drawMood"
       ==========================================================================================================================
    */

    private void drawSleep(Canvas canvas, int nr) {
        // Setting for Sub-titles
        Paint paint = new Paint();
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);
        // If requested: Paint the common right Y-axe and the curve respect to the leadTheme
        boolean upperFree=true;

        if (leadTheme<0) {canvas.drawText(PrintPdf.title(1,false), 50, 100, paint);upperFree=false;}
        else canvas.drawText(PrintPdf.title(1,true), 50, 100, paint);
        // Define Sleeping Scaale
        drawG.putSleepScale(extractSleepScale());

        // Set the x- A X I S (with the common interval of time) and other base lines for the upper grafic
        drawG.drawAxisX(true, false,topicColors[7], Color.BLACK, 12, 2, 1);


        // scaleSleepTime() ---> create {"0",...,"sleepTimeRange"}
        drawG.drawAxisY(true, true, false,PrintPdf.sleepTimeString , drawG.getSleepScale(),topicColors[5], Color.BLACK,2, 1);
        // Draw the at the upper chart for the sleep time
        drawG.drawChart(true, false, false,PrintPdf.sleepTimeString,topicColors[5]);

        if (leadTheme>-1) {
            paint.setColor(Color.LTGRAY);
            String[] scale = quality4a; if (leadTheme == 2) scale = quality7a;   // Scale is at default with 4 degrees, only in case for Mood with 7
            // Paint the common  Y-axe respect to the leadTheme
            drawG.drawAxisY(true, false, false, PrintPdf.attributeStrings(leadTheme), scale, topicColors[leadTheme], Color.BLACK, 2, 1);
            // Paint the common curve respect to the leadTheme
            drawG.drawCurve(true, false, PrintPdf.attributeStrings(leadTheme), topicColors[leadTheme], 3);
            upperFree=false;                                                     // Note that the upper grafic place was taken
        }

        // Set the lower x- A X I S
        drawG.drawAxisX(false, false,topicColors[7], Color.BLACK, 12, 2, 1);


        // P A I N T  the obligatory lower grafic elements
                // Draw the Spectrum for Delusion
        String[] attribute1={PrintPdf.delusionString,PrintPdf.delusionString,""};
        drawG.drawBoolSpectrum(false, false, attribute1,topicColors[7], 4, 1);
                // Draw the Spectrum for SleepInterruptions
        String[] attribute2={PrintPdf.sleepInterruptionString,PrintPdf.sleepInterruption1String,PrintPdf.sleepInterruption2String};
        drawG.drawBoolSpectrum(false, false, attribute2,topicColors[7], 4, 2);
                // Draw the Spectrum for Alcohol
        String[] attribute3={PrintPdf.alcoholString,PrintPdf.alcoholString,""};
        drawG.drawBoolSpectrum(false, false, attribute3,topicColors[7], 4, 3);
                // Draw the Spectrum for Drugs
        String[] attribute4={PrintPdf.drugsString,PrintPdf.drugsString,""};
        drawG.drawBoolSpectrum(false, false, attribute4,topicColors[7], 4, 4);

        // P A I N T  grafic elements for the sleep quality:
        // 1. right y-axis (at upper if upperFree else at lower)
        drawG.drawAxisY(upperFree, false, false, PrintPdf.sleepQualityString,quality5a,topicColors[6], Color.BLACK,2, 1);
        // 2. curve (at upper if upperFree else at lower)
        drawG.drawCurve(upperFree, false,PrintPdf.sleepQualityString,topicColors[6] ,3); //  ;
    }
    /*                                   E N D: "drawSleep"
       ==========================================================================================================================
    */

    //Determinate the maximum of Sleep time inside the given time Interval and give back a scale list
    private String[] extractSleepScale() {
        int max = 0;
        long lastDay = Zeit.later(firstDay, days);
        for (int i = 0; i < listMood.size(); ++i) {
            if (listMood.get(i).getDate() > lastDay) break;
            if (listMood.get(i).getSleepTime() > max) max = listMood.get(i).getSleepTime();
            }
        String[] scale = new String[max + 1];
        for (int i = 0; i <= max; ++i) scale[i] = String.valueOf(i);
        return (scale);
        }


    /* =======================================================================================================================
        drawPositive will paint on the PdfDocument "page" the Graph for the positive occurrencies
    */
    private void drawPositive(Canvas canvas) {
        Paint paint = new Paint();
        // Sub-title of the Positive Occurrencies
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);
        if (leadTheme<0) canvas.drawText(PrintPdf.title(2,false), 50, 100, paint);
        else canvas.drawText(PrintPdf.title(2,true), 50, 100, paint);

        // Set the x- A X I S (with the common interval of time) and other base lines for the upper grafic
        drawG.drawAxisX(true, false,topicColors[7], Color.BLACK, 12, 2, 1);

        // Set the y- A X I S if comparision is choosen
        if (leadTheme>-1) {
            paint.setColor(Color.LTGRAY);
            String[] scale = quality4a; if (leadTheme == 2) scale = quality7a;   // Scale is at default with 4 degrees, only in case for Mood with 7
            // Paint the common  Y-axe respect to the leadTheme
            drawG.drawAxisY(true, false, false, PrintPdf.attributeStrings(leadTheme), scale, topicColors[leadTheme], Color.BLACK, 2, 1);
            // Paint the common curve respect to the leadTheme
            drawG.drawCurve(true, false, PrintPdf.attributeStrings(leadTheme), topicColors[leadTheme], 3);
        }

        // Set the lower x- A X I S
        drawG.drawAxisX(false, false,topicColors[7], Color.BLACK, 12, 2, 1);


        // P A I N T  grafic elements for the diat:
        // 1. right y-axis
        drawG.drawAxisY(true, true, false, PrintPdf.dietString,quality3a,topicColors[7], Color.BLACK,2, 1);
        // 2. curve
        drawG.drawCurve(true, false,PrintPdf.dietString,topicColors[7] ,3);

        // Lable the boolean positive Occurrencies
        int[] rgb={100,50,150};
        String[] attribute1={PrintPdf.sportString,PrintPdf.sportString,""};
        drawG.drawBoolSpectrum(false, false, attribute1,rgb, 4, 1);

        String[] attribute2={PrintPdf.mindfullnessString,PrintPdf.mindfullnessString,""};
        drawG.drawBoolSpectrum(false, false, attribute2,rgb, 4, 2);

        String[] attribute3={PrintPdf.journalingString,PrintPdf.journalingString,""};
        drawG.drawBoolSpectrum(false, false, attribute3,rgb, 4, 3);


        // Define the row-graphs of the positive events
    }
    /*                                   E N D: "drawPositive"
       ==========================================================================================================================
    */

    /* =======================================================================================================================
    drawDiary will paint on the PdfDocument "page" the Graph for the Memo
    */
    private void drawDiary(Canvas canvas) {
        Paint paint = new Paint();
        // Sub-title of the Memos
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);
        canvas.drawText(PrintPdf.title(3,false), 50, 100, paint);

        // Define the head line of the table
        int yHeadLine=36;
        int y_top=1*canvas.getHeight()/6;
        int y_down=11*canvas.getHeight()/12;
        int x_left=canvas.getWidth()/12;
        int x_right=11*canvas.getWidth()/12;
        int backGround=Color.argb(255,0,0,255);
        paint.setColor(backGround);
        canvas.drawRect(x_left,y_top,x_right,y_top+yHeadLine,paint);   // Draw the background for the head line
        paint.setColor(Color.argb(255,255,255,255));
        paint.setTextSize(yHeadLine/2);
        int x_month=x_left+5;
        canvas.drawText(PrintPdf.monthString, x_month, y_top+3*yHeadLine/5, paint);
        int x_day=x_month+5+PrintPdf.monthString.length()*yHeadLine/2;
        canvas.drawText(PrintPdf.dayString, x_day, y_top+3*yHeadLine/5, paint);
        int x_memo=x_day+5+PrintPdf.dayString.length()*yHeadLine/2;
        canvas.drawText(PrintPdf.memoString, x_memo, y_top+3*yHeadLine/5, paint);

        // Define the frame of the table
        paint.setColor(backGround);
        int memoNumbers=0;
        for (int i=0;i<listMood.size();++i) if (listMood.get(i).getMemo()!="") ++memoNumbers;
        int steps=(y_down-y_top-yHeadLine)/(memoNumbers+1);
        if (steps>24) steps=24;
        canvas.drawLine(x_month-5,y_top,x_month-5,y_top+yHeadLine+memoNumbers*steps, paint);  // First vertical line of table
        canvas.drawLine(x_day-5,y_top,x_day-5,y_top+yHeadLine+memoNumbers*steps, paint);      // Second vertical line of table
        canvas.drawLine(x_memo-5,y_top,x_memo-5,y_top+yHeadLine+memoNumbers*steps, paint);    // Third vertical line of table
        canvas.drawLine(x_right,y_top,x_right,y_top+yHeadLine+memoNumbers*steps, paint);                  // Last vertical line of table
        int y_now = y_top + yHeadLine;                                                                          // Define the y base for the horizontal lines
        for (int i=0;i<memoNumbers;++i){y_now+=steps; canvas.drawLine(x_right, y_now, x_left, y_now , paint);}  // Draw the horozontal lines of table

        // Insert the memo infos into the table
        paint.setTextSize(12);
        y_now = y_top + yHeadLine+3*steps/5;
        for (int i=0;i<listMood.size();++i)
            if (listMood.get(i).getMemo()!="") {
                canvas.drawText(PrintPdf.monthString(listMood.get(i).getDate()), x_month, y_now, paint);
                canvas.drawText(String.valueOf(listMood.get(i).getDate() % 100), x_day, y_now, paint);
                canvas.drawText(listMood.get(i).getMemo(), x_memo, y_now, paint);
                y_now += steps;
            }
    }
    /*                                   E N D: "drawdiary"
       ==========================================================================================================================
    */

    /* =======================================================================================================================
    drawDiary will paint on the PdfDocument "page" the Graph for the Memo
    */
    private void drawMedications(Canvas canvas) {
        Paint paint = new Paint();
        // Sub-title of the Medications
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);
        if (leadTheme<0) canvas.drawText(PrintPdf.title(4,false), 50, 100, paint);
        else canvas.drawText(PrintPdf.title(4,true), 50, 100, paint);

        // Set the x- A X I S (with the common interval of time) and other base lines for the upper grafic
        drawG.drawAxisX(false, true,topicColors[7], Color.BLACK, 12, 2, 1);

        // Define the headline of y-medications
        int[] rgb = {100, 50, 150};
        paint.setTextSize(20);
        paint.setColor(Color.argb(255,rgb[0],rgb[1],rgb[2]));
        canvas.drawText(PrintPdf.medString, 10, 5*canvas.getHeight()/24-10, paint);
        // Define each line
        for(int i=0;i<listTherapie.size();++i) {
            String[] attribute = {"Medications", listTherapie.get(i).getMedication(), String.valueOf(listTherapie.get(i).getPlanTime()), String.valueOf(listTherapie.get(i).getId())};
            drawG.drawBoolSpectrum(false, true, attribute, rgb, listTherapie.size()+1, i+1);
            }

        // Set the y- A X I S if comparision is choosen
        if (leadTheme>-1) {
            paint.setColor(Color.LTGRAY);
            String[] scale = quality4a; if (leadTheme == 2) scale = quality7a;   // Scale is at default with 4 degrees, only in case for Mood with 7
            // Paint the common  Y-axe respect to the leadTheme
            drawG.drawAxisY(false, false, true, PrintPdf.attributeStrings(leadTheme), scale, topicColors[leadTheme], Color.BLACK, 2, 1);
            // Paint the common curve respect to the leadTheme
            drawG.drawCurve(false, true, PrintPdf.attributeStrings(leadTheme), topicColors[leadTheme], 3);
        }

    }
    /*                                   E N D: "drawMedications"
       ==========================================================================================================================
    */

    /* =======================================================================================================================
    drawDrinking will paint on the PdfDocument "page" the Graph for the Drinks
    */
    private void drawDrinking(Canvas canvas) {
        Paint paint = new Paint();
        // Sub-title of the Medications
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);
        if (leadTheme<0) canvas.drawText(PrintPdf.title(5,false), 50, 100, paint);
        else canvas.drawText(PrintPdf.title(5,true), 50, 100, paint);

        // Set the x- A X I S (with the common interval of time) and other base lines for the upper grafic
        drawG.drawAxisX(true, false,topicColors[7], Color.BLACK, 12, 2, 1);

        // Define the headline of y-drinks
        int[] rgb = {100, 50, 150};
        paint.setTextSize(20);
        paint.setColor(Color.argb(255,rgb[0],rgb[1],rgb[2]));
        // Define in the actual object of DrawGrafics the specific drinking parametrica
        drawG.putNameDrinks(extractNameDrinks());
        drawG.putTableDrinks(extractTableDrinks(drawG.getNameDrinks()));
        drawG.putMaxDiaryVolume(maxLiquits(drawG.getTableDrinks()));
        drawG.putTopNnumber(5);
        if (drawG.getTopNnumber()<drawG.getNameDrinks().length) drawG.putTopNnumber(drawG.getNameDrinks().length);
        drawG.putTopNdrinks();
        String[] scale=new String[11];
        for (int i=0;i<=10;++i) scale[i]=String.valueOf(i*drawG.getMaxDiaryVolume()/10);
        drawG.putDrinkScale(scale);

        // Paint the common drinking quantities
        drawG.drawAxisY(true, true, false, PrintPdf.attributeStrings(10)+"(ml)",drawG.getDrinkScale(), topicColors[8], Color.BLACK, 2, 1);
        drawG.drawChart(true, false, false, PrintPdf.drinkString,topicColors[8]);

        // Set the y- A X I S if comparision is choosen
        if (leadTheme>-1) {
            paint.setColor(Color.LTGRAY);
            scale = quality4a; if (leadTheme == 2) scale = quality7a;                               // Scale is at default with 4 degrees, only in case for Mood with 7
            // Paint the common  Y-axe respect to the leadTheme
            drawG.drawAxisY(true, false, false, PrintPdf.attributeStrings(leadTheme), scale, topicColors[leadTheme], Color.BLACK, 2, 1);
            // Paint the common curve respect to the leadTheme
            drawG.drawCurve(true, false, PrintPdf.attributeStrings(leadTheme), topicColors[leadTheme], 3);
        }

        // Set the A X I S for the lower grafic
        drawG.drawAxisX(false, false,topicColors[7], Color.BLACK, 12, 2, 1);
        drawG.drawAxisY(false, false, false, PrintPdf.attributeStrings(10)+"(ml)",drawG.getDrinkScale(), topicColors[8], Color.BLACK, 2, 1);

        // Set the Accumulation charts in the lower grafic
        drawG.drawChart(false, false, true,PrintPdf.attributeStrings(10),topicColors[7]);

    }
    /*                                   E N D: "drawDrinking"
       ==========================================================================================================================
    */

    private String[] extractNameDrinks(){
        String[] liq=new String[listDrinks.size()];
        int nrLiquits=0;
        boolean found=false;
        for (int i=0;i<listDrinks.size();i++) {
            found = false;
            for (int j = 0; j < nrLiquits; ++j)
                if (liq[j].equals(listDrinks.get(i).getDrinks())) found = true;
            if (!found) {
                liq[nrLiquits] = listDrinks.get(i).getDrinks();
                ++nrLiquits;
                }
            }
        String[] liquits=new String[nrLiquits];
        for (int i=0;i<nrLiquits;++i) liquits[i]=liq[i];
        return(liquits);
    }

    private int[][] extractTableDrinks(String[] nameD){
        int[][] tableD=new int[days][nameD.length+1];
        for (int i=0;i<days;i++) for (int k = 0; k < nameD.length; ++k) tableD[i][k]=0;                 // Init the values in array to value of "0"
        for (int j = 0; j < listDrinks.size(); ++j) for (int k = 0; k < nameD.length; ++k)              // Through all elements of the List and all possible Drink
            if (nameD[k].equals(listDrinks.get(j).getDrinks()))                                         //    if the element match the actual name of the drink
                for (int i=0;i<days;++i)                                                                //    look through all possible day
                    if (listDrinks.get(j).getDate()==Zeit.later(firstDay,i))                            //    to find the correspondent day
                        tableD[i][k]+=Integer.parseInt(listDrinks.get(j).getQuantity());                //    that we can sum it on this daily consumption
        for (int i=0;i<days;++i) {                                                                      // Note the total consuption of a day
            tableD[i][nameD.length] = 0;                                                                //    initialize at 0
            for (int k = 0; k < nameD.length; ++k)                                                      //    pass through all drink typs to
                tableD[i][nameD.length] += tableD[i][k];                                                //    sum also the consuption of actual one
        }
        return(tableD);
    }


    int maxLiquits(int[][] liquitTable){
        int max=0;
        for (int i=0;i<liquitTable.length;++i) if (liquitTable[i][liquitTable[0].length-1]>max)
                                                max=liquitTable[i][liquitTable[0].length-1];
        return(max);
    }
}
