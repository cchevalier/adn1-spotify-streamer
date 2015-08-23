package net.cchevalier.adnd.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

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

    private final String TAG = "PLAY_SERV";

    private MediaPlayer mMediaPlayer = null;

    private final IBinder mPlayerBind = new PlayerBinder();

    // Tracks settings
    private ArrayList<MyTrack> mTracks = null;
    private int trackNumber = -1;


    /*
    * PlayerBinder
    * */
    public class PlayerBinder extends Binder {

        public PlayerService getService() {
            Log.d(TAG, "getService ");
            return PlayerService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind ");
        return mPlayerBind;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind ");
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }


    /*
    *
    * */
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate ");
        super.onCreate();

        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }


    /*
    * Required MediaPlayer Listeners
    * */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared ");
        mp.start();
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
    }




    public void initMusicPlayer() {
        Log.d(TAG, "initMusicPlayer ");
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }


    public void playTrack() {
        Log.d(TAG, "playTrack ");

        MyTrack currentTrack = mTracks.get(trackNumber);

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(currentTrack.preview_url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PLAYER SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }


    public void pauseMediaPlayer() {
        mMediaPlayer.pause();
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
        this.trackNumber = number;
    }

    public int getTrackNumber() {
        return trackNumber;
    }


    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }


    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

}
