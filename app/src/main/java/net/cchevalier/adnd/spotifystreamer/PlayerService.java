package net.cchevalier.adnd.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.util.ArrayList;

/**
 * Created by cch on 19/08/2015.
 */
public class PlayerService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mMediaPlayer = null;

    private ArrayList<MyTrack> myTracks = null;
    private int mPosition = -1;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
