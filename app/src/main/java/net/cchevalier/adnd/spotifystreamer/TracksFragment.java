package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.cchevalier.adnd.spotifystreamer.adapters.TrackAdapter;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * TracksFragment
 */
public class TracksFragment extends Fragment {

    static final String ARTIST_SELECTED = "artistSelected";

    ListView listTrackView;
    TrackAdapter trackAdapter;

    ArrayList<MyTrack> tracksFound = new ArrayList<>();

    public TracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_tracks, container, false);

        // Handling of intent
        String artistId = "";
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(ARTIST_SELECTED)) {
            MyArtist artist = intent.getParcelableExtra(ARTIST_SELECTED);
            artistId = artist.id;
        }

        // Retrieve listTrackView
        listTrackView = (ListView) rootView.findViewById(R.id.listview_tracks);

        // Create  / Assign the track Array Adapter
        trackAdapter = new TrackAdapter(getActivity(), tracksFound);
        listTrackView.setAdapter(trackAdapter);


        // Launch tracks search as AsyncTask
        SearchSpotifyForTopTrack task = new SearchSpotifyForTopTrack();
        task.execute(artistId, "DK");


        // Event: Click on a track
        listTrackView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTrack selectedTrack = trackAdapter.getItem(position);
                String display = "Stage 2:\nWill launch player for track\n" + selectedTrack.name;
                Toast toast = Toast.makeText(getActivity(), display, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });

        return rootView;
    }


    /*
    * ASYNC TASK: SearchSpotifyForTopTrack
    *
    * */
    public class SearchSpotifyForTopTrack extends AsyncTask<String, Void, ArrayList<MyTrack>> {

        @Override
        protected ArrayList<MyTrack> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String artistId = params[0];
            String country = params[1];

            // Start Spotify services
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // Set country
            Map<String, Object> options = new HashMap<>();
            options.put("country", country);

            // Top Ten Tracks for most famous searchView
            Tracks results = service.getArtistTopTrack(artistId, options);

            ArrayList<MyTrack> output  = new ArrayList<>();
            for (int i = 0; i < results.tracks.size(); i++) {
                output.add(new MyTrack(results.tracks.get(i)));
            }

            return output;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            trackAdapter.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<MyTrack> tracks) {
//            super.onPostExecute(tracks);

            if (tracks == null || tracks.isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), " No track found", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                tracksFound = tracks;
                trackAdapter.addAll(tracks);
            }
        }
    }

}
