package de.psychology.mooddiary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.List;
/* ====================================================================== =================================================
        C L A S S : DrawingGrafics
        We assume we have a canvas which is subdivided into three vertical areas with the following base set:
                A) the first has the height of 1/6 of the size of the canvas and is reserved for the Head information of the page
                B) the second has the height of 5/12 of the size of the canvas and is reserved for the first vertical graphic element
                C) the third has the height of 5/12 of the size of the canvas and is reserved for the second vertical graphic element
        The base width of all three areas is centered inside 3/16 and 15/16 width of the canvas
    ====================================================================== =================================================
*/

public class DrawingGrafics {
    private Canvas canvas;                                  // The canvas were the graphic will drawn
    private long firstDay;                                  // The first day we will represent in the graphic
    private long lastDay;                                   // The last day we will represent in the graphic
    private int leadTheme;                                  // The criteria which will be used to compare the actual topic
    private int w_begin;                                    // The x-position where the right y-axis will be fixed
    private int w_end;                                      // The x-position where the left y-axis will be fixed
    private int h_upper_top;                                // The top height of y-axis for the upper graph
    private int h_upper_down;                               // The down height of y-axis for the upper graph (position for the line of the x-axis)
    private int h_lower_top;                                // The top height of y-axis for the lower graph
    private int h_lower_down;                               // The down height of y-axis for the lower graph (position for the line of the x-axis)
    private int day_unit;                                   // Define the extension for one day in the x-Axis
    private int days;                                       // Number of days represented on the x-axis
    private List<MoodMemo> listMood;                        // Extracted list (through the indicated time span) from the MoodMemo database
    private List<PositiveMemo> listPositive;                // Extracted list (through the indicated time span) from the PositiveMemo database
    private List<DrinksMemo> listDrinks;                  // Extracted list (through the indicated time span) from the TherapyMemo database
    private List<TakeMedicationMemo> listMedication;        // Extracted list (through the indicated time span) from the TakeMedicationMemo database

    private String[] sleepScale;
    public String[] getSleepScale(){return(sleepScale);}
    public void putSleepScale(String[]scale){sleepScale=scale;}

    private String[] nameDrinks={};
    public String[] getNameDrinks(){return(nameDrinks);}
    public void putNameDrinks(String[] names){nameDrinks=names;}

    private static int[][] tableDrinks={{}};
    public int[][] getTableDrinks(){return(tableDrinks);}
    public void putTableDrinks(int[][] table){tableDrinks=table;}

    private int maxDiaryVolume;
    public int getMaxDiaryVolume(){return(maxDiaryVolume);}
    public void putMaxDiaryVolume(int volume){maxDiaryVolume=volume;}

    private String[] drinkScale;
    public String[] getDrinkScale(){return(drinkScale);}
    public void putDrinkScale(String[]scale){drinkScale=scale;}

    private int topNnumber;
    public void putTopNnumber(int number){topNnumber=number;}
    public int getTopNnumber(){return(topNnumber);}

    private int[] topNDrinks;
    public int[] getTopDrinks(){return(topNDrinks);}
    public void putTopNdrinks(){
        if (topNnumber > nameDrinks.length) topNnumber = nameDrinks.length;
        int minQuantity = 10000000;
        int minIndex = -1;
        topNDrinks= new int[topNnumber];
        int quantity[] = new int[topNnumber];
        // Insert the first n elements in the top and decide which is the minimal element
        for (int i = 0; i < topNnumber; ++i) {
            topNDrinks[i] = i;
            quantity[i] = 0;
            for (int j = 0; j < tableDrinks.length; ++j) quantity[i] += tableDrinks[j][i];
            if (minQuantity > quantity[i]) {
                minQuantity = quantity[i];
                minIndex = i;
            }
        }
        // Look if an other element need to be inseted in the list
        for (int i = topNnumber; i < nameDrinks.length; ++i) {
            int sum = 0;
            for (int j = 0; j < tableDrinks.length; ++j) sum += tableDrinks[j][i];
            if (sum > minQuantity) {
                topNDrinks[minIndex] = i;
                quantity[minIndex] = sum;
                minQuantity = sum;
                for (int k = 0; k < topNnumber; ++k)
                    if (quantity[k] < quantity[minIndex]) {
                        minQuantity = quantity[k];
                        minIndex = k;
                    }
            }
        }
        return;
    }


