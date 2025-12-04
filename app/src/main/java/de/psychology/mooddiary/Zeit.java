package de.psychology.mooddiary;
/*
In this package we will use the date and (day-)time in a consistent way for our purpose
 */

import java.text.SimpleDateFormat;
import java.util.Date;


public class Zeit {
    //We will transform the date received by new Date().getTime () into the formattype ""yyyyMMdd", this means:
    // 4 numbers for the year (yyyy), 2 numbers for the month (MM) and then 2 numbers for the day (dd)
    // In the database we will put the following format:
    //    long date = Long.parseLong(sdfDate.format(new Date().getTime ()));
    private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");

    //We will transform the time received by new Date().getTime () into the formattype ""HH:mm", this means:
    // 2 numbers for the hour (HH), them 2 numbers for the minutes (mm)
    // In the database we will put the following format:
    //      String newTime = sdf.format(new Date());
    private static SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");


    public static String dateFormat(long date){
        //year=date/10000;
        // month=(date%10000)/100;
        // day=date%100
        return(Long.toString(date%100)+"/"+Long.toString((date%10000)/100)+"/"+Long.toString(date/10000));
    }

    /*  =============================================
        Return the actual date in the sdFDate pattern
     */
    public static long today(){
        long date=Integer.parseInt(sdfDate.format(new Date()));
        return (date);
    }
    // ==============================================


    /*  ==========================================================
        Return the actual hour of this day in the sdFTime pattern
     */
    public static String now(){
        return (sdfTime.format(new Date()));
    }
    // ============================================================


    /*  ========================================================================================================
        Convert the StringPattern "hh:mm" into a integer value between 0 and 2400 for comparing to the next time
     */
    public static int timeStamp(String time) {
        int stamp = 0;
        for (int i = 0; i < 5; ++i) {
            if (time.charAt(i) != ':') {
                stamp = stamp * 10 + (int) time.charAt(i)-48;
            }
        }
        return (stamp);
    }
    // ========================================================================================================

    /*  ========================================================================================================
        Convert the integer hourPattern "hhmm" getted by timeStamp into a integer value between 0 und 86,400,000
        milisec for comparing to the next time
     */
    public static long timeDayMillisec(long time)
    { long milli = ((time%100)*60+3600*(time/100))*1000;
        return(milli);
    }
    // ========================================================================================================

    /*  ========================================================================================================
        How many days passesd between 1.Janary on the year 0 since Christ and the day of "date" in the sdfDate pattern
        Dates are considered given in the format sdfDate
     */
    public static long day_passed_since_01_01_0000(long date){
        long year=date/10000;
        long month=(date%10000)/100;
        long day=date%100;
        long passed=year*365+day-1;                 // This is valid for all dates after 01-01-0000 for a normal year
                                                    // including the actual day, but not the month
        if (year>0) passed+=(year-1)/4+1;           // After the year 0000 a.C.we have year/4+1 times years with 366 days
                                                    // only until the last year, because the actual year may be before 29th of Feb
        passed-=year/400;                           // All 400 years the 366th day not happens
        passed+=(month-1)*30;                       // The simple 30 day rule for each month already passed for the actual year
        if (month>2 && year%4==0) passed+=1;        // The actual special year has even had it supplementary day
        // Now we have to adapt the simpel 30 day rule for each month
        // for the month Jan, Abr e May the rule is right
        if (month==3) passed-=1; // for the month Mar we have subtract the lack of Feb
        if (month==2||month==6||month==7) passed+=1;// for the mont Feb, Jun, Jul we have one day more
        if (month>7) passed+=1;                     // All after Jul have one more
        if (month>8) passed+=1;                     // All after Aug have one more
        if (month>10) passed+=1;                    // All after Oct have one more
        return(passed);
    }
    // ========================================================================================================

