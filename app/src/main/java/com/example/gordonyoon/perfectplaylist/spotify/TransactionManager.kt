package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.extensions.*
import com.example.gordonyoon.perfectplaylist.models.PlaylistTransaction
import io.realm.Realm
import kaaes.spotify.webapi.android.SpotifyApi
import org.jetbrains.anko.async
import org.jetbrains.anko.toast
import javax.inject.Inject

@PerActivity
class TransactionManager {

    lateinit var context: Activity
    lateinit var api: SpotifyApi

    @Inject
    constructor(context: Activity, api: SpotifyApi) {
        this.context = context
        this.api = api
    }

    fun save(track: NowPlayingReceiver.NowPlayingTrack) {
        Realm.getDefaultInstance().runAndClose {
            val transaction = PlaylistTransaction(track).apply { save = true }
            copyToRealmOrUpdate(transaction)
        }
        context.toast("1 track saved: ${track.name}")
        sync()
    }

    fun remove(track: NowPlayingReceiver.NowPlayingTrack) {
        Realm.getDefaultInstance().runAndClose {
            val transaction = PlaylistTransaction(track).apply { remove = true }
            copyToRealmOrUpdate(transaction)
        }
        context.toast("1 track removed: ${track.name}")
        sync()
    }

    fun sync() {
        if (context.hasInternetConnection()) {
            val transactions = Realm.getDefaultInstance().where(PlaylistTransaction::class.java).findAll()
            val (save, remove) = transactions.partition { it.save.xor(it.remove) && it.save }
            saveBatch(save.map { it.toNowPlayingTrack() })
            removeBatch(remove.map { it.toNowPlayingTrack() })
            Realm.getDefaultInstance().runAndClose {
                clear(PlaylistTransaction::class.java)
            }
        }
    }

    private fun saveBatch(tracks: List<NowPlayingReceiver.NowPlayingTrack>) {
        if (tracks.isEmpty()) return

        val spotify = api.service
        async() {
            val myId      = spotify.me.id
            val ppTempId  = spotify.getPpTempId(myId)
            val ppFinalId = spotify.getPpFinalId(myId)

            val uris = tracks.map { it.uri }
            val ids  = uris.map { it.removePrefix("spotify:track:") }

            spotify.addTracksToMyLibrary(ids)
            spotify.addDistinctTracksToPlaylist(myId, ppFinalId, uris)
            spotify.removeTracksFromPlaylist(myId, ppTempId, uris)
        }
    }

    private fun removeBatch(tracks: List<NowPlayingReceiver.NowPlayingTrack>) {
        if (tracks.isEmpty()) return

        val spotify = api.service
        async() {
            val myId     = spotify.me.id
            val ppTempId = spotify.getPpTempId(myId)

            spotify.removeTracksFromPlaylist(myId, ppTempId, tracks.map { it.uri })
        }
    }
}