package de.psychology.mooddiary;

public class PositiveMemo {

    private long id;
    private boolean checked;
    private String time;
    private long date;
    private boolean sport;
    private int diet;
    private boolean mindfullness;
    private boolean journaling;

    public PositiveMemo(long id, long date, String time, boolean sport, int diet, boolean mindfullness, boolean journaling, boolean checked) {

        this.id = id;
        this.date = date;
        this.time = time;
        this.sport = sport;
        this.diet = diet;
        this.mindfullness = mindfullness;
        this.journaling = journaling;
        this.checked = checked;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public boolean getSport() {
        return sport;
    }

    public void setSport(boolean sport) {
        this.sport = sport;
    }

    public int getDiet() {
        return diet;
    }

    public void setDiet(int diet) {
        this.diet = diet;
    }

    public boolean getMindfullness() {
        return mindfullness;
    }

    public void setMindfullness(boolean mindfullness) {
        this.mindfullness = mindfullness;
    }

    public boolean getJournaling() {
        return journaling;
    }

    public void setJournaling (boolean journaling) {
        this.journaling = journaling;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }


    @Override
    public String toString() {

        String output = date + " " + time + " " + sport + " " + diet + " " + mindfullness + " " + journaling;

        return output;
    }
}