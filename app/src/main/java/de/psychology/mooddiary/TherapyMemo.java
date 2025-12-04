package de.psychology.mooddiary;

public class TherapyMemo {

    private String medication;
    private String dosis;
    private long id;
    private boolean checked;
    private String planTime;


    public TherapyMemo(String medication, String dosis, long id, String planTime, boolean checked) {
        this.medication = medication;
        this.dosis = dosis;
        this.id = id;
        this.checked = checked;
        this.planTime = planTime;
    }


    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }


    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String dosis) {
        this.planTime = planTime;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }


    @Override
    public String toString() {
        String output = dosis + " x " + medication + " at " + planTime;

        return output;
    }
}