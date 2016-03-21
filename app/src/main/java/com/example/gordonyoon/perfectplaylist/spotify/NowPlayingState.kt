package com.example.gordonyoon.perfectplaylist.spotify

import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver.NowPlayingTrack
import com.example.gordonyoon.perfectplaylist.ui.OnNowPlayingChangeListener
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class NowPlayingState {

    lateinit var bus: RxBus
    lateinit var widgetController: SpotifyWidgetController

    var listener: OnNowPlayingChangeListener? = null

    var nowPlaying: NowPlayingTrack by Delegates.observable(NowPlayingTrack()) {
        prop, old, new ->
        if (listener != null) {
            listener?.updateUi(new.name, new.artist)
        }
    }

    @Inject
    constructor(bus: RxBus, widgetController: SpotifyWidgetController) {
        this.bus = bus
        this.widgetController = widgetController

        bus.toObserverable().subscribe {
            if (it is NowPlayingTrack) {
                nowPlaying = it
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
    }

    fun unregister() {
        this.listener = null
    }
}