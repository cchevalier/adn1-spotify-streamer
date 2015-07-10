package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class TracksFragment extends Fragment {

    ListView listTrack;
    ArrayAdapter<String> mTrackAdapter;

    public TracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_tracks, container, false);

        // Handling of intent
        String artist = "dummy";
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            artist = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.artist_header_view)).setText(artist);
        }

        // Retrieve listTrack
        listTrack = (ListView) rootView.findViewById(R.id.listview_tracks);

        // Build a dummy list of tracks (temporary)
        List<String> tracks = new ArrayList<>();
        for (Integer i = 1; i <= 10; i++) {
            String tmp = artist + " - Track " + i.toString();
            tracks.add(tmp);
        }

        // Create the track Array Adapter
        mTrackAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_track,
                R.id.list_item_track_name,
                tracks
        );

        // Assign adapter to listTrack
        listTrack.setAdapter(mTrackAdapter);

        listTrack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedTrack = mTrackAdapter.getItem(position);

                Toast.makeText(getActivity(), selectedTrack, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
