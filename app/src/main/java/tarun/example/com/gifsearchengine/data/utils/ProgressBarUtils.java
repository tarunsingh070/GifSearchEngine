package tarun.example.com.gifsearchengine.data.utils;

import android.content.Context;
import android.support.v4.widget.CircularProgressDrawable;

public class ProgressBarUtils {

    /**
     * A utility method that returns a circular placeholder progress bar.
     */
    public static CircularProgressDrawable getCircularProgressPlaceholder(Context context) {
        CircularProgressDrawable progressPlaceHolder = new CircularProgressDrawable(context);
        progressPlaceHolder.setStrokeWidth(5f);
        progressPlaceHolder.setCenterRadius(30f);
        progressPlaceHolder.start();
        return progressPlaceHolder;
    }

}
