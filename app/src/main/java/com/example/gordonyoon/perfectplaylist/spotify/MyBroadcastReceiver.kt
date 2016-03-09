package com.example.gordonyoon.perfectplaylist.spotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class MyBroadcastReceiver: BroadcastReceiver() {
    object BroadcastTypes {
        const val SPOTIFY_PACKAGE = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged"
        const val QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged"
        const val METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This is sent with all broadcasts, regardless of type. The value is taken from
        // System.currentTimeMillis(), which you can compare to in order to determine how
        // old the event is.
        val timeSentInMs = intent.getLongExtra("timeSent", 0L)

        val action = intent.action
        if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
            val trackId = intent.getStringExtra("id")
            val artistName = intent.getStringExtra("artist")
            val albumName = intent.getStringExtra("album")
            val trackName = intent.getStringExtra("track")
            val trackLengthInSec = intent.getIntExtra("length", 0)
            Timber.d("trackId: $trackId")
            Timber.d("artistName: $artistName")
            Timber.d("albumName: $albumName")
            Timber.d("trackName: $trackName")
            Timber.d("trackLengthInSec: $trackLengthInSec")
            // Do something with extracted information...
        } else if (action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            val playing = intent.getBooleanExtra("playing", false)
            val positionInMs = intent.getIntExtra("playbackPosition", 0)
            Timber.d("playing: $playing")
            Timber.d("positionInMs: $positionInMs")
            // Do something with extracted information
        } else if (action.equals(BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
            Timber.d("Queue changed")
        }
    }
}