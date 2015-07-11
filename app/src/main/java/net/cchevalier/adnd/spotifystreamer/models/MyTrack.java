package net.cchevalier.adnd.spotifystreamer.models;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by cch on 11/07/2015.
 */
public class MyTrack {
    public String name;
    public String album;
    public Integer popularity;
    public String imageLargeUrl = null;


    public MyTrack(Track track) {
        this.name = track.name;
        this.album = track.album.name;
        this.popularity = track.popularity;
        if (track.album.images.size() > 0) {
            this.imageLargeUrl = track.album.images.get(0).url;
        }
    }

}
