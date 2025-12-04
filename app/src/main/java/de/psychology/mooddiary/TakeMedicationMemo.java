package de.psychology.mooddiary;

public class TakeMedicationMemo {

    private long idMedication;
    private long id;
    private boolean checked;
    private String time;
    private long date;


    public TakeMedicationMemo(long idMedication, long id, String time, long date, boolean checked) {
        this.idMedication = idMedication;
        this.id = id;
        this.checked = checked;
        this.time = time;
        this.date = date;
    }


    public long getIdMedication() {
        return idMedication;
    }

    public void setIdMedication(long idMedication) {
        this.idMedication = idMedication;
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

        String output = idMedication + " at " + time + " on " + date;

        return output;
    }
}