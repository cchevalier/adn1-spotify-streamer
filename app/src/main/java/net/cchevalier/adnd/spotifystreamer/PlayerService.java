package net.cchevalier.adnd.spotifystreamer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cch on 19/08/2015.
 */
public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final String TAG = "PLAY_SERVICE";

    // Intents ACTION "net.cchevalier.adnd.spotifystreamer"
    public final static String ACTION_SHOW = "net.cchevalier.adnd.spotifystreamer.ACTION_SHOW";
    public final static String ACTION_START = "net.cchevalier.adnd.spotifystreamer.ACTION_START";

    //  Intents EXTRA
    public final static String EXTRA_ARTIST = "EXTRA_ARTIST";
    public final static String EXTRA_TRACKS = "EXTRA_TRACKS";
    public final static String EXTRA_TRACK_NB = "EXTRA_TRACK_NB";

    // Feedback messages
    private final String PLAY_COMPLETED = "PLAY_COMPLETED";

    private MediaPlayer mMediaPlayer = null;

    private final IBinder mPlayerBind = new PlayerBinder();

    private static final int NOTIFICATION_ID = 1;

    // Tracks settings
    private MyArtist mArtist;
    private ArrayList<MyTrack> mTracks = null;
    private int mTrackNumber = -1;
    private MyTrack mCurrentTrack;


    /*
    * PlayerBinder
    * */
    public class PlayerBinder extends Binder {

        public PlayerService getService() {
            Log.d(TAG, "PlayerBinder.getService ");
            return PlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
    /*
        The system calls this method when another component wants to bind with the
        service by calling bindService(). In your implementation of this method,
        you must provide an interface that clients use to communicate with the service,
        by returning an IBinder.

        You must always implement this method, but if you don't want to allow binding,
        then you should return null.
    */
        Log.d(TAG, "onBind ");
        return mPlayerBind;
    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind ");
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
        return false;
    }


    /*
    *
    * */
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate ");
        super.onCreate();

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            initMediaPlayer();
        }


/*
        // Create a notification area notification so the user
        // can get back to the MusicServiceClient
        final Intent notificationIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        final Notification notification = new Notification.Builder(
                getApplicationContext())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true).setContentTitle("Music Playing")
                .setContentText("Click to Access Music Player")
                .setContentIntent(pendingIntent).build();

        // Put this Service in a foreground state, so it won't
        // readily be killed by the system
        startForeground(NOTIFICATION_ID, notification);
*/
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);

        if (intent == null | intent.getAction() == null) {
            return START_STICKY;
        }

        if (intent.getAction() == PlayerService.ACTION_START) {
            mArtist = intent.getParcelableExtra(PlayerService.EXTRA_ARTIST);
            mTracks = intent.getParcelableArrayListExtra(PlayerService.EXTRA_TRACKS);
            mTrackNumber = intent.getIntExtra(PlayerService.EXTRA_TRACK_NB, 0);
            playTrack();
        }

        return START_STICKY;
    }

    public void initMediaPlayer() {
        Log.d(TAG, "initMediaPlayer ");

        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        Toast.makeText(this, "PlayerService done", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    /*
    * Required MediaPlayer Listeners
    * */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared ");
        mp.start();

        Toast.makeText(getApplicationContext(), "Now playing..." + mCurrentTrack.name, Toast.LENGTH_SHORT).show();

        // Create a notification area notification so the user
        // can get back to the MusicServiceClient
        final Intent notificationIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        final Notification notification = new Notification.Builder(
                getApplicationContext())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true).setContentTitle(mCurrentTrack.name)
                .setContentText(mArtist.name + " / " + mCurrentTrack.album)
                .setContentIntent(pendingIntent).build();

        // Put this Service in a foreground state, so it won't
        // readily be killed by the system
        startForeground(NOTIFICATION_ID, notification);

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError ");
        mp.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion ");

        if (mTrackNumber < mTracks.size() - 1) {
            mTrackNumber++;
            playTrack();
        } else {
            Toast.makeText(getApplicationContext(), "Playlist ended", Toast.LENGTH_SHORT).show();

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(PLAY_COMPLETED);
            getBaseContext().sendBroadcast(broadcastIntent);

            stopSelf();
        }


    }




    public void playTrack() {
        Log.d(TAG, "playTrack ");

        mCurrentTrack = mTracks.get(mTrackNumber);

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mCurrentTrack.preview_url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PLAYER SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }


    public void pause() {
        mMediaPlayer.pause();
    }


    public void play() {
        mMediaPlayer.start();
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }


    /*
    * get / set Methods
    * */
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }


    public void setTracks(ArrayList<MyTrack> theTracks) {
        mTracks = theTracks;
    }


    public void setTrackNumber(int number) {
        this.mTrackNumber = number;
    }

    public int getTrackNumber() {
        return mTrackNumber;
    }

    public ArrayList<MyTrack> getTracks() {
        return mTracks;
    }

    public MyTrack getCurrentTrack() {
        return mCurrentTrack;
    }

    public MyArtist getArtist() {
        return mArtist;
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }


    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

}
