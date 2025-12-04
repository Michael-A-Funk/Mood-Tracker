package de.psychology.mooddiary;

public class DrinksMemo {

    private String drinks;
    private String quantity;
    private long id;
    private boolean checked;
    private String time;
    private long date;


    public DrinksMemo(String drinks, String quantity, long id, String time, long date, boolean checked) {
        this.drinks = drinks;
        this.quantity = quantity;
        this.id = id;
        this.checked = checked;
        this.time = time;
        this.date = date;
    }


    public String getDrinks() {
        return drinks;
    }

    public void setDrinks(String drinks) {
        this.drinks = drinks;
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


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
        String output = quantity + " ml " + drinks + " at " + time + " on " + Zeit.dateFormat(date);

        return output;
    }
}