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

        // Creates or Retrieves ViewHolder
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_artist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set Artist data to Views
        final Artist artist = getItem(position);
        holder.nameView.setText(artist.name);
        holder.popularityView.setText(artist.popularity.toString());
        if (artist.images.size() > 0) {
            Picasso.with(getContext()).load(artist.images.get(0).url).into(holder.imageView);
        }

        return convertView;
    }


    class ViewHolder {
        TextView nameView;
        TextView popularityView;
        ImageView imageView;

        public ViewHolder(View v) {
            nameView = (TextView) v.findViewById(R.id.list_item_artist_name);
            popularityView = (TextView) v.findViewById(R.id.list_item_artist_popularity);
            imageView = (ImageView) v.findViewById(R.id.list_item_artist_image);
        }
    }
}