    public DrawingGrafics(Canvas can,long first, int days, int lead,List<MoodMemo> listM,List<PositiveMemo> listP,List<DrinksMemo> listD,List<TakeMedicationMemo> listMed)
        { canvas=can;                                       // Pass the canvas of apresentation
          firstDay=first;                                   // Pass the last day of apresentation
          leadTheme=lead;                                   // Pass the criteria we use to compare with all the others
          listMood=listM;                                   // Pass the mood database acess
          listPositive=listP;                               // Pass the positive Occurencies database access
          listDrinks=listD;                                // Pass the TherapyMemo database access
          listMedication=listMed;                             // Pass the TakeMedicationMemo database access

          h_upper_top=5*canvas.getHeight()/24;              // Beginning of the upper graph is after the heading 1/6 of the page plus the 1/24 of the graph label
          h_upper_down=h_upper_top+canvas.getHeight()/3;    // Ending of the upper graph is h_upper_top plus the 1/3 for the range of ther proper graph
          h_lower_top=h_upper_down+canvas.getHeight()/12;   // Beginning of the lower graph is h_upper_top plus two times 1/24 (the under labeling of the upper
                                                            //                                                                  and the headinh labeling of the lower one)
          h_lower_down=h_lower_top+canvas.getHeight()/3;    // Ending of the lower graph is h_lower_top plus the 1/3 for the range of ther proper graph
                                                            // Getting the time distance between the first and last day
          w_begin=3*canvas.getWidth()/16;                   // Beginning the proper graph at left hand only at 3/16 of the page width
          w_end=15*canvas.getWidth()/16;                    // Ending the proper graph at right hand only at 1/16 of the page width
          this.days=days;
          day_unit=w_end-w_begin;                           // Define the extension for the x-Axis
          if (days>-1) day_unit/=(days+1);                  // The unit of one day is this unit divide by the days plus one (if we have a positive difference of days)
          w_end=w_begin+(days+1)*day_unit;                  // Adapt the ending point to avoid rounding errors
        }
    /*                                   E N D: "MaxSleepTime"
       ==========================================================================================================================
    */

    /* =======================================================================================================================
        Function: drawAxisX
        Assuming we represent a x-axis of at maximum 28 days for a graph inside the graphic area B) [upper==true&&big==false]
        or C) [upper==false&&big==false] or the hole page [big=true] and we paint a curve applying the following parameters:
            - upper:                    indicates drawing the z-axis of the upper (=true) or the lower(=false) from the two possible grafics on this side
            -  big                      decides if a extend format of the graphic element (in a full page) is choosen
            - rgbLabel:                 the indicates rgb color is the base for the shades used for the color for the (i+1)-th month used
            - colorLines:               with this color for the lines
            - coordinationLineStroke:   the size for the text which indicates the dates represented in the x-line
            - coordinationLineStroke:   thickness of the line for the y-axis in the coordination system
            - helpLineStroke:           Thickness of the orientations lines for the y-axis
        Side-effect: A graphical chart which contains:
            - a x-axis on the botton side
            - the scaling values of the x-axis as numerical values of the month that the respective date represents
            - marking points in the x-axis for all occuring dates
//          - a vertical help line over every scaling value
            - under the numerical values of the dates we write the name of the every month were it at first times occurres
     */
    public void drawAxisX(boolean upper, boolean big,int[] rgbLabel, int colorLines, int textSize, int coordinationLineStroke, int helpLineStroke) {
        Paint paint = new Paint();                                // On the paint parameters to performe
        int h_top=h_upper_top;                                    // The upper graph is the dafault
        if (!upper) h_top+=5*canvas.getHeight()/12;               // Adjust position in case we use lower graph
        int h_down=h_top+canvas.getHeight()/3;                    // Define down position by add 1/3 on h_top
        if (big) {h_top=h_upper_top;h_down=h_lower_down;}         // If big format is requested extend the range of the graphic area to the whole page
        // paint x-axis completely in a grew color
        paint.setColor(colorLines);                               // Set the color as basecolor
        paint.setStrokeWidth(coordinationLineStroke);             // Set the Stroke width onto coordinationLineStroke
        canvas.drawLine(w_begin, h_down, w_end , h_down, paint);  // Write the x-axis

        // paint the first and last vertical line in colorLines and in helpLineStroke to mark the last index in the color of this date area
//        paint.setStrokeWidth(helpLineStroke);                     // Set the Stroke width onto helpLineStroke
//        canvas.drawLine(w_begin, h_top, w_begin, h_down, paint);       // Write the rightes y-line with the same attribute as the x-line
//        canvas.drawLine(w_end, h_top, w_end, h_down, paint);       // Write the rightes y-line with the same attribute as the x-line

        // intialisation before incremential labeling of the x-axis
        int monthIndex=-1;                                         // which indicate in which color of colorsLabel we have to represent the actual date
        paint.setTextSize(textSize);                              // Set the textsize for the first day description of the x-line
        int modulo=days/7;                                        // For controling that the day indications on x-lines not overlaps we have a gap between each number
        // acording the number of the weeks in the timeline
        // Incremential labeling of the x-axis
        long thisDay=firstDay;                                    // To success with the first day consecutevly
        // Alternate color for the dates in consecutive month
        int colorLabel=colorLabel=Color.argb(255,rgbLabel[0],rgbLabel[1],rgbLabel[2]);
        for (int i =0;i<=days;i++){
            // Paint the month of the actual date if it occurres the first time
            if ((monthIndex==-1) || ((thisDay % 100)==1)) {                 // For the first day or all first day of the month
                monthIndex = (monthIndex + 1) % 2;                          // alternate the color index between the month
                // Determinate the actual Color for the label
                colorLabel=Color.argb(255-monthIndex*80,rgbLabel[0],rgbLabel[1],rgbLabel[2]);
                paint.setColor(colorLabel);                                 // Set the color (with alternate colors for consecutive month)
                // Write the new month on under the x-axis (in English language)
                canvas.drawText(PrintPdf.monthString(thisDay), w_begin + i * day_unit, h_down + 2*textSize, paint);
            }
            // paint the vertical line in colorLines to mark the horizontal index in the color of this date area
            paint.setColor(colorLines);                  // Define its color in colorLines
            paint.setStrokeWidth(helpLineStroke);        // Its width as helpLineStroke
            // Paint the help line
//           canvas.drawLine(w_begin+i*day_unit, h_top, w_begin+i*day_unit, h_down, paint);
            // paint the mark of the x-axis with the value of the correspondente day
            paint.setColor(colorLabel);     // Set the color of the actual month
            paint.setStrokeWidth(coordinationLineStroke);// Set width as coordinationLineStroke
            // Paint the marking point in the x-axis for the actual date
            canvas.drawLine(w_begin+i*day_unit, h_down, w_begin+i*day_unit, h_down-textSize/2, paint);
            // paint the value of the actual day at the x-axis if it is acording to the permitted modulç
            if (i%modulo==0)                          // We only indicating 8 numerical values of the date
                canvas.drawText(String.valueOf(thisDay % 100), w_begin + i * day_unit, h_down+textSize, paint);
            thisDay=Zeit.nextDay(thisDay);                        // Determinate the next datum as day after this day
        }
    }
    /*                                                          E N D:  drawAxeX
     =======================================================================================================================
    */

