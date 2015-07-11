package net.cchevalier.adnd.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by cch on 11/07/2015.
 */
public class MyArtist implements Parcelable {
    public String name;
    public String id;
    public Integer popularity;
    public String imageLargeUrl = null;
    // String imageMediumUrl;


    public MyArtist(Artist artist) {
        this.name = artist.name;
        this.id = artist.id;
        this.popularity = artist.popularity;
        if (artist.images.size() > 0) {
            this.imageLargeUrl = artist.images.get(0).url;
        }
    }


    public MyArtist(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.popularity = in.readInt();
        this.imageLargeUrl = in.readString();
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
        dest.writeString(this.imageLargeUrl);
    }


    public final Parcelable.Creator<MyArtist> CREATOR = new Parcelable.Creator<MyArtist>() {

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
