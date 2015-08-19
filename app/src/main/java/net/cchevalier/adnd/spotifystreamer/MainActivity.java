package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.cchevalier.adnd.spotifystreamer.fragments.ArtistFragment;
import net.cchevalier.adnd.spotifystreamer.fragments.PlayerFragment;
import net.cchevalier.adnd.spotifystreamer.fragments.TracksFragment;
import net.cchevalier.adnd.spotifystreamer.models.MyArtist;
import net.cchevalier.adnd.spotifystreamer.models.MyTrack;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ArtistFragment.Callbacks, TracksFragment.Callbacks {

    public static final String KEY_TABLET = "KEY_TABLET";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container_fragment_tracks) != null) {
            mTwoPane = true;
/*
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.tracks_detail_container, new TracksFragment())
                        .commit();
            }
*/
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onArtistSelected(MyArtist selectedArtist) {

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(KEY_TABLET, mTwoPane);
            arguments.putParcelable(ArtistFragment.KEY_ARTIST_SELECTED, selectedArtist);

            TracksFragment tracksFragment = new TracksFragment();
            tracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_tracks, tracksFragment)
                    .commit();
        } else {
            // Start TracksActivity
            Intent intent = new Intent(this, TracksActivity.class);
            intent.putExtra(KEY_TABLET, mTwoPane);
            intent.putExtra(ArtistFragment.KEY_ARTIST_SELECTED, selectedArtist);
            startActivity(intent);
        }
    }


    @Override
    public void onTrackSelected(MyArtist selectedArtist, ArrayList<MyTrack> TracksFound, int position) {

        Bundle arguments = new Bundle();
        arguments.putParcelable(TracksFragment.KEY_ARTIST_SELECTED, selectedArtist);
        arguments.putParcelableArrayList(TracksFragment.KEY_TRACKS_FOUND, TracksFound);
        arguments.putInt(TracksFragment.KEY_POSITION, position);

        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(arguments);

//        FragmentManager fragmentManager = getFragmentManager();
        playerFragment.show(getFragmentManager(), "dialog");
    }
}
