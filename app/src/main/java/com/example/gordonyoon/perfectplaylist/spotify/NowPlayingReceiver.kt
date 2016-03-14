package com.example.gordonyoon.perfectplaylist.spotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gordonyoon.perfectplaylist.androidextentions.getAppContext
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import javax.inject.Inject

class NowPlayingReceiver : BroadcastReceiver() {

    @Inject lateinit var bus: RxBus

    override fun onReceive(context: Context, intent: Intent) {
        context.getAppContext().spotifyComponent.inject(this)

        when (intent.action) {
            BroadcastTypes.METADATA_CHANGED       -> bus.send(NowPlayingTrack(intent))
            BroadcastTypes.PLAYBACK_STATE_CHANGED -> bus.send(NowPlayingStateChange(intent))
            BroadcastTypes.QUEUE_CHANGED          -> bus.send(NowPlayingQueueChange(intent))
        }
    }

    object BroadcastTypes {
        const val SPOTIFY_PACKAGE        = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged"
        const val QUEUE_CHANGED          = SPOTIFY_PACKAGE + ".queuechanged"
        const val METADATA_CHANGED       = SPOTIFY_PACKAGE + ".metadatachanged"
    }

    data class NowPlayingTrack(val id: String, val artist: String, val album: String, val track: String, val length: Int) {

        constructor(intent: Intent): this(
                intent.getStringExtra("id"),
                intent.getStringExtra("artist"),
                intent.getStringExtra("album"),
                intent.getStringExtra("track"),
                intent.getIntExtra("length", 0)
        ) {
            if (intent.action != BroadcastTypes.METADATA_CHANGED
                    || !intent.hasExtra("id")
                    || !intent.hasExtra("artist")
                    || !intent.hasExtra("album")
                    || !intent.hasExtra("track")
                    || !intent.hasExtra("length")) {
                throw IllegalArgumentException()
            }
        }
    }

    data class NowPlayingStateChange(val playing: Boolean, val playbackPosition: Int) {

        constructor(intent: Intent) : this(
                intent.getBooleanExtra("playing", false),
                intent.getIntExtra("playbackPosition", 0)
        ) {
            if (intent.action != BroadcastTypes.PLAYBACK_STATE_CHANGED
                    || !intent.hasExtra("playing")
                    || !intent.hasExtra("playbackPosition")) {
                throw IllegalArgumentException()
            }
        }
    }

    class NowPlayingQueueChange(intent: Intent) {
        init {
            if (intent.action != BroadcastTypes.PLAYBACK_STATE_CHANGED) {
                throw IllegalArgumentException()
            }
        }
    }
}