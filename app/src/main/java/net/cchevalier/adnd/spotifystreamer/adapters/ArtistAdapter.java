package net.cchevalier.adnd.spotifystreamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.cchevalier.adnd.spotifystreamer.R;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;

import java.util.List;

/**
 * Created by cch on 09/07/2015.
 */
public class ArtistAdapter extends ArrayAdapter<MyArtist> {

    private LayoutInflater mLayoutInflater;

    public ArtistAdapter(Context context, List<MyArtist> objects) {
        super(context, 0, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Retrieves or Creates ViewHolder
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mLayoutInflater.inflate(R.layout.list_item_artist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // Set Artist data to Views
        final MyArtist artist = getItem(position);
        holder.nameView.setText(artist.name);
        holder.popularityView.setText(artist.popularity.toString());
        if (artist.imageLargeUrl != null) {
            Picasso.with(getContext()).load(artist.imageLargeUrl).into(holder.imageView);
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
