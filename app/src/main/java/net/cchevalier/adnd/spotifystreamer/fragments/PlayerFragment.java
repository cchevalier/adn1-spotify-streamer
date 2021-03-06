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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.cchevalier.adnd.spotifystreamer.PlayerService;
import net.cchevalier.adnd.spotifystreamer.PlayerService.PlayerBinder;
import net.cchevalier.adnd.spotifystreamer.R;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;
import net.cchevalier.adnd.spotifystreamer.utils.Constants;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends DialogFragment {
    
    private final String TAG = "PLAYER_FRAGMENT";

    private MyArtist mArtist = null;
    private ArrayList<MyTrack> mTracks = null;
    private int mTrackNumber = 0;
    private MyTrack mCurrentTrack = null;

    // Service
    private PlayerService mPlayerService;
    private boolean mPlayerBound = false;


    // Local Views
    private TextView mArtistView;
    private TextView mAlbumView;
    private ImageView mAlbumArtView;
    private TextView mTrackView;

    private TextView mCurrentTime;
    private TextView mTotalDuration;
    private SeekBar mSeekBar;

    private ImageButton mPreviousButton;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;


    private Handler durationHandler = new Handler();



    //
    // playerConnection = ServiceConnection to PlayerService
    //
    private ServiceConnection playerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected ");

            PlayerBinder binder = (PlayerBinder)service;
            mPlayerService = binder.getService();
            mPlayerBound = true;

            updateTrackDisplay();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mPlayerService != null) {
                        int currentTime = mPlayerService.getCurrentTime();
                        mSeekBar.setProgress(currentTime);
                        mCurrentTime.setText(msToMinSec(currentTime));
                    }
                    durationHandler.postDelayed(this, 1000);
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected ");

            mPlayerBound = false;
        }

    };



    public PlayerFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ");

        super.onCreate(savedInstanceState);
        setRetainInstance(true);


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

        mCurrentTime = (TextView) rootView.findViewById(R.id.mp_current_time);
        mTotalDuration = (TextView) rootView.findViewById(R.id.mp_total_duration);

        mPreviousButton = (ImageButton) rootView.findViewById(R.id.button_previous);
        mNextButton = (ImageButton) rootView.findViewById(R.id.button_next);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.button_play_plause);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        mSeekBar.setClickable(false);
        int spotifyDuration = 30000;
        mSeekBar.setMax(spotifyDuration);

        Intent playIntent = new Intent(getActivity(), PlayerService.class);
        playIntent.setAction(Constants.ACTION_CONNECT);
        getActivity().bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);

        return rootView;
    }


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
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume ");

        super.onResume();

        if (mPlayerBound) {
            updateTrackDisplay();
        }

        // Media buttons Listeners
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerService.playPrevious();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerService.playNext();
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerService.isPlaying()) {
                    mPlayerService.pause();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mPlayerService.resumePlay();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        // SeekBar Listeners
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

        // Broadcast Receiver of Player Service Messages
        IntentFilter broadcastIntentFilter = new IntentFilter();
        broadcastIntentFilter.addAction(Constants.PS_LAST_TRACK_COMPLETED);
        broadcastIntentFilter.addAction(Constants.PS_NEW_TRACK_STARTED);
        broadcastIntentFilter.addAction(Constants.PS_TRACK_PAUSE);
        broadcastIntentFilter.addAction(Constants.PS_TRACK_RESUME);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, broadcastIntentFilter);

    }

    //
    // How to properly retain a DialogFragment through rotation?
    // http://stackoverflow.com/questions/14657490/how-to-properly-retain-a-dialogfragment-through-rotation
    //
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause ");
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void onStop() {
        getActivity().unbindService(playerConnection);
        mPlayerService = null;
        mPlayerBound = false;

        super.onStop();
    }

    //
    // How to properly retain a DialogFragment through rotation?
    // http://stackoverflow.com/questions/14657490/how-to-properly-retain-a-dialogfragment-through-rotation
    //
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ");

        //getActivity().unbindService(playerConnection);
        //mPlayerService = null;
        //mPlayerBound = false;

        super.onDestroyView();
    }


    private void playTrack() {
        Log.d(TAG, "playTrack ");

        mPlayerService.playTrack();
        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
    }


    private void updateProgress() {
        Log.d(TAG, "updateProgress ");


        if (mPlayerService != null) {
            int currentTime = mPlayerService.getCurrentTime();
            mSeekBar.setProgress(currentTime);
            mCurrentTime.setText(msToMinSec(currentTime));
        }
    }


    private void updateTrackDisplay() {
        Log.d(TAG, "updateTrackDisplay ");

        mArtist = mPlayerService.getArtist();
        mArtistView.setText(mArtist.name);

        mCurrentTrack = mPlayerService.getCurrentTrack();

        mAlbumView.setText(mCurrentTrack.album);
        mTrackView.setText(mCurrentTrack.name);

        mTracks = mPlayerService.getTracks();
        mTrackNumber = mPlayerService.getTrackNumber();

        if (mPlayerService != null & mPlayerService.isPlaying()) {
            int totalDuration = mPlayerService.getTotalDuration();
            mSeekBar.setMax(totalDuration);
            mTotalDuration.setText(msToMinSec(totalDuration));

            int currentTime = mPlayerService.getCurrentTime();
            mCurrentTime.setText(msToMinSec(currentTime));
        }

        if (mPlayerService.isFirstTrack()) {
            mPreviousButton.setEnabled(false);
            mPreviousButton.setClickable(false);
            mPreviousButton.setVisibility(View.INVISIBLE);
        } else {
            mPreviousButton.setEnabled(true);
            mPreviousButton.setClickable(true);
            mPreviousButton.setVisibility(View.VISIBLE);
        }

        if (mPlayerService.isPlaying()) {
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
        }

        if (mPlayerService.isLastTrack()) {
            mNextButton.setEnabled(false);
            mNextButton.setClickable(false);
            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setEnabled(true);
            mNextButton.setClickable(true);
            mNextButton.setVisibility(View.VISIBLE);
        }

        if (mCurrentTrack.UrlLargeImage != null && !mCurrentTrack.UrlLargeImage.equals("")) {
            Picasso.with(getActivity())
                    .load(mCurrentTrack.UrlLargeImage)
                    .resize(300, 300)
                    .centerCrop()
                    .into(mAlbumArtView);
        } else {
            mAlbumArtView.setImageResource(android.R.drawable.ic_menu_help);
        }
    }


    private String msToMinSec(int ms) {

        ms = ms / 1000;

        int min = ms / 60;
        int sec = ms % 60;

        String resultString = Integer.toString(min) + ":";
        if (sec < 10) {
            resultString = resultString + "0";
        }
        resultString = resultString + Integer.toString(sec);

        return resultString;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String info = intent.getAction();
            Log.d(TAG, "onReceive " + info);

            if (info.equals(Constants.PS_NEW_TRACK_STARTED)) {
                Toast.makeText(context, "New Track started", Toast.LENGTH_SHORT).show();
                updateTrackDisplay();
            }
            else if (info.equals(Constants.PS_LAST_TRACK_COMPLETED)) {
                Toast.makeText(context, "Last Track completed", Toast.LENGTH_SHORT).show();
                updateTrackDisplay();
            }
            else if (info.equals(Constants.PS_TRACK_PAUSE)){
                Toast.makeText(context, "Current Track paused", Toast.LENGTH_SHORT).show();
                updateTrackDisplay();
            }
            else if (info.equals(Constants.PS_TRACK_RESUME)) {
                Toast.makeText(context, "Current Track resumed", Toast.LENGTH_SHORT).show();
                updateTrackDisplay();
            }

        }
    };

}
