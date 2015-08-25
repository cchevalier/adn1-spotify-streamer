package net.cchevalier.adnd.spotifystreamer.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
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
    
    private final String TAG = "PLAY_FRAG";

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

    private boolean isOnPause = false;

    // Local Views
    private TextView mArtistView;
    private TextView mAlbumView;
    private ImageView mAlbumArtView;
    private TextView mTrackView;

    private ImageButton mPreviousButton;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;

    private SeekBar mSeekBar;
    private int spotifyDuration = 30000;
    private Handler durationHandler = new Handler();


    private final String PLAY_COMPLETED = "PLAY_COMPLETED";
    private IntentFilter intentFilter;


    public PlayerFragment() {
        Log.d(TAG, "PlayerFragment ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ");

        super.onCreate(savedInstanceState);
//        setRetainInstance(true);

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        // Retrieves views
        mArtistView = (TextView) rootView.findViewById(R.id.mp_artist);
        mAlbumView = (TextView) rootView.findViewById(R.id.mp_album);
        mTrackView = (TextView) rootView.findViewById(R.id.mp_track);
        mAlbumArtView = (ImageView) rootView.findViewById(R.id.mp_album_img);

        mPreviousButton = (ImageButton) rootView.findViewById(R.id.button_previous);
        mNextButton = (ImageButton) rootView.findViewById(R.id.button_next);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.button_play_plause);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        mSeekBar.setMax(spotifyDuration);
        mSeekBar.setClickable(false);

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

        updateTrackDisplay();

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 0) {
                    mPosition--;
                    updateTrackDisplay();
                    playTrack();
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mTracksFound.size() - 1) {
                    mPosition++;
                    updateTrackDisplay();
                    playTrack();
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerService.isPlaying()) {
                    mPlayerService.pause();
                    isOnPause = true;
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    if (isOnPause) {
                        mPlayerService.play();
                    } else {
                        playTrack();
                    }
                    isOnPause = false;
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayerService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return rootView;
    }




    /*
    *   playerConnection = ServiceConnection to PlayerService
    * */
    private ServiceConnection playerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected ");

            PlayerBinder binder = (PlayerBinder)service;
            mPlayerService = binder.getService();
            mPlayerService.setTracks(mTracksFound);
            mPlayerService.setTrackNumber(mPosition);
            mPlayerBound = true;
            playTrack();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected ");

            mPlayerBound = false;
        }
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog ");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onStart() {
        Log.d(TAG, "onStart ");
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(getActivity(), PlayerService.class);
            getActivity().bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume ");
        super.onResume();

        intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY_COMPLETED);
        getActivity().registerReceiver(intentReceiver, intentFilter);

    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPause ");

        super.onPause();
        getActivity().unregisterReceiver(intentReceiver);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ");

        getActivity().unbindService(playerConnection);
        //getActivity().stopService(playIntent);
        mPlayerService = null;
        super.onDestroyView();
    }



    private void playTrack() {
        Log.d(TAG, "playTrack ");

        mPlayerService.setTrackNumber(mPosition);
        mPlayerService.playTrack();
        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPlayerService != null) {
                    int currentPosition = mPlayerService.getCurrentPosition();
                    mSeekBar.setProgress(currentPosition);
                    durationHandler.postDelayed(this, 1000);
                }
            }
        });
    }


    private void updateTrackDisplay() {
        Log.d(TAG, "updateTrackDisplay ");

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


    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive ");

            //Toast.makeText(context, "Play Track completed", Toast.LENGTH_SHORT ).show();
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);

            if (mPosition < mTracksFound.size() - 1) {
                mPosition++;
                updateTrackDisplay();
                playTrack();
            }
        }
    };

}