    /* =======================================================================================================================
        Function: drawAxisY
        Assuming we represent the y-axis of a graph inside the graphic area B) [upper==true&&big==false]
        or C) [upper==false&&big==false] or the hole page [big=true] and we paint a curve applying the following parameters:
            - upper:                    indicates drawing the z-axis of the upper (=true) or the lower(=false) from the two possible grafics on this side
            - left:                     indicates drawing the z-axis on the left (=true) or the right(=false) side of the grafic
            -  big                      decides if a extend format of the graphic element (in a full page) is choosen
            - nameLabel:                with this name for the axis
            - scaleLabel:               on this scale for the axis
            - colorLabel:               with this rgb color for all label elements
            - colorLines:               with this color for the lines
            - coordinationLineStroke:   thickness of the line for the y-axis in the coordination system
            - helpLineStroke:           Thickness of the orientations lines for the y-axis
        Side-effect: A graphical chart which contains:
            - a y-axis on the right[left==false] or the left[left==true] side
            - over this axis a name for the dimension of the y-axis
            - the scaling values of the y-axis
            - a horizontal help line besides every scaling value (only if left==false)
     */
    public void drawAxisY(boolean upper, boolean left, boolean big, String nameLabel, String[] scaleLabel,int[] colorLabel, int colorLines,int coordinationLineStroke, int helpLineStroke) {
        // Set the offsets for the dimension of the site with units equal to 1/72 inch
        Paint paint = new Paint();                                      // Define a new paint toolbox
                                                                        // define the int value for the rgb color of the label
        int labelColor=Color.argb(255,colorLabel[0],colorLabel[1],colorLabel[2]);
        int textSize=20;                                                // Define the size of the text
        int h_top=h_upper_top;                                          // The upper graph is the dafault
        if (!upper&&!big) h_top+=5*canvas.getHeight()/12;               // Adjust position in case we use lower graph
        int h_down=h_top+canvas.getHeight()/3;                          // Define down position by add 1/3 on h_top
        if (big) h_down=h_lower_down;                                   // If big format is requested extend the range of the graphic area to the whole page

        // Draw the name of the y axis
        paint.setTextSize(textSize);                                    // Define the size for writing the axis name
        paint.setColor(labelColor);                                     // Set the color to lable the criteria for y-axis
        int offSet=textSize/2;                                          // As default we consider that we draw the y-axis on left, so we retray the scale values one unit at left of the axis
        int offMult=-textSize/2;                                        // In this case we also will retray the scale values also leftside in propotion of its size
        int w=w_begin;
        if (!left){w=w_end;offMult=0;}                                  // If the orientation is not left the end position as reference point for the z-axis
        // Write the name of the y-axix
        if (left) canvas.drawText(nameLabel, 10,h_top-2*textSize/3, paint);
        else canvas.drawText(nameLabel, w-((nameLabel.length()+2)*textSize/3),h_top-2*textSize/3, paint);

        // Draw the y-axis
        paint.setStrokeWidth(coordinationLineStroke);                   // Set the stroke size for the y-axis
        paint.setColor(colorLines);                                     // Set the color we use to draw the y-axis
        if (left) canvas.drawLine(w_begin, h_top, w_begin, h_down, paint);
        else canvas.drawLine(w_end, h_top, w_end, h_down, paint);       // Write left respective right y-axix with the length of 1/3 height of the canvas


        // Draw for each scale unit its value and the correspondente help line
        textSize/=2;
        paint.setStrokeWidth(helpLineStroke);                           // Set the stroke size for the help axis
        int value_unit =(h_down-h_top)/(scaleLabel.length-1);           // Set as base value the height of the y-axis which is the third of the canvas height
        for (int i=0;i<scaleLabel.length;++i) {                         // For each scale element do:
            paint.setColor(Color.argb(255-i*80/(scaleLabel.length),colorLabel[0],colorLabel[1],colorLabel[2]));
            // Write the value of the scale element or at the left or at right side of the y-axix
            canvas.drawText(scaleLabel[i], w+offSet+offMult*(scaleLabel[i].length()+2), h_down-i*value_unit+2*textSize/3, paint);
            //  Draw the help line of the scale element greater than the first one
            if (i>0) {
                paint.setColor(colorLines);                             // Set the color for the help lines of the y-axis
                                                                        // draw the help line only for the left y-axis
                if (!left) canvas.drawLine(w_begin, h_down - i*value_unit, w_end, h_down -i*value_unit, paint);
                paint.setColor(labelColor);                             // Set the color back to lable the criteria for y-axis
                }
            }
    }
    /*                                                          E N D:  drawAxeY
     =======================================================================================================================
    */

