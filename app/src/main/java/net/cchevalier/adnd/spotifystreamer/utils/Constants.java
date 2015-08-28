package net.cchevalier.adnd.spotifystreamer.utils;

/**
 * Created by cch on 27/08/2015.
 */
public class Constants {

    /*
    From http://developer.android.com/guide/components/intents-filters.html:
    If you define your own actions, be sure to include your app's package name as a prefix. For example:
    static final String ACTION_TIMETRAVEL = "com.example.action.TIMETRAVEL";
    */

    // Intents ACTION "net.cchevalier.adnd.spotifystreamer"
    public final static String ACTION_SHOW = "net.cchevalier.adnd.spotifystreamer.ACTION_SHOW";
    public final static String ACTION_START = "net.cchevalier.adnd.spotifystreamer.ACTION_START";

    //  Intents EXTRA
    public final static String EXTRA_ARTIST = "EXTRA_ARTIST";
    public final static String EXTRA_TRACKS = "EXTRA_TRACKS";
    public final static String EXTRA_TRACK_NB = "EXTRA_TRACK_NB";

    // Feedback ACTION messages
    public final static String PLAY_COMPLETED = "PLAY_COMPLETED";

    public static final String KEY_SEARCH_STRING = "KEY_SEARCH_STRING";

    public static final String KEY_TABLET = "KEY_UI_TABLET";

}
