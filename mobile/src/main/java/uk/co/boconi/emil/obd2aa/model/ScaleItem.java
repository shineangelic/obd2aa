package uk.co.boconi.emil.obd2aa.model;

public class ScaleItem {

    private float value;
    private float perc;
    private String display;

    public ScaleItem(float value, float perc, String display) {
        this.value = value;
        this.perc = perc;
        this.display = display;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getPerc() {
        return perc;
    }

    public void setPerc(float perc) {
        this.perc = perc;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