    /* =======================================================================================================================
       Function: drawCurve
        Assuming we represent at maximum 28 days of a graph inside the graphic area B) [upper==true&&big==false]
        or C) [upper==false&&big==false] or the hole page [big=true] and we paint a curve applying the following parameters:
            - upper:                    indicates drawing the z-axis of the upper (=true) or the lower(=false) from the two possible grafics on this side
            -  big                      decides if a extend format of the graphic element (in a full page) is choosen
            - attribute:                its the attribute in the listMood or listPositive we want to represent
            - rgbColor:                 the graph will be drawn with this rgb color
            - stroke                    and this stroke
        Side-effect: A graphic curve will be drawn
     */
    public void drawCurve(boolean upper, boolean big, String attribute,int[] rgbColor,int stroke) {
        boolean moodType=databaseAttribute("Moodies",attribute);
        boolean positiveType=databaseAttribute("Positives",attribute);
        int diff=0;                                     // Rectificar entre real anf virtussl height of each graph point
        if (listMood.isEmpty()&&moodType) return;              // If the relevant list is empty then is nothing to do
        if (positiveType&&listPositive.isEmpty()) return;      // If the relevant list is empty then is nothing to do

        int base_color=Color.argb(255,rgbColor[0],rgbColor[1],rgbColor[2]);
        int h_top=h_upper_top;                             // The upper graph is the default
                                                           // and 1/24 for the titel header of the own graph
        if (upper==false) h_top+=(5*canvas.getHeight()/12);// In the lower graph this offset will be added 5/12 by the size of the first graph
        int h_down=h_top+canvas.getHeight()/3;             // The area of the line in the graph is only 1/3
        if (big) {h_top=h_upper_top;h_down=h_lower_down;}  // If big format is requested extend the range of the graphic area to the whole page

        Paint paint = new Paint();                       // Define a new paint setup
        // Define the units in the y-axe
        int value_unit = (h_down - h_top) / yScale(attribute);
        // Define the limit of the cycle
        int limit=listMood.size();                        // As default the limit in case of moodType
        if (!moodType) limit=listPositive.size();         // Rectify the limit in case of PositionType
        // Intialize the coordenates of the lines
        int x_now=0;int y_now=0;int x_next=0;int y_next=0;// Coordenates for the actual point and the next point of the graph
        // Passing all points of the graph
        for (int i = 0; i < limit; i++) {
            if (moodType)    { x_now = xValue(w_begin, day_unit, listMood.get(i).getDate(), listMood.get(i).getTime());
                               y_now = h_down - yMoodValue(listMood.get(i), attribute) * value_unit-diff; }
            if (positiveType){ x_now = xValue(w_begin, day_unit, listPositive.get(i).getDate(), listPositive.get(i).getTime());
                               y_now = h_down - (listPositive.get(i).getDiet()+1)* value_unit-diff;}
            paint.setStrokeWidth(stroke+1);
            paint.setColor(Color.argb(255-y_now*80/(yScale(attribute)+1),rgbColor[0],rgbColor[1],rgbColor[2]));
            canvas.drawPoint(x_now, y_now, paint);
            if (i < limit - 1) {
                paint.setStrokeWidth(stroke);
                paint.setColor(base_color);
                if (moodType){ x_next = xValue(w_begin, day_unit, listMood.get(i + 1).getDate(), listMood.get(i + 1).getTime());
                               y_next = h_down - (yMoodValue(listMood.get(i + 1), attribute) * value_unit)-diff;}
                else         { x_next = xValue(w_begin, day_unit, listPositive.get(i + 1).getDate(), listPositive.get(i + 1).getTime());
                               y_next = h_down - (listPositive.get(i + 1).getDiet()+1) * value_unit-diff;}
                canvas.drawLine(x_now, y_now, x_next, y_next, paint);
            }
        }
    }
    /*                                                          E N D:  drawCurve
     =======================================================================================================================
    */

