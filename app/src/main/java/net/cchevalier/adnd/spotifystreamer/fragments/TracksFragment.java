package net.cchevalier.adnd.spotifystreamer.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.cchevalier.adnd.spotifystreamer.PlayerActivity;
import net.cchevalier.adnd.spotifystreamer.PlayerService;
import net.cchevalier.adnd.spotifystreamer.R;
import net.cchevalier.adnd.spotifystreamer.adapters.TrackAdapter;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * TracksFragment
 */
public class TracksFragment extends Fragment {

    private final String TAG = "TRACKS_FRAG";

    public static final String KEY_ARTIST_SELECTED = "KEY_ARTIST_SELECTED";
    public static final String KEY_TRACKS_FOUND = "KEY_TRACKS_FOUND";
    public static final String KEY_POSITION = "KEY_POSITION";
    public static final String KEY_UI_TABLET = "KEY_UI_TABLET";

    private ListView mTrackListView;

    private TrackAdapter mTrackAdapter;

    private MyArtist mArtist = null;
    String mArtistId = "";
    private ArrayList<MyTrack> mTracksFound = new ArrayList<>();

    private boolean mUiTablet = false;

    public interface Callbacks {

        public void onTrackSelected(MyArtist selectedArtist, ArrayList<MyTrack> TracksFound, int position);
    }


    public TracksFragment() {
        Log.d(TAG, "TracksFragment ");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ");
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(KEY_ARTIST_SELECTED)) {
            mArtist = getArguments().getParcelable(KEY_ARTIST_SELECTED);
        }
        mArtistId = mArtist.id;

        if (getArguments().containsKey(KEY_UI_TABLET)) {
            mUiTablet = getArguments().getBoolean(KEY_UI_TABLET);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");

        View rootView =  inflater.inflate(R.layout.fragment_tracks, container, false);

/*
        // Handling of intent
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(KEY_ARTIST_SELECTED)) {
            mArtist = intent.getParcelableExtra(KEY_ARTIST_SELECTED);
            mArtistId = mArtist.id;
        }
*/

        if (savedInstanceState != null) {
            mTracksFound = savedInstanceState.getParcelableArrayList(KEY_TRACKS_FOUND);
        }

        // Retrieve mTrackListView
        mTrackListView = (ListView) rootView.findViewById(R.id.listview_tracks);

        // Create  / Assign the track Array Adapter
        mTrackAdapter = new TrackAdapter(getActivity(), mTracksFound);
        mTrackListView.setAdapter(mTrackAdapter);


        // Event: Click on a track
        mTrackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

/*
                // Stage 1: display toast instead of launching mediaPlayer
                MyTrack selectedTrack = mTrackAdapter.getItem(position);
                String display = "Stage 2:\nWill launch player for track\n" + selectedTrack.name;
                Toast toast = Toast.makeText(getActivity(), display, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
*/

                // Stage 2: launch PlayerActivity
                if (mUiTablet) {
                    ((Callbacks) getActivity()).onTrackSelected(mArtist, mTracksFound, position);
                } else {
                    Intent playerServiceIntent = new Intent(getActivity(), PlayerService.class);
                    playerServiceIntent.setAction(PlayerService.ACTION_START);
                    playerServiceIntent.putExtra(PlayerService.EXTRA_ARTIST, mArtist);
                    playerServiceIntent.putParcelableArrayListExtra(PlayerService.EXTRA_TRACKS, mTracksFound);
                    playerServiceIntent.putExtra(PlayerService.EXTRA_TRACK_NB, position);
                    getActivity().startService(playerServiceIntent);

                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra(KEY_ARTIST_SELECTED, mArtist);
                    intent.putParcelableArrayListExtra(KEY_TRACKS_FOUND, mTracksFound);
                    intent.putExtra(KEY_POSITION, position);
                    startActivity(intent);


                }
/*
                if (mUiTablet) {
                    ((Callbacks) getActivity()).onTrackSelected(mArtist, mTracksFound, position);
                }
                else {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra(KEY_ARTIST_SELECTED, mArtist);
                    intent.putParcelableArrayListExtra(KEY_TRACKS_FOUND, mTracksFound);
                    intent.putExtra(KEY_POSITION, position);
                    startActivity(intent);
                }
*/
            }
            });

        if (mTracksFound.isEmpty()) {
            // Launch tracks search as AsyncTask with country = DK (hardcoded)
            SearchSpotifyForTopTrack task = new SearchSpotifyForTopTrack();
            task.execute(mArtistId, "DK");
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState ");
        // our own data to preserve
        outState.putParcelableArrayList(KEY_TRACKS_FOUND, mTracksFound);
        super.onSaveInstanceState(outState);
    }




    /*
    * ASYNC TASK: SearchSpotifyForTopTrack
    *
    * */
    public class SearchSpotifyForTopTrack extends AsyncTask<String, Void, ArrayList<MyTrack>> {

        boolean mFetchErrorFlag;

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
            Tracks results;
            try {
                results = service.getArtistTopTrack(artistId, options);
            } catch (RetrofitError e) {
                e.printStackTrace();
                mFetchErrorFlag = true;
                return null;
            }

            ArrayList<MyTrack> output  = new ArrayList<>();
            for (int i = 0; i < results.tracks.size(); i++) {
                output.add(new MyTrack(results.tracks.get(i)));
            }

            return output;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFetchErrorFlag = false;
            mTrackAdapter.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<MyTrack> tracks) {
//            super.onPostExecute(tracks);

            if (mFetchErrorFlag){
                Toast toast = Toast.makeText(getActivity(),
                        "Error fetching track data.\nPlease check your \nnetwork connection. ", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                return;
            }

            if (tracks == null || tracks.isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), " No track found", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                mTracksFound = tracks;
                mTrackAdapter.addAll(tracks);
            }
        }
    }

}
