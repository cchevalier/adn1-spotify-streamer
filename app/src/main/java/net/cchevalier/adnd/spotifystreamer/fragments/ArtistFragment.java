package net.cchevalier.adnd.spotifystreamer.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import net.cchevalier.adnd.spotifystreamer.adapters.ArtistAdapter;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.utils.Constants;

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

    private String mSearchString;
    private ArrayList<MyArtist> mArtistsFound;

    private EditText mSearchView;
    private ListView mListArtistView;

    private ArtistAdapter mArtistAdapter;


    public interface Callbacks {
        public void onArtistSelected(MyArtist selectedArtist);
    }


    public ArtistFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(Constants.KEY_SEARCH_STRING);
            mArtistsFound = savedInstanceState.getParcelableArrayList(Constants.EXTRA_ARTIST);
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
                ((Callbacks) getActivity()).onArtistSelected(selectedArtist);

            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // our own data to preserve
        outState.putString(Constants.KEY_SEARCH_STRING, mSearchString);
        outState.putParcelableArrayList(Constants.EXTRA_ARTIST, mArtistsFound);

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
/*
                // Logging
                Log.i("SAPI", i + " " + artist.name);
                Log.i("SAPI", i + "  pop:" + artist.popularity.toString());
                Log.i("SAPI", i + "   id: " + artist.id);
                Log.i("SAPI", i + "  uri: " + artist.uri);
                if (artist.images.size() > 0) {
                    Log.i("SAPI", i + "  url: " + artist.images.get(0).url);
                }
*/
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
