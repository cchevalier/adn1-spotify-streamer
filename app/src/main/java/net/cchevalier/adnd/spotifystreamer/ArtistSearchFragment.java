package net.cchevalier.adnd.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {

    EditText artist;
    ListView listArtist;
    ArrayAdapter<String> mArtistAdapter;

    public ArtistSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //EditText artist = (EditText) getView().findViewById(R.id.artist_name);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        artist = (EditText) rootView.findViewById(R.id.artist_name);

        artist.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //displayArtistAsToast();

                    searchArtist();
                    //handled = true;
                }

                return handled;
            }
        });

        listArtist = (ListView) rootView.findViewById(R.id.listview_artist);
        List<String> emptyList = new ArrayList<>();
        mArtistAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_artist,
                R.id.list_item_artist_textview,
                emptyList
        );
        listArtist.setAdapter(mArtistAdapter);

        listArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedArtist = mArtistAdapter.getItem(position);
                Toast.makeText(getActivity(), selectedArtist, Toast.LENGTH_LONG).show();
            }
        });


        return rootView;
    }

    private void searchArtist() {
        String search = artist.getText().toString();
        mArtistAdapter.clear();

        int count = 10;
        for (Integer i = 0; i < count; i++) {
            mArtistAdapter.add(search + " " + i.toString());
        }


    }

    private void displayArtistAsToast() {
        String search = artist.getText().toString();
        Toast.makeText(getActivity(), search, Toast.LENGTH_SHORT).show();
    }


}
