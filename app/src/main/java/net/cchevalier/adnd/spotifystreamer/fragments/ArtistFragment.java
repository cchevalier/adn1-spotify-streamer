package net.cchevalier.adnd.spotifystreamer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.cchevalier.adnd.spotifystreamer.R;
import net.cchevalier.adnd.spotifystreamer.TracksActivity;
import net.cchevalier.adnd.spotifystreamer.adapters.ArtistAdapter;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * ArtistFragment
 */
public class ArtistFragment extends Fragment {

    private static final String KEY_SEARCH_STRING = "KEY_SEARCH_STRING";
    private static final String KEY_ARTISTS_FOUND = "KEY_ARTISTS_FOUND";
    private static final String KEY_ARTIST_SELECTED = "KEY_ARTIST_SELECTED";

    private EditText mSearchView;
    private ListView mListArtistView;

    private ArtistAdapter mArtistAdapter;

    private String mSearchString;
    private ArrayList<MyArtist> mArtistsFound;


    public ArtistFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(KEY_SEARCH_STRING);
            mArtistsFound = savedInstanceState.getParcelableArrayList(KEY_ARTISTS_FOUND);
        } else {
            mSearchString = "";
            mArtistsFound = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Retrieves mSearchView / Set value to mSearchString (possibly restored)
        mSearchView = (EditText) rootView.findViewById(R.id.search_view);
        mSearchView.setText(mSearchString);

        // Retrieves mListArtistView / Set Adapter + value (possibly restored)
        mListArtistView = (ListView) rootView.findViewById(R.id.listview_artist);
        mArtistAdapter = new ArtistAdapter(getActivity(), mArtistsFound);
        mListArtistView.setAdapter(mArtistAdapter);


        // Launching search using setOnEditorActionListener
        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    mSearchString = mSearchView.getText().toString();

                    // Hide the kb
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

                    // Launch search as AsyncTask
                    SearchSpotifyForArtist task = new SearchSpotifyForArtist();
                    task.execute(mSearchString);

                    handled = true;
                }
                return handled;
            }
        });


        // Click handling on item mListArtistView
        mListArtistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyArtist selectedArtist = mArtistAdapter.getItem(position);

                // Start TracksActivity
                Intent intent = new Intent(getActivity(), TracksActivity.class);
                intent.putExtra(KEY_ARTIST_SELECTED, selectedArtist);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        // our own data to preserve
        outState.putString(KEY_SEARCH_STRING, mSearchString);
        outState.putParcelableArrayList(KEY_ARTISTS_FOUND, mArtistsFound);

        super.onSaveInstanceState(outState);
    }




/*
*   ASYNC TASK: SearchSpotifyForArtist
*
* */
    public class SearchSpotifyForArtist extends AsyncTask<String, Void, ArrayList<MyArtist>> {

        private boolean mFetchErrorFlag;

        @Override
        protected ArrayList<MyArtist> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // Start Spotify services
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // A basic search on artists named params[0]
            ArtistsPager resultsArtists;
            try {
                resultsArtists = service.searchArtists(params[0]);
            } catch (RetrofitError e){
                e.printStackTrace();
                mFetchErrorFlag = true;
                return null;
            }


            ArrayList<MyArtist> artists = new ArrayList<>();
            int count = resultsArtists.artists.items.size();
            for (int i = 0; i < count; i++) {
                Artist artist = resultsArtists.artists.items.get(i);
                artists.add(new MyArtist(artist));

                Log.i("SAPI", i + " " + artist.name);
//                Log.i("SAPI", i + "  pop:" + artist.popularity.toString());
//                Log.i("SAPI", i + "   id: " + artist.id);
//                Log.i("SAPI", i + "  uri: " + artist.uri);
                if (artist.images.size() > 0) {
                    Log.i("SAPI", i + "  url: " + artist.images.get(0).url);
                }
            }

            return artists;
        }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mFetchErrorFlag = false;
        mArtistAdapter.clear();
    }


    @Override
        protected void onPostExecute(ArrayList<MyArtist> artists) {

            if (mFetchErrorFlag){
                Toast toast = Toast.makeText(getActivity(),
                        "Error fetching data.\nPlease check your \nnetwork connection. ", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                return;
            }

            if (artists == null || artists.isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), "No artist found", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } else {
                mArtistsFound = artists;
                mArtistAdapter.addAll(artists);
            }
        }
    }
}
