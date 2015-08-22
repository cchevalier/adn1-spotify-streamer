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


    private MediaPlayer mMediaPlayer = null;
    private final IBinder mPlayerBind = new PlayerBinder();

    private ArrayList<MyTrack> mTracks = null;
    private int mPosition = -1;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mPlayerBind;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }


    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }


    public void setTracksList(ArrayList<MyTrack> theTracks) {
        mTracks = theTracks;
    }


    public void playTrack() {

        MyTrack currentTrack = mTracks.get(mPosition);

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(currentTrack.preview_url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PLAYER SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }


    public void setTrack(int position) {
        mPosition = position;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }


    public class PlayerBinder extends Binder {

        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
