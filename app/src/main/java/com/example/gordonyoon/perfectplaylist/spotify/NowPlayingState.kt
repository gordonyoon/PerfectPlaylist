package com.example.gordonyoon.perfectplaylist.spotify

import com.example.gordonyoon.perfectplaylist.extensions.runAndClose
import com.example.gordonyoon.perfectplaylist.models.PlaylistTransaction
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.ui.OnNowPlayingChangeListener
import io.realm.Realm
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class NowPlayingState {

    var listener: OnNowPlayingChangeListener? = null

    var nowPlaying: NowPlayingReceiver.NowPlayingTrack by Delegates.observable(NowPlayingReceiver.NowPlayingTrack()) {
        prop, old, new ->
        if (listener != null) {
            (listener as OnNowPlayingChangeListener).updateUi(new.name, new.artist)

            Realm.getDefaultInstance().runAndClose {
                copyToRealmOrUpdate(PlaylistTransaction(new))
            }

//            val realm = Realm.getDefaultInstance()
//            realm.beginTransaction()
//            realm.copyToRealmOrUpdate(PlaylistTransaction(new))
//            realm.commitTransaction()
//            realm.close()
        }
    }

    @Inject lateinit var bus:   RxBus

    @Inject
    constructor(bus: RxBus) {
        this.bus = bus

        bus.toObserverable().subscribe {
            if (it is NowPlayingReceiver.NowPlayingTrack) {
                nowPlaying = it
            }
        }
    }

    fun register(listener: OnNowPlayingChangeListener) {
        this.listener = listener
        if (!nowPlaying.isEmpty()) {
            (this.listener as OnNowPlayingChangeListener).updateUi(nowPlaying.name, nowPlaying.artist)
        }
    }

    fun unregister() {
        this.listener = null
    }
}