    /* =======================================================================================================================
       Function: drawChart
        Assuming we represent at maximum 28 days of a chart inside the graphic area B) [upper==true&&big==false]
        or C) [upper==false&&big==false] or the hole page [big=true] and we paint a curve applying the following parameters:
            - upper:                    indicates drawing the z-axis of the upper (=true) or the lower(=false) from the two possible grafics on this side
            -  big                      decides if a extend format of the graphic element (in a full page) is choosen
            - attribute:                its the attribute in the listMood or listPositive we want to represent
            - rgbColor:                 the graph will be drawn with this rgb color
        Side-effect: A graphic chart will be drawn
     */
    public void drawChart(boolean upper, boolean big, boolean accumulation,String attribute,int[] rgbColor) {
        // Define the limit of the cycle
        int limit=listMood.size();                             // As default the limit in case of moodType

        boolean moodType=databaseAttribute("Moodies",attribute);
        boolean positiveType=databaseAttribute("Positives",attribute);
        boolean drinkType=databaseAttribute(PrintPdf.drinkString,attribute);
        if (listMood.isEmpty()&&moodType) return;               // If the relevant list is empty then is nothing to do
        if (positiveType&&listPositive.isEmpty()) return;       // If the relevant list is empty then is nothing to do
        if (drinkType&&listDrinks.isEmpty()) return;            // If the relevant list is empty then is nothing to do

        int h_top=h_upper_top;                                  // The upper graph is the default
        // and 1/24 for the titel header of the own graph
        if (upper==false) h_top+=(5*canvas.getHeight()/12);     // In the lower graph this offset will be added 5/12 by the size of the first graph
        int h_down=h_top+canvas.getHeight()/3;                  // The area of the line in the graph is only 1/3
        if (big) {h_top=h_upper_top;h_down=h_lower_down;}       // If big format is requested extend the range of the graphic area to the whole page

        Paint paint = new Paint();                              // Define a new paint setup
        // Define the units in the y-axe
        float value_unit = (float)(h_down - h_top) / yScale(attribute);

        if (!moodType) limit=listPositive.size();              // Rectify the limit in case of PositionType
        if (drinkType) limit=days;                             // In this case the limit is the number of days

        // Intialize the coordenates of the lines
        int x_now=0;int y_now=0;int x_last=w_begin;            // Coordenates for the actual point and the next point of the graph
        // Passing all points of the graph
        for (int i = 0; i < limit; i++) {
            // Define actual point for the Mood values
            int value=0;
            if (moodType) x_now = xValue(w_begin, day_unit, listMood.get(i).getDate(), listMood.get(i).getTime());
            if (positiveType) x_now = xValue(w_begin, day_unit, listPositive.get(i).getDate(), listPositive.get(i).getTime());
            if (drinkType) x_now = w_begin + day_unit * i;
            if (i == 0 && x_last < x_now - day_unit) {x_last = x_now - day_unit;
                                                      paint.setColor(Color.LTGRAY);
                                                      canvas.drawLine(x_last, h_top, x_last, h_down, paint);}
            if (accumulation) {
                if (drinkType) {
                    if (i==0) labelSubdivided(h_top,h_down,10);
                    for (int j=0;j<tableDrinks[0].length-1;++j) {
                        int sum=0;
                        int y_last=h_down;
                        for (int k=0;k<topNnumber;++k) {
                            sum += tableDrinks[i][topNDrinks[k]];
                            y_now=h_down -((h_down-h_top)*sum)/yScale(PrintPdf.drinkString);
                            paint.setColor(Color.argb(255, DemoPrintDocumentAdapter.topicColors[k][0], DemoPrintDocumentAdapter.topicColors[k][1], DemoPrintDocumentAdapter.topicColors[k][2]));
                            canvas.drawRect(x_last, y_now, x_now, y_last, paint);   // Draw the fraction
                            y_last=y_now;
                            }
                        }
                    }
                 }
            else {
                if (moodType) value = (int) (yMoodValue(listMood.get(i), attribute) * value_unit);
                if (positiveType) value = (int) ((listPositive.get(i).getDiet() + 1) * value_unit);
                if (drinkType)  value = (int) (tableDrinks[i][tableDrinks[0].length - 1] * value_unit);

                y_now = h_down - value;
                // Define the a new begin if the choosen date is much different as the first register
                // Draw the actual chart
                // Set the color nuance according to the height of y_now
                int transparence = (255 * value) / (h_down - h_top);
                paint.setColor(Color.argb(transparence, rgbColor[0], rgbColor[1], rgbColor[2]));
                canvas.drawRect(x_last, y_now, x_now, h_down, paint);   // Draw the line
                }
            x_last=x_now+1;                                       // Increment the lasgt value of x onto the actual
           }
        // If the last x value is not the final date draw a closure line
        if (x_last<w_end) {paint.setColor(Color.LTGRAY);canvas.drawLine(x_last, h_top, x_last, h_down, paint);}
        }
    /*                                                          E N D:  drawChart
     =======================================================================================================================
    */

