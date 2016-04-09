package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import com.example.gordonyoon.perfectplaylist.R
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.extensions.*
import com.example.gordonyoon.perfectplaylist.models.PlaylistTransaction
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver.NowPlayingTrack
import com.example.gordonyoon.perfectplaylist.spotify.constants.BroadcastTypes
import io.realm.Realm
import io.realm.RealmResults
import kaaes.spotify.webapi.android.SpotifyApi
import org.jetbrains.anko.async
import org.jetbrains.anko.toast
import javax.inject.Inject

@PerActivity
class TransactionManager {

    lateinit var context: Activity
    lateinit var api: SpotifyApi
    lateinit var authenticator: Authenticator
    lateinit var widgetController: SpotifyWidgetController

    @Inject
    constructor(context: Activity, api: SpotifyApi, authenticator: Authenticator, widgetController: SpotifyWidgetController) {
        this.context = context
        this.api = api
        this.authenticator = authenticator
        this.widgetController = widgetController
    }

    fun save(track: NowPlayingTrack) {
        if (!widgetController.isRunning()) {
            context.toast("Spotify is not running; please try again in a moment.")
            widgetController.ping()
            return
        }

        Realm.getDefaultInstance().tryCommitClose {
            val transaction = PlaylistTransaction(track).apply { save = true }
            copyToRealmOrUpdate(transaction)
        }

        Snackbar.make(context.findViewById(R.id.fab), "SAVED: ${track.name}", Snackbar.LENGTH_LONG)
                .setAction("UNDO", { reverseCommit(track) })
                .setOnDismissedNoActionCallback { sync() }
                .show()
    }

    fun remove(track: NowPlayingTrack) {
        if (!widgetController.isRunning()) {
            context.toast("Spotify is not running; please try again in a moment.")
            widgetController.ping()
            return
        }

        Realm.getDefaultInstance().tryCommitClose {
            val transaction = PlaylistTransaction(track).apply { remove = true }
            copyToRealmOrUpdate(transaction)
        }

        Snackbar.make(context.findViewById(R.id.fab), "REMOVED: ${track.name}", Snackbar.LENGTH_LONG)
                .setAction("UNDO", { reverseCommit(track) })
                .setOnDismissedNoActionCallback { sync() }
                .show()

        widgetController.nextTrack()
    }

    private fun reverseCommit(track: NowPlayingTrack) {
        val results: RealmResults<PlaylistTransaction> = Realm.getDefaultInstance()
                .where(PlaylistTransaction::class.java)
                .equalTo("uri", track.uri).findAll()
        if (results.size > 0) {
            Realm.getDefaultInstance().tryCommitClose {
                results.clear()
            }
        }
    }

    fun sync() {
        if (!context.hasInternetConnection()) return

        authenticator.authorizedApiCall(
                authorized = {
                    val transactions = Realm.getDefaultInstance().where(PlaylistTransaction::class.java).findAll()
                    val (save, remove) = transactions.partition { it.save.xor(it.remove) && it.save }
                    saveBatch(save.map { it.toNowPlayingTrack() })
                    removeBatch(remove.map { it.toNowPlayingTrack() })
                    Realm.getDefaultInstance().tryCommitClose {
                        clear(PlaylistTransaction::class.java)
                    }
                })
    }

    private fun saveBatch(tracks: List<NowPlayingTrack>) {
        if (tracks.isEmpty()) return

        val spotify = api.service
        async() {
            val myId = spotify.me.id
            val ppTempId = spotify.getPpTempId(myId)
            val ppFinalId = spotify.getPpFinalId(myId)

            val uris = tracks.map { it.uri }.distinct()
            val ids = uris.map { it.removePrefix("spotify:track:") }

            spotify.addTracksToMyLibrary(ids)
            spotify.addTracksToPlaylist(myId, ppFinalId, uris)
            spotify.removeTracksFromPlaylist(myId, ppTempId, uris)
        }
    }

    private fun removeBatch(tracks: List<NowPlayingTrack>) {
        if (tracks.isEmpty()) return

        val spotify = api.service
        async() {
            val myId = spotify.me.id
            val ppTempId = spotify.getPpTempId(myId)

            spotify.removeTracksFromPlaylist(myId, ppTempId, tracks.map { it.uri })
        }
    }
}