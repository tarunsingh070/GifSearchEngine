package tarun.example.com.gifsearchengine.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * A utility class for all keyboard related utility methods.
 */
public class KeyboardUtil {

    /**
     * Utility method to explicitly hide soft keyboard.
     */
    public static void hideKeyboard(Context context, Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }
}
