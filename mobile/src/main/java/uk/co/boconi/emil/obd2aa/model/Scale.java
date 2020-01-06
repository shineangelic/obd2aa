package uk.co.boconi.emil.obd2aa.model;

import java.util.ArrayList;

public class Scale {
    private ArrayList<ScaleItem> scaleItems;

    private float minValue;
    private float maxValue;
    private int unit;
    private float displayDivider;
    private int displayDecimals;

    public Scale(float minValue, float maxValue,
                 int unit, float displayDivider, int displayDecimals) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.unit = unit;
        this.displayDivider = displayDivider;
        this.displayDecimals = displayDecimals;
        Recalc();
    }

    private void Recalc() {
        scaleItems = new ArrayList<>();

        // automatic unit
        if (unit == 0 || true) {
            unit = 1;
            float x = maxValue - minValue;
            while (x > 10) {
                x /= 10;
                unit *= 10;
            }
        }

        if (maxValue > minValue) {
            String displayFormat = "%." + displayDecimals + "f";
            // low val
            scaleItems.add(new ScaleItem(minValue, 0,
                    String.format(displayFormat, minValue / displayDivider)));

            // center vals
            float x = (float) Math.floor(minValue / unit) * unit;
            while (x + unit <= maxValue) {
                x += unit;
                scaleItems.add(new ScaleItem(x, (x - minValue) / (maxValue - minValue),
                        String.format(displayFormat, x / displayDivider)));
            }

            // high val
            if (x < maxValue) {
                scaleItems.add(new ScaleItem(x, 1,
                        String.format(displayFormat, maxValue / displayDivider)));
            }
        } else {
            for (int x = 0; x <= 10; x++) {
                scaleItems.add(new ScaleItem(x,1 / x, "" + x));
            }
        }
    }

    public int getSize() {
        return scaleItems.size();
    }

    public ScaleItem getItem(int itemNo) {
        if (itemNo >= 0 && itemNo < scaleItems.size())
            return scaleItems.get(itemNo);

        return null;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
        Recalc();
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        Recalc();
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
        Recalc();
    }

    public float getDisplayDivider() {
        return displayDivider;
    }

    public void setDisplayDivider(float displayDivider) {
        this.displayDivider = displayDivider;
        Recalc();
    }

    public int getDisplayDecimals() {
        return displayDecimals;
    }

    public void setDisplayDecimals(int displayDecimals) {
        this.displayDecimals = displayDecimals;
        Recalc();
    }
}
