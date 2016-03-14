package com.example.gordonyoon.perfectplaylist.spotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gordonyoon.perfectplaylist.androidextentions.getAppContext
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import timber.log.Timber
import javax.inject.Inject

class NowPlayingReceiver : BroadcastReceiver() {

    @Inject lateinit var bus: RxBus

    override fun onReceive(context: Context, intent: Intent) {
        context.getAppContext().spotifyComponent.inject(this)
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
            bus.send(NowPlayingTrack(trackId, artistName, albumName, trackName, trackLengthInSec))
        } else if (action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            val playing = intent.getBooleanExtra("playing", false)
            val positionInMs = intent.getIntExtra("playbackPosition", 0)
            bus.send(NowPlayingStateChange(playing, positionInMs))
        } else if (action.equals(BroadcastTypes.QUEUE_CHANGED)) {
            bus.send(NowPlayingQueueChange())
        }
    }

    object BroadcastTypes {
        const val SPOTIFY_PACKAGE = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged"
        const val QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged"
        const val METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged"
    }

    data class NowPlayingTrack(val id: String, val artist: String, val album: String, val track: String, val length: Int)

    data class NowPlayingStateChange(val playing: Boolean, val playbackPosition: Int)

    class NowPlayingQueueChange()
}