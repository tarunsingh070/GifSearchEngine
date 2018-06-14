package tarun.example.com.gifsearchengine.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;

/**
 * Data Access Object interface containing the methods used for accessing the local room database data.
 */
@Dao
public interface GifDao {

    @Query(Constants.QUERY_GET_RATED_GIF_BY_ID)
    UserRatedGif getRatedGifByIdAsync(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRatedGif(UserRatedGif ratedGif);

    @Update
    void updateRatedGif(UserRatedGif ratedGif);

}
