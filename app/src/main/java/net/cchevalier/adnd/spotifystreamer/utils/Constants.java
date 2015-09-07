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

    // PlayerService
    //
    // Intents ACTION "net.cchevalier.adnd.spotifystreamer"
    public final static String ACTION_CONNECT = "net.cchevalier.adnd.spotifystreamer.ACTION_CONNECT";
    public final static String ACTION_START = "net.cchevalier.adnd.spotifystreamer.ACTION_START";
    public final static String ACTION_DISPLAY_PLAYER = "net.cchevalier.adnd.spotifystreamer.ACTION_DISPLAY_PLAYER";
    public final static String ACTION_PLAY_PREVIOUS = "net.cchevalier.adnd.spotifystreamer.ACTION_PLAY_PREVIOUS";
    public final static String ACTION_PLAY_NEXT = "net.cchevalier.adnd.spotifystreamer.ACTION_PLAY_NEXT";
    public final static String ACTION_PLAY_PAUSE = "net.cchevalier.adnd.spotifystreamer.ACTION_PLAY_PAUSE";


    // Feedback ACTION messages
    public final static String PS_LAST_TRACK_COMPLETED = "PS_LAST_TRACK_COMPLETED";
    public final static String PS_NEW_TRACK_STARTED = "PS_NEW_TRACK_STARTED";
    public final static String PS_TRACK_PAUSE = "PS_TRACK_PAUSE";
    public final static String PS_TRACK_RESUME = "PS_TRACK_RESUME";
    public final static String PS_START = "PS_START";


    //  Intents EXTRA
    public static final String EXTRA_IS_TABLET = "EXTRA_IS_TABLET";
    public static final String EXTRA_SEARCH_STRING = "EXTRA_SEARCH_STRING";

    public final static String EXTRA_ARTIST = "EXTRA_ARTIST";
    public final static String EXTRA_TRACKS = "EXTRA_TRACKS";
    public final static String EXTRA_TRACK_NB = "EXTRA_TRACK_NB";



}
