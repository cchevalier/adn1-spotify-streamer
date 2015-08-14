package net.cchevalier.adnd.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * My own Track model (Parcelable)
 *
 * Created by cch on 11/07/2015.
 */
public class MyTrack implements Parcelable {

    public String name;
    public String album;
    public Integer popularity;
    public String UrlLargeImage = null;
    public String UrlMediumImage = null;
    public String preview_url = null;

    // Constructor based on Spotify Track
    public MyTrack(Track track) {
        this.name = track.name;
        this.album = track.album.name;
        this.popularity = track.popularity;
        this.preview_url = track.preview_url;

        // Picking image with res. closest to (200, 200)px for medium
        // Picking first image for Large
        int target = 200;
        int count = track.album.images.size();
        if (count > 0) {
            int iClosest = 0;
            float distMin = 0;
            for (int i = 0; i < count ; i++) {
                int w = track.album.images.get(i).width;
                int h = track.album.images.get(i).height;
                float dist = dist(w, h, target);
                if (i > 0) {
                    if (dist < distMin) {
                        distMin = dist;
                        iClosest = i;
                    }
                } else {
                    distMin = dist;
                }
            }
            this.UrlMediumImage = track.album.images.get(iClosest).url;
            this.UrlLargeImage = track.album.images.get(0).url;
        }
    }


    private float dist(int x, int y, int target) {
        return (float) Math.sqrt(Math.pow(x - target, 2) + Math.pow(y - target, 2));
    }


    // Constructor based on Parcel
    public MyTrack(Parcel in) {
        this.name = in.readString();
        this.album = in.readString();
        this.popularity = in.readInt();
        this.UrlLargeImage = in.readString();
        this.UrlMediumImage = in.readString();
        this.preview_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.album);
        dest.writeInt(this.popularity);
        dest.writeString(this.UrlLargeImage);
        dest.writeString(this.UrlMediumImage);
        dest.writeString(this.preview_url);
    }

    public static final Parcelable.Creator<MyTrack> CREATOR = new Parcelable.Creator<MyTrack>() {

        @Override
        public MyTrack createFromParcel(Parcel source) {
            return new MyTrack(source);
        }

        @Override
        public MyTrack[] newArray(int size) {
            return new MyTrack[0];
        }
    };

}
