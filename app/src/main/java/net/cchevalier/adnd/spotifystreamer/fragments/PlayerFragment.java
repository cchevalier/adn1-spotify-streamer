package net.cchevalier.adnd.spotifystreamer.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.cchevalier.adnd.spotifystreamer.PlayerService;
import net.cchevalier.adnd.spotifystreamer.PlayerService.PlayerBinder;
import net.cchevalier.adnd.spotifystreamer.R;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends DialogFragment {

    private static final String KEY_ARTIST_SELECTED = "KEY_ARTIST_SELECTED";
    private static final String KEY_TRACKS_FOUND = "KEY_TRACKS_FOUND";
    private static final String KEY_POSITION = "KEY_POSITION";


    private MyArtist mArtist = null;
    private ArrayList<MyTrack> mTracksFound = null;
    private int mPosition = 0;

    // Service
    private PlayerService mPlayerService;
    private Intent playIntent;
    private boolean mPlayerBound = false;


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
/*
        setRetainInstance(true);
*/

        // Retrieves views
        mArtistView = (TextView) rootView.findViewById(R.id.mp_artist);
        mAlbumView = (TextView) rootView.findViewById(R.id.mp_album);
        mTrackView = (TextView) rootView.findViewById(R.id.mp_track);
        mAlbumArtView = (ImageView) rootView.findViewById(R.id.mp_album_img);

        mPreviousButton = (ImageButton) rootView.findViewById(R.id.button_previous);
        mNextButton = (ImageButton) rootView.findViewById(R.id.button_next);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.button_play_plause);

        // Handles intent
        Intent intent = getActivity().getIntent();

        if (intent != null) {
            if (intent.hasExtra(KEY_ARTIST_SELECTED)) {
                mArtist = intent.getParcelableExtra(KEY_ARTIST_SELECTED);
            }

            if (intent.hasExtra(KEY_POSITION)) {
                mPosition = intent.getIntExtra(KEY_POSITION, 0);
            }

            if (intent.hasExtra(KEY_TRACKS_FOUND)) {
                mTracksFound = intent.getParcelableArrayListExtra(KEY_TRACKS_FOUND);
            }
        }


        updateTrack();

/*
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        MyTrack currentTrack = mTracksFound.get(mPosition);
        try {
            mediaPlayer.setDataSource(currentTrack.preview_url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
*/


        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 0) {
                    mPosition--;
                    updateTrack();
                    playTrack();
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mTracksFound.size() - 1) {
                    mPosition++;
                    updateTrack();
                    playTrack();
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTrack();
            }
        });

        return rootView;
    }


    private void playTrack() {
        mPlayerService.setTrack(mPosition);
        mPlayerService.playTrack();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            if (getArguments().containsKey(KEY_ARTIST_SELECTED)) {
                mArtist = getArguments().getParcelable(KEY_ARTIST_SELECTED);
            }

            if (getArguments().containsKey(KEY_POSITION)) {
                mPosition = getArguments().getInt(KEY_POSITION);
            }

            if (getArguments().containsKey(KEY_TRACKS_FOUND)) {
                mTracksFound = getArguments().getParcelableArrayList(KEY_TRACKS_FOUND);
            }
        }
    }

    // Connect to the PlayerService
    private ServiceConnection playerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerBinder binder = (PlayerBinder)service;
            mPlayerService = binder.getService();
            mPlayerService.setTracksList(mTracksFound);
            mPlayerService.setTrack(mPosition);
            mPlayerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayerBound = false;
        }
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(getActivity(), PlayerService.class);
            getActivity().bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }


    @Override
    public void onDestroyView() {
        mPlayerService.stopService(playIntent);
        mPlayerService = null;
        super.onDestroyView();
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
