package uk.co.boconi.emil.obd2aa.util;

public class UnitConversionUtil {

    public static float ConvertValue(float val, String unit) {
        if (unit.equalsIgnoreCase("km/h"))
            val = (float) (val / 1.60);
        else if (unit.equalsIgnoreCase("km"))
            val = (float) (val / 1.60);
        else if (unit.equalsIgnoreCase("l"))
            val = (float) (val * 0.219969);
        else if (unit.equalsIgnoreCase("°C"))
            val = (float) ((val * 1.8) + 32);
        else if (unit.equalsIgnoreCase("Kg"))
            val = (float) (val * 2.20462);
        else if (unit.equalsIgnoreCase("m"))
            val = (float) (val * 3.28084);
        else if (unit.equalsIgnoreCase("psi"))
            val = (float) (val * 0.0689476);
        else if (unit.equalsIgnoreCase("ft-lb"))
            val = (float) (val * 1.35581795);
        else if (unit.equalsIgnoreCase("kPa"))
            val = (float) (val * 0.145038);
        else if (unit.equalsIgnoreCase("l/hr"))
            val = (float) (val * 0.219969);
        return val;
    }

}
