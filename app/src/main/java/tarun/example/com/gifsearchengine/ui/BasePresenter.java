package tarun.example.com.gifsearchengine.ui;

/**
 * The base class for all presenters of different modules containing the absolute basic methods that
 * every presenter must implement.
 */
public interface BasePresenter<V> {

    void takeView(V v);

    void dropView();

}
