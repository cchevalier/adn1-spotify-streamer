package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.cchevalier.adnd.spotifystreamer.fragments.TracksFragment;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.utils.Constants;


public class TracksActivity extends AppCompatActivity {

    private final String TAG = "TRACKS_ACT";

    private boolean mTwoPane = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        String artistName = "";

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.EXTRA_ARTIST)) {

            MyArtist artist = intent.getParcelableExtra(Constants.EXTRA_ARTIST);
            artistName = artist.name;

            Bundle arguments = new Bundle();
            arguments.putBoolean(Constants.EXTRA_IS_TABLET, mTwoPane);
            arguments.putParcelable(Constants.EXTRA_ARTIST, artist);

            TracksFragment tracksFragment = new TracksFragment();
            tracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_fragment_tracks, tracksFragment)
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(artistName);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
