package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.cchevalier.adnd.spotifystreamer.models.MyArtist;


public class TracksActivity extends AppCompatActivity {

    static final String ARTIST_SELECTED = "artistSelected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(ARTIST_SELECTED)) {
                MyArtist artist = (MyArtist)intent.getParcelableExtra(ARTIST_SELECTED);
                actionBar.setSubtitle(artist.name);
            }

        }
    }

/*
* https://discussions.udacity.com/t/back-works-rotation-works-but-up-doesnt/21564/2
*//*

    @Override
    public boolean onSupportNavigateUp(){
        if(!super.onSupportNavigateUp()){
            finish();
        }
        return true;
    }

*/

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
            return true;
        }

/*
        // http://stackoverflow.com/questions/22182888/actionbar-up-button-destroys-parent-activity-back-does-not
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
*/

        return super.onOptionsItemSelected(item);
    }
}
