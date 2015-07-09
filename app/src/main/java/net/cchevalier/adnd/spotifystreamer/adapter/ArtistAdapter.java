package net.cchevalier.adnd.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.cchevalier.adnd.spotifystreamer.R;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by cch on 09/07/2015.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    private LayoutInflater mLayoutInflater;

    public ArtistAdapter(Context context, List<Artist> objects) {
        super(context, 0, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder = null;

        if(v == null) {
            v = mLayoutInflater.inflate(R.layout.list_item_artist, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.list_item_artist_name);
            holder.popularity = (TextView) v.findViewById(R.id.list_item_artist_popularity);
            holder.image = (ImageView) v.findViewById(R.id.list_item_artist_image);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        final Artist artist = getItem(position);

        holder.name.setText(artist.name);
        holder.popularity.setText(artist.popularity.toString());

        if (artist.images.size() > 0) {
            Picasso.with(getContext()).load(artist.images.get(0).url).into(holder.image);
        }

        return v;
    }


    class ViewHolder {
        TextView name;
        TextView popularity;
        ImageView image;
    }
}
