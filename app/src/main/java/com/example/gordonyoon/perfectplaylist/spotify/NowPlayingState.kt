package com.example.gordonyoon.perfectplaylist.spotify

import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver.NowPlayingStateChange
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver.NowPlayingTrack
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class NowPlayingState {

    lateinit var bus: RxBus
    lateinit var widgetController: SpotifyWidgetController

    private var nowPlayingChangeListener: OnNowPlayingChangeListener? = null
    private var nowPlayingExpiredListener: OnNowPlayingExpiredListener? = null
    private var expireTimeout: TimeoutThread? = null
    private var prevTrackTimeout: TimeoutThread? = null
    private var timedOut: Boolean = false

    var nowPlaying: NowPlayingTrack by Delegates.observable(NowPlayingTrack()) {
        prop, old, new ->
        if (!new.isEmpty()) {
            nowPlayingChangeListener?.updateUi(new.name, new.artist)
        }
        restartExpireTimeout(new.length)
        prevTrackTimeout?.interrupt()
    }

    private fun restartExpireTimeout(time: Int) {
        timedOut = false
        nowPlayingExpiredListener?.unexpire()
        expireTimeout?.interrupt()
        expireTimeout = TimeoutThread(time) {
            timedOut = true
            nowPlayingExpiredListener?.expire()
        }
        expireTimeout?.start()
    }

    @Inject
    constructor(bus: RxBus, widgetController: SpotifyWidgetController) {
        this.bus = bus
        this.widgetController = widgetController

        bus.toObserverable().subscribe {
            if (it is NowPlayingTrack) {
                this.nowPlaying = it
            } else if (it is NowPlayingStateChange) {
                if (it.playing == true && !nowPlaying.isEmpty()) {
                    restartExpireTimeout(nowPlaying.length)
                }
            } else if (it is SpotifyWidgetController.PreviousTrack) {
                prevTrackTimeout?.interrupt()
                prevTrackTimeout = TimeoutThread(500) { widgetController.prevTrack(true) }
                prevTrackTimeout?.start()
            }
        }
    }

    fun register(nowPlayingChangeListener: OnNowPlayingChangeListener,
                 nowPlayingExpiredListener: OnNowPlayingExpiredListener) {
        this.nowPlayingChangeListener = nowPlayingChangeListener
        this.nowPlayingExpiredListener = nowPlayingExpiredListener

        if (!nowPlaying.isEmpty()) {
            nowPlayingChangeListener.updateUi(nowPlaying.name, nowPlaying.artist)
        } else {
            widgetController.ping()
        }

        if (timedOut)
            nowPlayingExpiredListener.expire()
        else
            nowPlayingExpiredListener.unexpire()
    }

    fun unregister() {
        this.nowPlayingChangeListener = null
        this.nowPlayingExpiredListener = null
    }

    class TimeoutThread(val timeout: Int, val onFinishNoInterrupt: () -> Unit): Thread() {

        val STEP = 500

        override fun run() {
            for (i in 0..timeout step STEP) {
                try {
                    sleep(STEP.toLong())
                } catch (e: InterruptedException) {
                    return
                }
            }
            AndroidSchedulers.mainThread().createWorker().schedule {
                onFinishNoInterrupt()
            }
        }
    }

    interface OnNowPlayingChangeListener {
        fun updateUi(trackTitle: String, artistName: String)
    }

    interface OnNowPlayingExpiredListener {
        fun expire()

        fun unexpire()
    }
}