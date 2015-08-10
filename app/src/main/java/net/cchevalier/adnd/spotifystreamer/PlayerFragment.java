package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    static final String ARTIST_SELECTED = "artistSelected";
    static final String TRACKS_FOUND = "tracksFound";
    static final String POSITION = "position";


    MyArtist artist = null;
    ArrayList<MyTrack> tracksFound = null;
    int position = 0;

    TextView artistView;
    TextView albumView;
    ImageView albumImageView;
    TextView trackView;

    ImageButton previousButton;
    ImageButton playButton;
    ImageButton nextButton;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        // Retrieves views
        artistView = (TextView) rootView.findViewById(R.id.mp_artist);
        albumView = (TextView) rootView.findViewById(R.id.mp_album);
        trackView = (TextView) rootView.findViewById(R.id.mp_track);
        albumImageView = (ImageView) rootView.findViewById(R.id.mp_album_img);

        previousButton = (ImageButton) rootView.findViewById(R.id.button_previous);
        nextButton = (ImageButton) rootView.findViewById(R.id.button_next);

        // Handles intent
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(ARTIST_SELECTED)) {
            artist = intent.getParcelableExtra(ARTIST_SELECTED);
        }

        if (intent != null && intent.hasExtra(POSITION)) {
            position = intent.getIntExtra(POSITION, 0);
        }

        if (intent != null && intent.hasExtra(TRACKS_FOUND)) {
            tracksFound = intent.getParcelableArrayListExtra(TRACKS_FOUND);
        }
        updateTrack();

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    position--;
                    updateTrack();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < tracksFound.size() - 1) {
                    position++;
                    updateTrack();
                }
            }
        });


        return rootView;
    }

    private void updateTrack() {

        artistView.setText(artist.name);

        MyTrack currentTrack = tracksFound.get(position);

        albumView.setText(currentTrack.album);
        trackView.setText(currentTrack.name);

        if (position == 0) {
            previousButton.setEnabled(false);
            previousButton.setClickable(false);
            previousButton.setVisibility(View.INVISIBLE);
        } else {
            previousButton.setEnabled(true);
            previousButton.setClickable(true);
            previousButton.setVisibility(View.VISIBLE);
        }

        if (position == tracksFound.size() - 1) {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setEnabled(true);
            nextButton.setClickable(true);
            nextButton.setVisibility(View.VISIBLE);
        }

        if (currentTrack.UrlLargeImage != null && currentTrack.UrlLargeImage != "") {
            Picasso.with(getActivity())
                    .load(currentTrack.UrlLargeImage)
                    .resize(300, 300)
                    .centerCrop()
                    .into(albumImageView);
        } else {
            albumImageView.setImageResource(android.R.drawable.ic_menu_help);
        }
    }
}
