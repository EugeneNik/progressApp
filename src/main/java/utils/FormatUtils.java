package utils;

import common.property.PropertyManager;
import common.property.PropertyNamespace;

/**
 * Created by Евгений on 28.06.2015.
 */
public class FormatUtils {

    public static String getProperDoubleFormat(boolean isPercentage) {
        int digits = PropertyManager.getValue(PropertyNamespace.DIGITS_AFTER_POINTS);
        StringBuilder sb = new StringBuilder("%.").append(digits).append("f").append(isPercentage ? "%%" : "");
        return sb.toString();
    }

    public static String getProperDoubleFormatForProgressBars() {
        int digits = PropertyManager.getValue(PropertyNamespace.DIGITS_AFTER_POINTS);
        StringBuilder sb = new StringBuilder("#0.");
        for (int i = 0; i < digits; i++) {
            sb.append("0");
        }
        return sb.toString();
    }
}