    /*  ========================================================================================================
        Give for an day after 1.Janary on the year 0 since Christ with "date" in the sdfDate pattern the next
        day in the same pattern
    */
    public static long nextDay(long date){
        //(88,5% of the casses are resolved in the first line)
        if ((date%100)<28) return(date+1);              // Normal succession in the first 27 day of any month

        long day=date%100;                              // Define the date day
        long month=(date%10000)/100;                    // Define the date month

        if (month!=2) {                                 // In all month except February we have:
            //(more 3% on this line)
            if (day < 30)                                                 // a) In all days unter 30
                return (date + 1);                                             // a normal succession
            // Implicity is used that there is no incorrect date given!!!
            // (more 1,09% on this line)
            if (month == 4 || month == 6 || month == 9 || month == 11)    // b) In the month Abr,Jun,Sep e Nov we 30 days
                return (date + 71);                                            // so now rest to fill the next hundred plus one by +71
            // (more 3% on this line)
            if (day == 30)                                                // c)The restant month have 31 days where
                return (date + 1);                                             // 30 days have a normal succession

            if (month == 12)                                              // d) In the cases with 31 days the Dec is a exception by passing
                return (date + 8870);                                          // into a new year  (+8870 in this format)
            return (date + 70);                                           // e) In the rest cases is reached the next month (hundred plus one)
                                                                               // we reach with +70 on the base of 31
            }
        // In the case of February (the only month we not handled) we have to distinque betwee Years of 365 or 366 days
        long year=date/10000;                            // Define the date year

        if (year%4!=0) return (date+73);                 // In the year where 4 is no divisor we have 365 days and so we get to the next
                                                         //  month (the next hundred plus one) by +73
         // Having a year of 366 days we have to know if we still on day 28 or at the day 29
        if (day==28) return (date+1);                   // with 28 we have a normal succession (+1)
        return(date+72);                                // or going in the first day of the next month by +72
    }

    /*  ========================================================================================================
        Give the exact date "days" times later than the inicial date "first"
    */
    public static long later(long first,int days){
        for (int i=0;i<days;++i) first=nextDay(first);   // sucessive incrementation
        return(first);                                   // return the final date
    }


    /*  ========================================================================================================
        Count the days it needs to the next month
    */
    public static long daysToNextMonth(long date) {
        int day = (int) (date % 100);
        int month = (int) ((date % 10000) / 100);
        if (month == 4 || month == 6 || month == 9 || month == 11) // We need in Month with 30 days (Abr,Jun,Sep,Nov)
            return (31 - day);                   // waiting this number of days

        if (month == 2)                                // In February we have to distingue between years with
            if ((date / 10000) % 4 == 0)                    // with 366 days
                return (30 - day);                    // where the number in one higher
            else return (29 - day);                      // then in years of 365 days

        return (32 - day);                              // In the restant month with 31 days we wait 32-day times
    }
    // ========================================================================================================


    // ========================================================================================================


    /*  ========================================================================================================
        ????????????????????????????????????????????????????????????????????????????????????????????????????????
        Determine the milliseconds which will pass from now to "time" with the date of today and than add the milliseconds
        passed between now and 1.1.1970 [function: System.currentTimeMillis()]
        If it will be on an other day we may have to consider the following
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
        long date = Long.parseLong(sdfDate.format(new Date().getTime ()));

     */
    public static long millisec_since_1970_until_today_hour(long time)
        {
        long milli = 100000 * timeStamp(sdfTime.format(new Date()))-time+System.currentTimeMillis();
        return(milli);
        }
    // ========================================================================================================


    /*  ========================================================================================================
        Check it the given string "time" is formally correct in the pattern sdfTime = new SimpleDateFormat("HH:mm")
        Return give the boolean answer if it is conform or not
     */
    public static boolean checkTime (String time) {
        if (time.length() != 5) return (false);     // need to have formally 5 characters to be conform
        if (time.charAt(2) !=':') return (false); // need have on third position the character ":"
        // the first digit d has to be 0<=d<3
        if (time.charAt(0)> 50 || time.charAt(0) < 48) return (false);
        // the first digit d has to be 0<=d<=9
        if (time.charAt(1) > 57 || time.charAt(1) < 48) return (false);
        // if the first digit was 2 then the second digit d has to be 0<=d<4
        if (time.charAt(0) == 50 && time.charAt(1) > 51) return (false);
        // the third digit d has to be 0<=d<6
        if (time.charAt(3) > 53 || time.charAt(3) < 48) return (false);
        // the fourth digit d has to be 0<=d<=9
        if (time.charAt(4)> 57 || time.charAt(4 ) < 48) return (false);
        return (true);
    }
}
