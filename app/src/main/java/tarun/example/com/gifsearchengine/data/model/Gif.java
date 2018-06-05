package tarun.example.com.gifsearchengine.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for storing all information related to a gif object.
 */
public class Gif {

    private String id;

    @SerializedName("username")
    private String userName;

    @SerializedName("import_datetime")
    private String importDate;

    private String title;

    private Images images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}
