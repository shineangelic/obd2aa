package uk.co.boconi.emil.obd2aa.model;

import org.json.JSONObject;

public class PidList {

    public String longName;
    public String shortName;
    public String unit;
    public String maxValue;
    public String minValue;
    public String scale;
    public String pid;

    public String scaleUnit;
    public String scaleDivider;
    public String scaleDecimals;


    public PidList(String info) {
        String[] pidinfo = info.split(",");
        this.longName = pidinfo[0];
        this.shortName = pidinfo[1];
        this.unit = pidinfo[2];
        this.maxValue = pidinfo[3];
        this.minValue = pidinfo[4];
        this.scale = pidinfo[5];
        this.pid = pidinfo[6] + "," + pidinfo[7];

        this.scaleUnit = "1";
        this.scaleDivider = "1";
        this.scaleDecimals = "0";
    }

    public PidList(JSONObject data) {
        try {
            this.longName = data.getString("longName");
            this.shortName = data.getString("shortName");
            this.unit = data.getString("unit");
            this.maxValue = data.getString("maxValue");
            this.minValue = data.getString("minValue");
            this.scale = data.getString("scale");
            this.pid = data.getString("pid");

            this.scaleUnit = "1";
            this.scaleDivider = "1";
            this.scaleDecimals = "0";
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return this.longName.toString();
    }

    public String getPidName() {
        return this.longName;
    }

    public String getShortPidName() { return this.shortName; }

    public String getUnit() {
        return this.unit;
    }

    public String getMaxValue() {
        return this.maxValue;
    }

    public String getMinValue() {
        return this.minValue;
    }

    public String getScale() {
        return this.scale;
    }

    public String getPid() {
        return this.pid;
    }

    public String getScaleUnit() { return this.scaleUnit; }

    public String getScaleDivider() {
        return this.scaleDivider;
    }

    public String getScaleDecimals() { return this.scaleDecimals; }
}
