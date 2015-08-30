package net.cchevalier.adnd.spotifystreamer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;
import net.cchevalier.adnd.spotifystreamer.utils.Constants;

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

    // Tracks settings
    private MyArtist mArtist;
    private ArrayList<MyTrack> mTracks = null;
    private int mTrackNumber = -1;
    private MyTrack mCurrentTrack;

    private MediaPlayer mMediaPlayer = null;


    private final IBinder mPlayerBind = new PlayerBinder();

    private static final int NOTIFICATION_ID = 1;



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

        if (intent.getAction() == Constants.ACTION_START) {
            mArtist = intent.getParcelableExtra(Constants.EXTRA_ARTIST);
            mTracks = intent.getParcelableArrayListExtra(Constants.EXTRA_TRACKS);
            mTrackNumber = intent.getIntExtra(Constants.EXTRA_TRACK_NB, 0);
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

        //Toast.makeText(getApplicationContext(), "Now playing..." + mCurrentTrack.name, Toast.LENGTH_SHORT).show();

        sendBroadcastInfo(Constants.PS_NEW_SONG_STARTED);


        // Create a notification area that get user back to the PlayerActivity (UI)

        // Intents
        final Intent notificationIntent = new Intent(getApplicationContext(), PlayerActivity.class);
        notificationIntent.setAction(Constants.ACTION_DISPLAY_PLAYER);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        PendingIntent prevPendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        PendingIntent pausePendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        PendingIntent nextPendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        // Building base notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .setContentTitle(mCurrentTrack.name)
                .setContentText(mArtist.name + " / " + mCurrentTrack.album)
                .setContentIntent(pendingIntent);

        final Notification notification = mBuilder.build();

        //
        // RemoteViews for (normal) contentView
        //
        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_player);

        remoteViews.setImageViewResource(R.id.notification_album_art, android.R.drawable.ic_menu_help);
        Picasso.with(this)
                .load(mCurrentTrack.UrlMediumImage)
                .into(remoteViews, R.id.notification_album_art, NOTIFICATION_ID, notification);

        remoteViews.setTextViewText(R.id.notification_track_name, mCurrentTrack.name);
        remoteViews.setTextColor(R.id.notification_track_name, getResources().getColor(android.R.color.black));
        remoteViews.setTextViewText(R.id.notification_artist_name, mArtist.name);
        remoteViews.setTextColor(R.id.notification_artist_name, getResources().getColor(android.R.color.black));

        notification.contentView = remoteViews;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showControlsOnNotification = prefs.getBoolean(getString(R.string.pref_controls_key), true);

        if (showControlsOnNotification) {
            //
            // RemoteViews for (big) contentView
            //
            final RemoteViews bigRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_player_big);
            bigRemoteViews.setImageViewResource(R.id.notification_album_art, android.R.drawable.ic_menu_help);
            Picasso.with(this)
                    .load(mCurrentTrack.UrlMediumImage)
                    .into(bigRemoteViews, R.id.notification_album_art, NOTIFICATION_ID, notification);

            bigRemoteViews.setTextViewText(R.id.notification_track_name, mCurrentTrack.name);
            bigRemoteViews.setTextColor(R.id.notification_track_name, getResources().getColor(android.R.color.black));
            bigRemoteViews.setTextViewText(R.id.notification_artist_name, mArtist.name);
            bigRemoteViews.setTextColor(R.id.notification_artist_name, getResources().getColor(android.R.color.black));
            bigRemoteViews.setTextViewText(R.id.notification_album_name, mCurrentTrack.album);
            bigRemoteViews.setTextColor(R.id.notification_album_name, getResources().getColor(android.R.color.black));

            notification.bigContentView = bigRemoteViews;
        }


        // Put this Service in a foreground state, so it won't
        // readily be killed by the system
        startForeground(NOTIFICATION_ID, notification);



        // Following would work only for And L 5.0 and above...
/*
        Notification altNotification = new Notification.Builder(getApplicationContext())
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.ic_media_play)
                        // Add media control buttons that invoke intents in your media service
                .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent) // #0
                .addAction(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent)  // #1
                .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)     // #2
                        // Apply the media style template
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(1))
                .setContentTitle(mCurrentTrack.name)
                .setContentText(mArtist.name + " / " + mCurrentTrack.album)
                .build();
        startForeground(NOTIFICATION_ID, altNotification);
*/

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

            sendBroadcastInfo(Constants.PS_LAST_SONG_COMPLETED);

            stopSelf();
        }
    }


    private void sendBroadcastInfo(String info) {
        Log.d(TAG, "sendBroadcastInfo " + info);
        Intent broadcastInfoIntent = new Intent();
        broadcastInfoIntent.setAction(info);

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastInfoIntent);

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

    public void playNext(){
        if (mTrackNumber < mTracks.size() - 1){
            mTrackNumber++;
            playTrack();
        }
    }

    public void playPrevious(){
        if (mTrackNumber > 0){
            mTrackNumber--;
            playTrack();
        }
    }

    public void pause() {
        mMediaPlayer.pause();
    }


    public void resumePlay() {
        mMediaPlayer.start();
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

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
