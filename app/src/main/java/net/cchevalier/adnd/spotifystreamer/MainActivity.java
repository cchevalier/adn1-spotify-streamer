package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.cchevalier.adnd.spotifystreamer.fragments.ArtistFragment;
import net.cchevalier.adnd.spotifystreamer.fragments.PlayerFragment;
import net.cchevalier.adnd.spotifystreamer.fragments.TracksFragment;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;
import net.cchevalier.adnd.spotifystreamer.utils.Constants;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements ArtistFragment.Callbacks, TracksFragment.Callbacks {

    private final String TAG = "MAIN_ACT";

    private boolean mUiTablet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container_fragment_tracks) != null) {
            mUiTablet = true;
        } else {
            mUiTablet = false;
        }
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
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "onPause ");

        super.onPause();
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
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy ");

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu ");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected ");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    /* ArtistFragment.Callbacks */
    @Override
    public void onArtistSelected(MyArtist selectedArtist) {
        Log.d(TAG, "onArtistSelected ");


        if (mUiTablet) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(Constants.KEY_TABLET, mUiTablet);
            arguments.putParcelable(Constants.EXTRA_ARTIST, selectedArtist);

            TracksFragment tracksFragment = new TracksFragment();
            tracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_tracks, tracksFragment)
                    .commit();
        } else {
            // Start TracksActivity
            Intent intent = new Intent(this, TracksActivity.class);
            intent.putExtra(Constants.KEY_TABLET, mUiTablet);
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

/*
        Bundle arguments = new Bundle();
        arguments.putParcelable(TracksFragment.KEY_ARTIST_SELECTED, selectedArtist);
        arguments.putParcelableArrayList(TracksFragment.KEY_TRACKS_FOUND, TracksFound);
        arguments.putInt(TracksFragment.KEY_POSITION, position);
        playerFragment.setArguments(arguments);
*/

        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.show(getFragmentManager(), "dialog");
    }
}
