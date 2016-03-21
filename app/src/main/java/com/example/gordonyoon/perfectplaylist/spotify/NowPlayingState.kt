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

    private var listener: OnNowPlayingChangeListener? = null
    private var timeoutThread: TimeoutThread? = null
    private var timedOut: Boolean = false

    var nowPlaying: NowPlayingTrack by Delegates.observable(NowPlayingTrack()) {
        prop, old, new ->
        listener?.updateUi(new.name, new.artist)
        restartTimeout(new.length)
    }

    private fun restartTimeout(time: Int) {
        timedOut = false
        listener?.nowPlayingStart()
        timeoutThread?.interrupt()
        timeoutThread = TimeoutThread(time) {
            timedOut = true
            listener?.nowPlayingExpire()
        }
        timeoutThread?.start()
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
                    restartTimeout(nowPlaying.length)
                }
            }
        }
    }

    fun register(listener: OnNowPlayingChangeListener) {
        this.listener = listener
        if (!nowPlaying.isEmpty()) {
            listener.updateUi(nowPlaying.name, nowPlaying.artist)
        } else {
            widgetController.ping()
        }

        if (timedOut)
            listener.nowPlayingExpire()
        else
            listener.nowPlayingStart()
    }

    fun unregister() {
        this.listener = null
    }

    class TimeoutThread(val timeout: Int, val onFinishNoInterrupt: () -> Unit): Thread() {

        val STEP = 4000

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

        fun nowPlayingExpire()

        fun nowPlayingStart()
    }
}