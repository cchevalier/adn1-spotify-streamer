# Spotify Streamer

ADnd-P1-SpotifyStreamer : A music streaming application.

by CChevalier, July 2015.


## Part 2

#### References
- Tuts+
    - [Create a Music Player on Android] (http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778)



#### References (french)


#### Discussions groups
- [Udacity Discussion Forum - ADnd - P2 Spotify ](https://discussions.udacity.com/c/nd801-2015-05-28/p1-spotify-streamer-stage-2)
- [Google+ Udacity's Android Developer Nanodegree] (https://plus.google.com/communities/109766100514206800627)

---

## Part 1

## Update for submission 1.1:
Thanks to the reviewer for a fast and very valuable review of my first submission.

First submission failed for mainly two reasons:  
1. App was crashing if there was no network connection (I had completely forgotten that!)   
2. Top ten tracks queried results were not retained on rotation change (here again I knew something was weired but didn't connect the dots properly...)  


FIXES:   
1. Use try / catch statement around code fetching data with basic treatment of the Retrofit error. Also use an internal flag fetchErrorFlag to issue a specific Toast on Post Execution (see ArtistFragment.java and TracksFragment.java)   
2. Implement onSaveInstanceState in TracksFragment for tracksFound data, retrieves it in onCreateView   

Just a final remark:   
While retrieving data from savedInstanceState I do not check if the requested data type is there (I take for granted that since my app put it there then there it is...) I guess a more robust approach would be to try/catch all these get calls. I will need to investigate this more in the future but reviewer insights is welcome.  


## Original message for submission 1.0:    
As the deadline of July 13th is getting close and I don't want to restart stage 1 with the Movies App, I add to make some choices to finalize a first version for submission, see below:

### Known issues or Personal Choices

- Images art selection (artist / track) if present:
    - Medium res is targeted to closest to (200, 200)px
    - High res (not used at this stage 1) is chosen as the first one (assuming this is the highest res)
- Country code is hardcoded to "DK" for now (see TracksFragment.java)

- Rotation change:
    - is handled programmatically on the search artist fragment (via Parcelable / saveInstance state) as it wouldn't work otherwise
    - but was already functional (!) on the tracks fragment before I even finalize implementation of saveInstance state (MyTrack model implements Parcelable for that purpose)
    - therefore I have chosen to submit a first time this way, while investigating further (would pb arises with next media player fragment?)

- Misc:
    - Added popularity to artist list item view (for my own reference)


#### References
- Ryan Harter
    - [Customizing the ListView with Picasso] (http://ryanharter.com/blog/2014/02/23/layouts-and-adapters/)
- Developer Phil
    - [Parcelable vs Serializable] (http://www.developerphil.com/parcelable-vs-serializable/)
- Android Research Blog
    - [Example usage of AppCompatActivity in Android] (https://androidresearch.wordpress.com/2015/04/24/example-usage-of-appcompatactivity-in-android/)

#### References (french)
- Tutos Android France
    - [ListView] (http://tutos-android-france.com/listview-afficher-une-liste-delements/)
    - [Picasso] (http://tutos-android-france.com/picasso/)
    - [Intents / Parcelable] (http://tutos-android-france.com/passer-des-donnees-entre-activites/)

#### Discussions groups
- [Udacity Discussion Forum - ADnd - P1](https://discussions.udacity.com/c/nd801-2015-05-28/p1-spotify-streamer-stage-1)
- [Google+ Udacity's Android Developer Nanodegree] (https://plus.google.com/communities/109766100514206800627)
