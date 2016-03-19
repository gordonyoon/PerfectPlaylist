package com.example.gordonyoon.perfectplaylist.spotify

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.example.gordonyoon.perfectplaylist.App
import com.example.gordonyoon.perfectplaylist.spotify.constants.BroadcastTypes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyWidgetController {

    lateinit var context: App

    @Inject
    constructor(context: App) {
        this.context = context
    }

    fun nextTrack() {
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_NEXT))
    }

    fun prevTrack() {
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_PREV))
    }

    fun playTrack() {
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_PLAY))
    }

    fun isRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val spotify = activityManager.getRunningServices(Integer.MAX_VALUE)
                .find { it.process == BroadcastTypes.SPOTIFY_PACKAGE }
        return spotify != null
    }
}