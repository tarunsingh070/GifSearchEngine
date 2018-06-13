package tarun.example.com.gifsearchengine.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class with common http related utility methods.
 */

public class HttpUtil {

    private static final String TAG = HttpUtil.class.getSimpleName();

    /**
     * Checks the internet connectivity status of user's device.
     * @return True if connected, False otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

}