    private void labelSubdivided(int top,int down,int x){
        Paint paint = new Paint();                              // Define a new paint setup
        int y=down-10;
        for (int k=0;k<topNnumber;++k) {
            paint.setColor(Color.argb(255-k*255/topNnumber, DemoPrintDocumentAdapter.topicColors[k][0], DemoPrintDocumentAdapter.topicColors[k][1], DemoPrintDocumentAdapter.topicColors[k][2]));
            canvas.drawText(nameDrinks[topNDrinks[k]], x, y, paint);
            int sub=0;
            if ((topNnumber-1)>1) sub=(down-top)/(topNnumber-1);else sub=(down-top)/2;
            y-=sub;
            }
        return;
    }


    /* =======================================================================================================================
       Function: drawBoolSpectrum
        Assuming we represent at maximum 28 days of a boolean chart along y-axis inside the graphic area B) [upper==true&&big==false]
        or C) [upper==false&&big==false] or the hole page [big=true] and we paint a curve applying the following parameters:
            - upper:                    indicates drawing the z-axis of the upper (=true) or the lower(=false) from the two possible grafics on this side
            -  big                      decides if a extend format of the graphic element (in a full page) is choosen
            - attribute:                there are several String attributes, the first is the type of the Element, the second is the name of
                                        of the element and the nexts are suplementary one (like for medications the time of use)
            - rgbColor:                 the graph will be drawn with this rgb color
            - boolElements              indicates how much boolElements would be represented onside the y-axis
            - actualBoolElement         is the position from the top where we will represent the actual element on the y-axis
        Side-effect: A graphic Sprectrum of a boolean value alongside the y-axis over the time x-axis will be drawn
     */
        public void drawBoolSpectrum(boolean upper, boolean big, String[] attribute,int[] rgbColor, int boolElements, int actualBoolElement) {
        Paint paint = new Paint();                             // Define a new paint setup
        int textSize=20;                                       // Define the size of the text
        paint.setTextSize(textSize);                                    // Define the size for writing the axis name
        paint.setColor(Color.argb(255-actualBoolElement*120/boolElements,rgbColor[0],rgbColor[1],rgbColor[2]));

        // Define the limits for the y-axis
        int h_top=h_upper_top;                                 // The upper graph is the default
        if (upper==false) h_top+=(5*canvas.getHeight()/12);    // In the lower graph this offset will be added 5/12 by the size of the first graph
        int h_down=h_top+canvas.getHeight()/3;                 // The area of the line in the graph is only 1/3
        if (big) {h_top=h_upper_top;h_down=h_lower_down;}      // If big format is requested extend the range of the graphic area to the whole page
        int value_unit = (h_down - h_top) / boolElements;      // Define the units in the y-axe
        // Write the label on the y-axix
        canvas.drawText(attribute[1], w_begin-((attribute[1].length()+3)*textSize/3),h_top+actualBoolElement*value_unit-textSize/2, paint);
        canvas.drawText(attribute[2], w_begin-((attribute[2].length()+3)*textSize/3),h_top+actualBoolElement*value_unit+textSize/2, paint);
        // Write the horiziontal lines range for this criteria
        canvas.drawLine(w_begin,h_top+value_unit*actualBoolElement,w_end,h_top+value_unit*actualBoolElement,paint);
        canvas.drawLine(w_begin,h_top+value_unit*actualBoolElement-value_unit/4,w_end,h_top+value_unit*actualBoolElement-value_unit/4,paint);

        // Define type of the actual attribute to choose the correct database where is stored the respective boolean value
        boolean moodType=databaseAttribute("Moodies",attribute[0]);
        boolean positiveType=databaseAttribute("Positives",attribute[0]);
        boolean medicationType=databaseAttribute("Medications",attribute[0]);
        if (moodType&&listMood.isEmpty()) return;              // If the relevant list is empty then is nothing to do
        if (positiveType&&listPositive.isEmpty()) return;      // If the relevant list is empty then is nothing to do
        if (medicationType&&listMedication.isEmpty()) return;  // If the relevant list is empty then is nothing to do


        // Define the number of acesses on the choosen database
        int limit=listMood.size();                             // As default the limit in case of moodType
        if (positiveType) limit=listPositive.size();           // Rectify the limit in case of PositionType
        if (medicationType) limit=days;                        // Rectify the limit in case of Medication (Nr of days)

        // Intialize the coordenates of the lines
        int x=0;int y=0;int x_last=w_begin;            // Coordenates for the actual point and the next point of the graph
        long actualDay=firstDay;

        // Passing all points of the graph
        for (int i = 0; i < limit; i++) {
            // Define actual point for the Mood values
            if (moodType)    { x = xValue(w_begin, day_unit, listMood.get(i).getDate(),listMood.get(i).getTime());
                               y = yMoodValue(listMood.get(i), attribute[0]);}
            // Define actual point for the Positive Occurencies values
            if (positiveType){ x = xValue(w_begin, day_unit, listPositive.get(i).getDate(), listPositive.get(i).getTime());
                               y = yPositiveValue(listPositive.get(i), attribute[0]);}
            // Define actual point for the Medication values
            if (medicationType) { x = (i+1)*day_unit+w_begin;
                                  x_last=x-day_unit;
                                  y = yMedicationValue(attribute,actualDay);
                                  actualDay=Zeit.nextDay(actualDay);}

            // Define the a new begin if the choosen date is much different as the first register
            if (i==0 && x_last<x-day_unit) {x_last=x-day_unit;paint.setColor(Color.LTGRAY);
                                   if (big||upper) canvas.drawLine(x_last, h_top, x_last, h_down, paint);
                                   else canvas.drawLine(x_last, h_upper_down, x_last, h_down, paint);}

            // Draw the actual chart
                                                               // Set the color nuance according to the height of y_now
            paint.setColor(Color.argb(255*y,rgbColor[0],rgbColor[1],rgbColor[2]));
            canvas.drawRect(x_last,h_top+value_unit*actualBoolElement-value_unit/4,x,h_top+value_unit*actualBoolElement,paint);
            x_last=x;
        }
        // If the last x value is not the final date draw a closure line
        if (x_last<w_end) {paint.setColor(Color.LTGRAY);
                          if (big||upper) canvas.drawLine(x_last, h_top, x_last, h_down, paint);
                          else canvas.drawLine(x_last, h_upper_down, x_last, h_down, paint);}
    }
    /*                                                          E N D: drawBoolSpectrum
     =======================================================================================================================
    */

