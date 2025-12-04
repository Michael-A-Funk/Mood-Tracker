package de.psychology.mooddiary;

public class MoodMemo {


    private long id;
    private String time;
    private long date;
    private int mood;
    private int fear;
    private int irritability;
    private boolean delusion;
    private int stress;
    private int sleepTime;
    private int sleepQuality;
    private boolean sleepInterruptions;
    private boolean alcohol;
    private boolean drugs;
    private String memo;
    private boolean checked;



    public MoodMemo(long id, long date, String time, int mood, int fear, int irritability, boolean delusion, int stress, int sleepTime, int sleepQuality, boolean sleepInterruptions,
                    boolean alcohol, boolean drugs, String memo, boolean checked) {

        this.id = id;
        this.checked = checked;
        this.time = time;
        this.date = date;
        this.mood= mood;
        this.fear = fear;
        this.irritability = irritability;
        this.delusion = delusion;
        this.stress = stress;
        this.sleepTime = sleepTime;
        this.sleepQuality = sleepQuality;
        this.sleepInterruptions = sleepInterruptions;
        this.alcohol = alcohol;
        this.drugs = drugs;
        this.memo = memo;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int drinks) {
        this.mood = mood;
    }

    public int getFear() {
        return fear;
    }

    public void setFear(int fear) { this.fear = fear; }

    public int getIrritability() {
        return irritability;
    }

    public void setIrritability(int irritability) { this.irritability = irritability; }

    public boolean getDelusion() {
        return delusion;
    }

    public void setDelusion(boolean delusion) { this.delusion = delusion; }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) { this.stress = stress; }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) { this.sleepTime = sleepTime; }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) { this.sleepQuality = sleepQuality; }

    public boolean getSleepInterruptions() {return sleepInterruptions;}

    public void setSleepInterruptions (boolean sleepInterruptions) { this.sleepInterruptions = sleepInterruptions; }

    public boolean getAlcohol() {return alcohol;}

    public void setAlcohol(boolean alcohol) { this.alcohol = alcohol; }

    public boolean getDrugs() {
        return drugs;
    }

    public void setDrugs(boolean drugs) { this.drugs = drugs; }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    // Insert here MISSING FIELDS

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }


    @Override
   public String toString() {

        String output = date + " " + time + " " + mood + " " + fear + " " + irritability + " " + delusion + " " + stress + " " + sleepTime + sleepQuality + " " + sleepInterruptions
                + " " + alcohol + " " + drugs + " " + memo;
        return output;
    }
}