package net.cchevalier.adnd.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * My own Artist model (Parcelable)
 *
 * Created by cch on 11/07/2015.
 */
public class MyArtist implements Parcelable {
    public String name;
    public String id;
    public Integer popularity;
    public String UrlLargeImage = null;
    public String UrlMediumImage = null;


    // Constructor based on Spotify Artist
    public MyArtist(Artist artist) {
        this.name = artist.name;
        this.id = artist.id;
        this.popularity = artist.popularity;

        // Picking image with res. closest to (200, 200)px for medium
        // Picking first image for Large
        int target = 200;
        int count = artist.images.size();
        if (count > 0) {
            int iClosest = 0;
            float distMin = 0;
            for (int i = 0; i < count ; i++) {
                int w = artist.images.get(i).width;
                int h = artist.images.get(i).height;
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
            this.UrlMediumImage = artist.images.get(iClosest).url;
            this.UrlLargeImage = artist.images.get(0).url;
        }
    }


    private float dist(int x, int y, int target) {
        return (float) Math.sqrt(Math.pow(x - target, 2) + Math.pow(y - target, 2));
    }


    // Constructor based on Parcel
    public MyArtist(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.popularity = in.readInt();
        this.UrlLargeImage = in.readString();
        this.UrlMediumImage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeInt(this.popularity);
        dest.writeString(this.UrlLargeImage);
        dest.writeString(this.UrlMediumImage);
    }


    public static final Parcelable.Creator<MyArtist> CREATOR = new Parcelable.Creator<MyArtist>() {

        @Override
        public MyArtist createFromParcel(Parcel source) {
            return new MyArtist(source);
        }

        @Override
        public MyArtist[] newArray(int size) {
            return new MyArtist[0];
        }
    };
}