    /* =======================================================================================================================
    */

    private int yScale(String attribute){
        if (attribute==PrintPdf.moodString) return(6);
        if (attribute==PrintPdf.fearString || attribute==PrintPdf.irritabilityString || attribute==PrintPdf.stressString || attribute=="Delusion") return(3);
        if (attribute==PrintPdf.sleepQualityString) return(4);
        if (attribute==PrintPdf.sleepTimeString) return(sleepScale.length-1);
        if (attribute==PrintPdf.drinkString) return(maxDiaryVolume);
        if (attribute==PrintPdf.dietString) return(2);
        // Presume the rest are boolean attributes with 2 differente values
        return(1);
    }
    /*                                                          E N D:  yScale
     =======================================================================================================================
    */

    /* =======================================================================================================================
    */
    private int xValue(int offSet,int timeUnit, long actualDay,String timeActual){
        // Determinate position due offSet of the beginning point and the days are past since the first reference point
        int x=offSet+timeUnit*((int)(Zeit.day_passed_since_01_01_0000(actualDay) - Zeit.day_passed_since_01_01_0000(firstDay)));
        // Adjust the position of the hours today passed
        return(x+(timeUnit*Zeit.timeStamp(timeActual))/Zeit.timeStamp("24:00"));
    }
    /*                                                          E N D:  xValue
     =======================================================================================================================
    */

