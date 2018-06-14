package tarun.example.com.gifsearchengine.data;

/**
 * An interface for storing the various constants that maybe needed throughout the app.
 */
public interface Constants {

    // Firebase constants.
    String PATH_GIFS = "gifs";

    // Giphy Rest Client related constants.
    int LOADING_PAGE_SIZE = 30;

    /* Room constants */
    // Room DB info
    String DATABASE_NAME = "rated-gifs-database";
    String GIFS_TABLE_NAME = "user_rated_gifs";
    int DATABASE_VERSION = 1;

    // Room Queries
    String QUERY_GET_RATED_GIF_BY_ID = "SELECT * FROM user_rated_gifs where gif_id = :id";

}
