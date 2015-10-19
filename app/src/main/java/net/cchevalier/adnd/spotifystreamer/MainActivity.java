package net.cchevalier.adnd.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.cchevalier.adnd.spotifystreamer.fragments.ArtistFragment;
import net.cchevalier.adnd.spotifystreamer.fragments.PlayerFragment;
import net.cchevalier.adnd.spotifystreamer.fragments.TracksFragment;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;
import net.cchevalier.adnd.spotifystreamer.utils.Constants;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements ArtistFragment.Callbacks, TracksFragment.Callbacks {

    private final String TAG = "MAIN";

    private MenuItem menuNowPlaying;
    private MenuItem menuShare;

    private ShareActionProvider mShareActionProvider;

    private boolean mUiTablet;

    // PlayerService stuff
    private PlayerService mPlayerService;
    private boolean mPlayerServiceBound;
    private ServiceConnection playerServiceConnection = new ServiceConnection() {
        /*
        Interface for monitoring the state of an application service
        */

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected ");

            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder)service;
            mPlayerService = binder.getService();
            mPlayerServiceBound = true;
            if (menuNowPlaying != null) {
                updateOptionsMenu();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected ");
            mPlayerServiceBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate start");

        super.onCreate(savedInstanceState);

        // Set default values for Preferences at first launch
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Get Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String country_code = prefs.getString(getString(R.string.pref_country_key), "unknown");
        Log.d(TAG, "onCreate: country = " + country_code);

        // Connect to PlayerService
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        playerServiceIntent.setAction(Constants.ACTION_CONNECT);
        Log.d(TAG, "onCreate: call to bindService ");
        bindService(playerServiceIntent, playerServiceConnection, BIND_AUTO_CREATE);

        //
        setContentView(R.layout.activity_main);

        //
        mUiTablet = findViewById(R.id.container_fragment_tracks) != null;
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart ");
        super.onStart();
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume ");
        super.onResume();

        // Broadcast Receiver of Player Service Messages
        IntentFilter broadcastIntentFilter = new IntentFilter();
        broadcastIntentFilter.addAction(Constants.PS_LAST_TRACK_COMPLETED);
        broadcastIntentFilter.addAction(Constants.PS_NEW_TRACK_STARTED);
        broadcastIntentFilter.addAction(Constants.PS_TRACK_PAUSE);
        broadcastIntentFilter.addAction(Constants.PS_TRACK_RESUME);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, broadcastIntentFilter);
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "onPause ");
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop ");
        super.onStop();
    }


    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart ");
        super.onRestart();
        updateOptionsMenu();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy ");

        unbindService(playerServiceConnection);
        mPlayerService = null;

        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu ");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get Now Playing MenuItem
        menuNowPlaying = menu.findItem(R.id.action_now_playing);

        // Get Share MenuItem
        menuShare = menu.findItem(R.id.action_share);

        // Get ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);

        updateOptionsMenu();

        return true;
    }

    private void updateOptionsMenu() {
        Log.d(TAG, "updateOptionsMenu");

        if (mPlayerServiceBound & mPlayerService != null) {
            if (mPlayerService.isPlaying()) {
                menuNowPlaying.setVisible(true);
                menuShare.setVisible(true);
                updateShareTrackIntent();
            } else {
                menuNowPlaying.setVisible(false);
                menuShare.setVisible(false);
            }
        }
    }


    private void updateShareTrackIntent(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Now playing: " + mPlayerService.getCurrentTrack().name);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         /*
         Handle action bar item clicks here. The action bar will
         automatically handle clicks on the Home/Up button, so long
         as you specify a parent activity in AndroidManifest.xml.
         */

        Log.d(TAG, "onOptionsItemSelected ");

        switch (item.getItemId()) {

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_share:
                Toast.makeText(this, "Share on the way...", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_now_playing:
                if (mPlayerService.isPlaying()) {
                    if (mUiTablet) {
                        PlayerFragment playerFragment = new PlayerFragment();
                        playerFragment.show(getFragmentManager(), "dialog");
                    } else {
                        final Intent displayPlayerIntent = new Intent(this, PlayerActivity.class);
                        displayPlayerIntent.setAction(Constants.ACTION_DISPLAY_PLAYER);
                        startActivity(displayPlayerIntent);
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateOptionsMenu();
            String info = intent.getAction();
            Log.d(TAG, "onReceive " + info);

            if (info.equals(Constants.PS_NEW_TRACK_STARTED)) {
                Toast.makeText(context, "MAIN: New Track started", Toast.LENGTH_SHORT).show();
            }
            else if (info.equals(Constants.PS_LAST_TRACK_COMPLETED)) {
                Toast.makeText(context, "MAIN:Last Track completed", Toast.LENGTH_SHORT).show();
            }
            else if (info.equals(Constants.PS_TRACK_PAUSE)){
                Toast.makeText(context, "MAIN:Current Track paused", Toast.LENGTH_SHORT).show();
            }
            else if (info.equals(Constants.PS_TRACK_RESUME)) {
                Toast.makeText(context, "Current Track resumed", Toast.LENGTH_SHORT).show();
            }

        }
    };


    /* ArtistFragment.Callbacks */
    @Override
    public void onArtistSelected(MyArtist selectedArtist) {
        Log.d(TAG, "onArtistSelected ");

        if (mUiTablet) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(Constants.EXTRA_IS_TABLET, mUiTablet);
            arguments.putParcelable(Constants.EXTRA_ARTIST, selectedArtist);

            TracksFragment tracksFragment = new TracksFragment();
            tracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_tracks, tracksFragment)
                    .commit();
        } else {
            // Start TracksActivity
            Intent intent = new Intent(this, TracksActivity.class);
            intent.putExtra(Constants.EXTRA_IS_TABLET, mUiTablet);
            intent.putExtra(Constants.EXTRA_ARTIST, selectedArtist);

            startActivity(intent);
        }
    }



    /* TracksFragment.Callbacks */
    @Override
    public void onTrackSelected(MyArtist selectedArtist, ArrayList<MyTrack> TracksFound, int position) {
        Log.d(TAG, "onTrackSelected ");

        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        playerServiceIntent.setAction(Constants.ACTION_START);
        playerServiceIntent.putExtra(Constants.EXTRA_ARTIST, selectedArtist);
        playerServiceIntent.putParcelableArrayListExtra(Constants.EXTRA_TRACKS, TracksFound);
        playerServiceIntent.putExtra(Constants.EXTRA_TRACK_NB, position);

        startService(playerServiceIntent);

        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.show(getFragmentManager(), "dialog");
    }
}
