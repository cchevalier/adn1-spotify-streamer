package net.cchevalier.adnd.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import net.cchevalier.adnd.spotifystreamer.adapters.ArtistAdapter;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment {

    static final String SEARCH_FIELD = "searchField";
    static final String ARTISTS_FOUND = "artistsFound";
    static final String ARTIST_SELECTED = "artistSelected";

    EditText searchView;
    ListView listArtistView;
    ArtistAdapter artistAdapter;

    String searchField;
    ArrayList<MyArtist> artistsFound;


    public ArtistFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            searchField = savedInstanceState.getString(SEARCH_FIELD);
            artistsFound = savedInstanceState.getParcelableArrayList(ARTISTS_FOUND);
        } else {
            searchField = "";
            artistsFound = new ArrayList<MyArtist>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Retrieves searchView / Set value to searchField (possibly restored)
        searchView = (EditText) rootView.findViewById(R.id.search_view);
        searchView.setText(searchField);

        // Retrieves listArtistView / Set Adapter + value (possibly restored)
        listArtistView = (ListView) rootView.findViewById(R.id.listview_artist);
        artistAdapter = new ArtistAdapter(getActivity(), artistsFound);
        listArtistView.setAdapter(artistAdapter);


        // Launching search using setOnEditorActionListener
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    searchField = searchView.getText().toString();

                    // Hide the kb
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                    // Launch search as AsyncTask
                    SearchSpotifyForArtist task = new SearchSpotifyForArtist();
                    task.execute(searchField);

                    handled = true;
                }
                return handled;
            }
        });


        // Click handling on item listArtistView
        listArtistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyArtist selectedArtist = artistAdapter.getItem(position);

                // Toast version
                Toast.makeText(getActivity(), selectedArtist.name, Toast.LENGTH_LONG).show();

                // Start TracksActivity
                Intent intent = new Intent(getActivity(), TracksActivity.class);
                intent.putExtra(ARTIST_SELECTED, selectedArtist);
//                intent.putExtra("ARTIST_NAME", selectedArtist.name);
//                intent.putExtra("ARTIST_ID", selectedArtist.id);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        // our own data to preserve
        outState.putString(SEARCH_FIELD, searchField);
        outState.putParcelableArrayList(ARTISTS_FOUND, artistsFound);

        super.onSaveInstanceState(outState);
    }




/*
*   ASYNC TASK
*
* */
    public class SearchSpotifyForArtist extends AsyncTask<String, Void, ArrayList<MyArtist>> {

        @Override
        protected ArrayList<MyArtist> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // A basic search on artists named params[0]
            ArtistsPager resultsArtists = service.searchArtists(params[0]);


            ArrayList<MyArtist> artists = new ArrayList<MyArtist>();

            int count = resultsArtists.artists.items.size();
            for (int i = 0; i < count; i++) {
                Artist artist = resultsArtists.artists.items.get(i);
                artists.add(new MyArtist(artist));

                Log.i("SAPI", i + " " + artist.name);
                Log.i("SAPI", i + "  pop:" + artist.popularity.toString());
                Log.i("SAPI", i + "   id: " + artist.id);
                Log.i("SAPI", i + "  uri: " + artist.uri);
                if (artist.images.size() > 0) {
                    Log.i("SAPI", i + "  url: " + artist.images.get(0).url);
                }            }

/*
            // logcat: Top Ten Tracks for most famous searchView named on previous search
            TracksPager resultsTracks = service.searchTracks(artists.get(0).name);
            List<Track> tracks = resultsTracks.tracks.items;
            for (int i = 0; i < 10; i++) {
                Track track = tracks.get(i);
                Log.i("SAPI", i + " - " + track.name );
            }
*/

            return artists;
        }

        @Override
        protected void onPostExecute(ArrayList<MyArtist> artists) {
            if (artists != null) {
                artistsFound = artists;
                artistAdapter.clear();
                artistAdapter.addAll(artists);
            }
        }


    }
}
