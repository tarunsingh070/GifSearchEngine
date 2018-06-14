package android.text;

import android.support.annotation.Nullable;

/**
 * Mock class for mocking Android's TextUtils class apis.
 */
public class TextUtils {

    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

}
