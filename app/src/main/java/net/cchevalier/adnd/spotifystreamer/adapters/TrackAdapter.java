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
 * A Custom ArrayAdapter for MyTrack
 *
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
        // FIX: check on Url not empty to avoid IllegalArgumentException
        if (track.UrlMediumImage != null && !track.UrlMediumImage.equals("")) {
            Picasso.with(getContext())
                    .load(track.UrlMediumImage)
                    .resize(90, 90)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_help);
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
