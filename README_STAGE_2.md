# Spotify Streamer - Stage 2

ADnd-P1-SpotifyStreamer : A music streaming application.

by CChevalier, October 2015.

---


## Submission 2

### Optional Components

#### User Interface - Function

- App displays a “Now Playing” Button in the ActionBar that serves to reopen the player UI should the user navigate back to browse content and then want to resume control over playback.

#### Notifications

- App implements a notification with playback controls ( Play, pause , next & previous track )

- Notification media controls are usable on the lockscreen and drawer

- Notification displays track name and album art thumbnail

#### Sharing Functionality

- App adds a menu for sharing the currently playing track

- App uses a shareIntent to expose the external Spotify URL for the current track

#### Settings Menu

- App has a menu item to select the country code (which is automatically passed into the get Top Tracks query )  
  _Only 5 countries are implemented but this could be increased easily if needed_

- App has menu item to toggle showing notification controls on the drawer and lock screen  
  _This feature is implementing by removing the BigRemoteView (containing media controls) from notifications_  


   
---
   
## Submission 1
A first attempt to meet the deadline of the June cohort / Spotify Stage 2. The track playback is implemented via a service bound to the player fragment
but without implementation of status notification for this attempt. The service/player also uses broadcast feature to signal the end of the current track.
The seekbar is implemented through a runnable/handler using the bound service to access current position of the media player.   
     
---     
     
## References

- Guides on developer.android.com
    - [Guide Media Playback] (http://developer.android.com/guide/topics/media/mediaplayer.html)
    - [Guide Media Playback: Using a Service with MediaPlayer] (http://developer.android.com/guide/topics/media/mediaplayer.html#mpandservices)

    - [Services] (http://developer.android.com/guide/components/services.html)
    - [Bound Services] (http://developer.android.com/guide/components/bound-services.html)
    - [Bound Services: Managing the Lifecycle of a Bound Service] (http://developer.android.com/guide/components/bound-services.html#Lifecycle)

- Tuts+
    - [Create a Music Player on Android: Project Setup] (http://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764)
    - [Create a Music Player on Android: Song Playback] (http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778)
    - [Create a Music Player on Android: User Controls] (http://code.tutsplus.com/tutorials/create-a-music-player-on-android-user-controls--mobile-22787)

- Stackoverflow
    - [SeekBar and media player in android] (http://stackoverflow.com/questions/17168215/seekbar-and-media-player-in-android)

- Intertech
    - [Using LocalBroadcastManager in Service to Activity Communications] (http://www.intertech.com/Blog/using-localbroadcastmanager-in-service-to-activity-communications/)

- Future Studio
    - [Picasso — Callbacks, RemoteViews and Notifications] (https://futurestud.io/blog/picasso-callbacks-remoteviews-and-notifications/)


#### Samples codes
- Java Code Geeks
    - [Android MediaPlayer Example] (http://examples.javacodegeeks.com/android/android-mediaplayer-example/)
- Android samples
    - [UniversalMusicPlayer] (https://github.com/googlesamples/android-UniversalMusicPlayer)
    - [RandomMusicPlayer] (https://github.com/android/platform_development/tree/master/samples/RandomMusicPlayer)


#### Discussions groups
- [Udacity Discussion Forum - ADnd - P2 Spotify ](https://discussions.udacity.com/c/nd801-p1-p2-developing-android-apps/p2-specific-questions)
- [Google+ Udacity's Android Developer Nanodegree] (https://plus.google.com/communities/109766100514206800627)
   
   