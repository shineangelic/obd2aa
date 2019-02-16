package uk.co.boconi.emil.obd2aa;

/**
 * Created by Emil on 19/09/2017.
 */
public class ItemData {

    private String text;
    private int imageId;
    private int gaugeNumber;

    public ItemData(String text, Integer imageId, int gaugeNumber) {
        this.text = text;
        if (imageId != null)
            this.imageId = imageId;
        this.gaugeNumber = gaugeNumber;
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

