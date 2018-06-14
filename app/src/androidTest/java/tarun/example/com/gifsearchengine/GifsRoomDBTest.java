package tarun.example.com.gifsearchengine;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import tarun.example.com.gifsearchengine.data.local.GifDao;
import tarun.example.com.gifsearchengine.data.local.RatedGifsDatabase;
import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Class containing Instrumental tests for testing the Room database operations.
 */
@RunWith(AndroidJUnit4.class)
public class GifsRoomDBTest {

    private GifDao gifDao;
    private RatedGifsDatabase database;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, RatedGifsDatabase.class).build();
        gifDao = database.gifDao();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void insertNewRatedGifAndVerify() {
        // Insert a rated gif item in db.
        UserRatedGif userRatedGif = new UserRatedGif("Fdy1pYtlhotclXBHdf", 3);
        gifDao.insertRatedGif(userRatedGif);

        // Fetch the newly added gif item from db and verify if correctly written.
        UserRatedGif byId = gifDao.getRatedGifByIdAsync("Fdy1pYtlhotclXBHdf");
        assertThat(byId, equalTo(userRatedGif));
    }

    @Test
    public void updateRatedGifAndVerify() {
        // Insert a rated gif item in db and read it to verify written values.
        UserRatedGif userRatedGif = new UserRatedGif("Fdy1pYtlhotclXBHdf", 3);
        gifDao.insertRatedGif(userRatedGif);
        UserRatedGif byId = gifDao.getRatedGifByIdAsync("Fdy1pYtlhotclXBHdf");
        assertThat(byId, equalTo(userRatedGif));

        // Update the newly updated rated gif item in db and read it to verify the updated written values.
        UserRatedGif updatedUserRatedGif = new UserRatedGif("Fdy1pYtlhotclXBHdf", 5);
        gifDao.updateRatedGif(updatedUserRatedGif);

        UserRatedGif updatedGifReadById = gifDao.getRatedGifByIdAsync("Fdy1pYtlhotclXBHdf");
        assertEquals(updatedGifReadById.getGifId(), userRatedGif.getGifId());
        assertEquals(updatedGifReadById.getRatingGiven(), 5);
    }
}