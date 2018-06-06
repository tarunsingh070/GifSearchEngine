package tarun.example.com.gifsearchengine.data;

/**
 * An interface for storing the various constants that maybe needed throughout the app.
 *
 * Some constants (eg activity titles) which are needed to be accessed from the presenter classes are
 * also declared here instead of strings.xml so as to avoid creating a dependency of presenters on
 * Android framework classes ({@link android.content.Context} in this case).
 * Having said that, find a better way for this.
 */
public interface Constants {

    // Firebase constants.
    String PATH_GIFS = "gifs";

    // Activity Title Constants
    String ACTIVITY_TITLE_APP_NAME = "GifSearchEngine";
    String ACTIVITY_TITLE_TRENDING = "Trending";
    String ACTIVITY_TITLE_RESULTS = "Results";

}
