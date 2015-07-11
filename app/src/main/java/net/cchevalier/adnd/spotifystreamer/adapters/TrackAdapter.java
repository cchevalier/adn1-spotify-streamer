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
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.util.List;

/**
 * Created by cch on 11/07/2015.
 */
public class TrackAdapter extends ArrayAdapter<MyTrack> {

    private LayoutInflater mLayoutInflater;

    public TrackAdapter(Context context, List<MyTrack> objects) {
        super(context, 0, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Retrieves or Creates ViewHolder
        ViewHolder holder;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mLayoutInflater.inflate(R.layout.list_item_track, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // Set MyTrack data to Views
        final MyTrack track = getItem(position);
        holder.trackView.setText(track.name);
        holder.albumView.setText(track.album);
        if (track.imageLargeUrl != null) {
            Picasso.with(getContext()).load(track.imageLargeUrl).into(holder.imageView);
        }

        return convertView;
    }

    class ViewHolder {
        TextView trackView;
        TextView albumView;
        ImageView imageView;

        public ViewHolder(View v) {
            trackView = (TextView) v.findViewById(R.id.list_item_track_name);
            albumView = (TextView) v.findViewById(R.id.list_item_album_name);
            imageView = (ImageView) v.findViewById(R.id.list_item_album_image);
        }
    }

}
