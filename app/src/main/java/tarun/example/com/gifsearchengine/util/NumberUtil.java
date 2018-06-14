package tarun.example.com.gifsearchengine.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * A utility class for all number manipulation related utility methods.
 */
public class NumberUtil {

    /**
     * Round the number to one decimal place.
     */
    public static float getRoundedToOneDecimalPlace(float number) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return Float.valueOf(df.format(number));
    }

}
