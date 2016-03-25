package com.example.gordonyoon.perfectplaylist.spotify

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.example.gordonyoon.perfectplaylist.App
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.spotify.constants.BroadcastTypes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyWidgetController {

    lateinit var context: App
    lateinit var bus: RxBus

    @Inject
    constructor(context: App, bus: RxBus) {
        this.context = context
        this.bus = bus
    }

    fun nextTrack() {
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_NEXT))
    }

    fun prevTrack(sendEvent: Boolean = false) {
        if (sendEvent)
            bus.send(PreviousTrack())
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_PREV))
    }

    fun playTrack() {
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_PLAY))
    }

    fun ping() {
        nextTrack()
        prevTrack()
    }

    fun isRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val spotify = activityManager.getRunningServices(Integer.MAX_VALUE)
                .find { it.process == BroadcastTypes.SPOTIFY_PACKAGE }
        return spotify != null
    }

    class PreviousTrack
}