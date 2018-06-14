package tarun.example.com.gifsearchengine;

import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.giphy.FullGif;

public class GifTestUtil {

    /**
     * This method creates an instance of type {@link AdapterGifItem} with test data and returns it.
     * @return The test data instance of {@link AdapterGifItem}.
     */
    public static AdapterGifItem getTestAdapterGifItem() {
        FullGif fullGif = new FullGif("https://media2.giphy.com/media/Fdy1pYtlhotclXBHdf/200w.gif", "200", "250", "405060");
        return new AdapterGifItem("Fdy1pYtlhotclXBHdf", "Oksana Smilska", "2018-06-07 00:28:19"
                , "Mimimi !", "https://media2.giphy.com/media/Fdy1pYtlhotclXBHdf/200w_d.gif", fullGif);
    }

}
