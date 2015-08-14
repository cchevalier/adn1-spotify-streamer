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

    private static final String KEY_ARTIST_SELECTED = "KEY_ARTIST_SELECTED";
    private static final String KEY_TRACKS_FOUND = "KEY_TRACKS_FOUND";
    private static final String KEY_POSITION = "KEY_POSITION";


    private MyArtist mArtist = null;
    private ArrayList<MyTrack> mTracksFound = null;
    private int mPosition = 0;

    private TextView mArtistView;
    private TextView mAlbumView;
    private ImageView mAlbumArtView;
    private TextView mTrackView;
    private ImageButton mPreviousButton;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        // Retrieves views
        mArtistView = (TextView) rootView.findViewById(R.id.mp_artist);
        mAlbumView = (TextView) rootView.findViewById(R.id.mp_album);
        mTrackView = (TextView) rootView.findViewById(R.id.mp_track);
        mAlbumArtView = (ImageView) rootView.findViewById(R.id.mp_album_img);

        mPreviousButton = (ImageButton) rootView.findViewById(R.id.button_previous);
        mNextButton = (ImageButton) rootView.findViewById(R.id.button_next);

        // Handles intent
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(KEY_ARTIST_SELECTED)) {
            mArtist = intent.getParcelableExtra(KEY_ARTIST_SELECTED);
        }

        if (intent != null && intent.hasExtra(KEY_POSITION)) {
            mPosition = intent.getIntExtra(KEY_POSITION, 0);
        }

        if (intent != null && intent.hasExtra(KEY_TRACKS_FOUND)) {
            mTracksFound = intent.getParcelableArrayListExtra(KEY_TRACKS_FOUND);
        }
        updateTrack();

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 0) {
                    mPosition--;
                    updateTrack();
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mTracksFound.size() - 1) {
                    mPosition++;
                    updateTrack();
                }
            }
        });


        return rootView;
    }

    private void updateTrack() {

        mArtistView.setText(mArtist.name);

        MyTrack currentTrack = mTracksFound.get(mPosition);

        mAlbumView.setText(currentTrack.album);
        mTrackView.setText(currentTrack.name);

        if (mPosition == 0) {
            mPreviousButton.setEnabled(false);
            mPreviousButton.setClickable(false);
            mPreviousButton.setVisibility(View.INVISIBLE);
        } else {
            mPreviousButton.setEnabled(true);
            mPreviousButton.setClickable(true);
            mPreviousButton.setVisibility(View.VISIBLE);
        }

        if (mPosition == mTracksFound.size() - 1) {
            mNextButton.setEnabled(false);
            mNextButton.setClickable(false);
            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setEnabled(true);
            mNextButton.setClickable(true);
            mNextButton.setVisibility(View.VISIBLE);
        }

        if (currentTrack.UrlLargeImage != null && currentTrack.UrlLargeImage != "") {
            Picasso.with(getActivity())
                    .load(currentTrack.UrlLargeImage)
                    .resize(300, 300)
                    .centerCrop()
                    .into(mAlbumArtView);
        } else {
            mAlbumArtView.setImageResource(android.R.drawable.ic_menu_help);
        }
    }
}
