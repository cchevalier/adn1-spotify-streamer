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
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment {

    EditText searchView;
    ListView listArtistView;
    ArtistAdapter mArtistAdapter;


    public ArtistFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Retrieves Search View
        searchView = (EditText) rootView.findViewById(R.id.search_view);

        // Launching search using setOnEditorActionListener
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String search = searchView.getText().toString();

                    // Hide the kb
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                    // Launch search as AsyncTask
                    SearchSpotifyForArtist task = new SearchSpotifyForArtist();
                    task.execute(search);

                    handled = true;
                }
                return handled;
            }
        });

        // ListArtistView + Adapter Handling
        listArtistView = (ListView) rootView.findViewById(R.id.listview_artist);
        mArtistAdapter = new ArtistAdapter(getActivity(), new ArrayList<MyArtist>());
        listArtistView.setAdapter(mArtistAdapter);

        // Click handling on item searchView
        listArtistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyArtist selectedArtist = mArtistAdapter.getItem(position);

                // Toast version
                Toast.makeText(getActivity(), selectedArtist.name, Toast.LENGTH_LONG).show();

                // Start TracksActivity
                Intent intent = new Intent(getActivity(), TracksActivity.class);
                intent.putExtra("ARTIST_NAME", selectedArtist.name);
                intent.putExtra("ARTIST_ID", selectedArtist.id);
                startActivity(intent);
            }
        });

        return rootView;
    }


    public class SearchSpotifyForArtist extends AsyncTask<String, Void, List<MyArtist>> {

        @Override
        protected List<MyArtist> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // A basic search on artists named params[0]
            ArtistsPager resultsArtists = service.searchArtists(params[0]);


            List<MyArtist> artists = new ArrayList<MyArtist>();

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
        protected void onPostExecute(List<MyArtist> artists) {
            //super.onPostExecute(aVoid);

            if (artists != null) {
                mArtistAdapter.clear();

                mArtistAdapter.addAll(artists);
/*
                for (int i = 0; i < artists.size(); i++) {
                    mArtistAdapter.add(artists.get(i));
                }
*/
            }
        }


    }
}
