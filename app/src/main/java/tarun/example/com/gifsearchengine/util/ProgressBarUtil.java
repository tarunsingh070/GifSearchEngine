package tarun.example.com.gifsearchengine.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.View;
import android.widget.ProgressBar;

import tarun.example.com.gifsearchengine.R;

/**
 * A utility class for all progress bar related utility methods.
 */
public class ProgressBarUtil {

    /**
     * A utility method that returns a white circular placeholder progress bar.
     */
    public static CircularProgressDrawable getCircularProgressPlaceholder(Context context) {
        CircularProgressDrawable progressPlaceHolder = new CircularProgressDrawable(context);
        progressPlaceHolder.setStrokeWidth(5f);
        progressPlaceHolder.setCenterRadius(30f);
        progressPlaceHolder.setColorSchemeColors(Color.WHITE);
        progressPlaceHolder.start();
        return progressPlaceHolder;
    }

    /**
     * Show the cyclic animation progress bar in centre of screen.
     */
    public static void showProgressBarCenter(Activity activity) {
        final View progressBar = activity.findViewById(R.id.progress_layout_center);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide the cyclic animation progress bar from centre of screen.
     */
    public static void hideProgressBarCenter(Activity activity) {
        final View progressBar = activity.findViewById(R.id.progress_layout_center);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Show the cyclic animation progress bar at the bottom of screen.
     */
    public static void showProgressBarBottom(Activity activity) {
        final ProgressBar progressBar = activity.findViewById(R.id.progress_bar_bottom);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide the cyclic animation progress bar from the bottom of screen.
     */
    public static void hideProgressBarBottom(Activity activity) {
        final ProgressBar progressBar = activity.findViewById(R.id.progress_bar_bottom);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

}
