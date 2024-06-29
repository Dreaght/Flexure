package org.dreaght.flexure.util;

import java.util.Arrays;

public class ValidatorUtil {
    public static boolean isNumber(String str) {
        try {
            double v = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }

    public static boolean isNumbers(String... numbers) {
        return Arrays.stream(numbers).allMatch(ValidatorUtil::isNumber);
    }
}
