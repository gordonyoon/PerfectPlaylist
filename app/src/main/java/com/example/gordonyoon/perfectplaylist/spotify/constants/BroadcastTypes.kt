package com.example.gordonyoon.perfectplaylist.spotify.constants

object BroadcastTypes {
    const val SPOTIFY_PACKAGE        = "com.spotify.music"
    const val PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged"
    const val QUEUE_CHANGED          = SPOTIFY_PACKAGE + ".queuechanged"
    const val METADATA_CHANGED       = SPOTIFY_PACKAGE + ".metadatachanged"

    const val SPOTIFY_WIDGET = "com.spotify.mobile.android.ui.widget"
    const val WIDGET_NEXT    = SPOTIFY_WIDGET + ".NEXT"
}
