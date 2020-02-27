package uk.co.boconi.emil.obd2aa.model;

public class ItemData {

    private String text;
    private int imageId;
    private int gaugeNumber;

    public ItemData(String text, Integer imageId, int gaugeNumber) {
        this.text = text;
        this.gaugeNumber = gaugeNumber;

        if (imageId != null) {
            this.imageId = imageId;
        }
    }

    public String getText() {
        return text;
    }

    public int getImageId() {
        return imageId;
    }

    public int getGaugeNumber() {
        return gaugeNumber;
    }

}