    /* =======================================================================================================================
        moodiesValue choose out of the input value of the Type MoodMemo the y-attribute out of ["Mood",...,"Drugs"] to paint on
        the y-value in the graph acording to the parameter "attribute"
    */
    private int yMoodValue(MoodMemo value, String attribute){
        // MoodMemo(long: date, String: time,
        //          int: mood/7-->3, fear/4, irritability/4, delusion/4, stress/4, sleepTime/24, sleepQuality/5-->2,
        //          boolean: sleepInterruptions, alcohol, drugs
        if (attribute==PrintPdf.moodString) return(3+value.getMood());                                                       // Transposition to pass the min_value -3 into 0
        if (attribute==PrintPdf.fearString) return(value.getFear());                                                         // Use the original value
        if (attribute==PrintPdf.irritabilityString) return(value.getIrritability());                                         // Use the original value
        if (attribute==PrintPdf.stressString) return(value.getStress());                                                     // Use the original value
        if (attribute==PrintPdf.sleepTimeString) return(value.getSleepTime());                                               // Use the original value
        if (attribute==PrintPdf.sleepQualityString) return(2+value.getSleepQuality());                                       // Transposition to pass the min_value -2 into 0
        if (attribute==PrintPdf.sleepInterruptionString) if (value.getSleepInterruptions()==true) return 1;else return 0;    // Transition of the boolean value to the canonical integer value
        if (attribute==PrintPdf.sleepInterruptionString) if (value.getDelusion()==true) return 1;else return 0;              // Transition of the boolean value to the canonical integer value
        if (attribute==PrintPdf.alcoholString) if (value.getAlcohol()==true) return 1;else return 0;                         // Transition of the boolean value to the canonical integer value
        if (attribute==PrintPdf.drugsString) if (value.getDrugs()==true) return 1;else return 0;                             // Transition of the boolean value to the canonical integer value
        return(0);                                                                                                           // Is there an fault return the default value 0
    }

    /*                                                          E N D:  yMoodValue
     =======================================================================================================================
    */



    /* =======================================================================================================================
    PositiveValue choose out of the input value of the Type PositiveMemo the y-attribute out of ["Diet",...,"???"] to paint on
    the y-value in the graph acording to the parameter "attribute"
    */
    private int yPositiveValue(PositiveMemo value, String attribute){
        // PositiveMemo
        if (attribute== PrintPdf.dietString) return(1+value.getDiet());       // Transposition to pass the min_value -1 into 1

        // Match the attributes with boolean values in Positive-Database into the equivalent int value
        if (attribute==PrintPdf.sportString) if (value.getSport()==true) return(1);else return(0);
        if (attribute==PrintPdf.mindfullnessString) if (value.getMindfullness()==true) return(1);else return(0);
        if (attribute==PrintPdf.journalingString) if (value.getJournaling()==true) return(1);else return(0);
        return(0);                                                           // Is there an fault return the default value 0
    }
    /*                                                          E N D:  yPositiveValue
     =======================================================================================================================
    */
    /* =======================================================================================================================
    yMedicationValue decide on a trivial integer value (bool: true=1/fales=0) if the indicated Medication in the attribute String
     was taken on the given date
    */
    private int yMedicationValue(String[] attribute,long date){
        for (int i=0;i<listMedication.size();++i){
            if (listMedication.get(i).getIdMedication()==Long.parseLong(attribute[3])&&listMedication.get(i).getDate()==date) return(1);
            }
        return(0);
    }
    /* =======================================================================================================================
                                                    E N D: yMedicationValue
    */

    /* =======================================================================================================================
    databaseAttribute checks if for a given database a specific attribute fits
    */
    private boolean databaseAttribute(String database,String attribute) {
        // Match the attributes with the equivalent in Mood-Database
        if (database == "Moodies" && ((attribute == PrintPdf.moodString) || (attribute == PrintPdf.fearString) || (attribute == PrintPdf.irritabilityString) ||
                (attribute == PrintPdf.stressString)|| (attribute == PrintPdf.delusionString) || (attribute == PrintPdf.sleepTimeString) ||
                (attribute == PrintPdf.sleepQualityString) || (attribute == PrintPdf.sleepInterruptionString) || (attribute == PrintPdf.alcoholString) ||
                (attribute == PrintPdf.drugsString))) return true;
        // Match the attributes with the equivalent in Mood-Database
        if (database=="Positives"&&((attribute==PrintPdf.dietString)||(attribute==PrintPdf.sportString)||
                (attribute==PrintPdf.mindfullnessString)||(attribute==PrintPdf.journalingString))) return true;

        if (database=="Medications"&&attribute=="Medications") return true;

        if (database==PrintPdf.drinkString&&attribute==PrintPdf.drinkString) return true;

        // No match found
        return false;
    }
    /* =======================================================================================================================
                                                    E N D: databaseAttribute
    */
}