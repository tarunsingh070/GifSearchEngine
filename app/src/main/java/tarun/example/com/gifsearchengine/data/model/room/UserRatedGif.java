package tarun.example.com.gifsearchengine.data.model.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Objects;

import tarun.example.com.gifsearchengine.data.Constants;

/**
 * Model class for storing the information about a gif that user rated to be stored in local room db.
 * The entity annotation represents the name of table containing {@link UserRatedGif} objects within the database.
 */
@Entity(tableName = Constants.GIFS_TABLE_NAME)
public class UserRatedGif {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "gif_id")
    private String gifId;

    @ColumnInfo(name = "rating_given")
    private int ratingGiven;

    public UserRatedGif(@NonNull String gifId, int ratingGiven) {
        this.gifId = gifId;
        this.ratingGiven = ratingGiven;
    }

    @NonNull
    public String getGifId() {
        return gifId;
    }

    public void setGifId(@NonNull String gifId) {
        this.gifId = gifId;
    }

    public int getRatingGiven() {
        return ratingGiven;
    }

    public void setRatingGiven(int ratingGiven) {
        this.ratingGiven = ratingGiven;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gifId, ratingGiven);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserRatedGif) {
            UserRatedGif gif = (UserRatedGif) obj;
            return Objects.equals(gifId, gif.gifId) && Objects.equals(ratingGiven, gif.ratingGiven);
        }
        return false;
    }
}
