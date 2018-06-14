package tarun.example.com.gifsearchengine.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;

/**
 * This class represents the Room Database and serves as the main access point for the underlying connection to the app's local data.
 */
@Database(entities = {UserRatedGif.class}, version = Constants.DATABASE_VERSION, exportSchema = false)
public abstract class RatedGifsDatabase extends RoomDatabase {

    private static RatedGifsDatabase databaseInstance;

    public abstract GifDao gifDao();

    /**
     * Return the singleton instance of the {@link RatedGifsDatabase} database class.
     * @param context Context used to build the instance of database.
     * @return The instance of {@link RatedGifsDatabase} database.
     */
    public static RatedGifsDatabase getDatabase(final Context context) {
        if (databaseInstance == null) {
            synchronized (RatedGifsDatabase.class) {
                if (databaseInstance == null) {
                    databaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            RatedGifsDatabase.class, Constants.DATABASE_NAME)
                            .build();

                }
            }
        }
        return databaseInstance;
    }

}
