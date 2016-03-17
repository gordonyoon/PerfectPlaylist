package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.extensions.*
import com.example.gordonyoon.perfectplaylist.models.PlaylistTransaction
import com.example.gordonyoon.perfectplaylist.spotify.constants.BroadcastTypes
import io.realm.Realm
import kaaes.spotify.webapi.android.SpotifyApi
import org.jetbrains.anko.async
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
        val transaction = PlaylistTransaction(track).apply { save = true }

        val realm = Realm.getDefaultInstance().apply {
            beginTransaction()
            copyToRealmOrUpdate(transaction)
        }

        Snackbar.make(context.window.decorView.rootView, "SAVED: ${track.name}", Snackbar.LENGTH_LONG)
                .setAction("UNDO", { realm.cancelTransaction() })
                .setCallback(object: Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar?, event: Int) {
                        super.onDismissed(snackbar, event)
                        if (event != DISMISS_EVENT_ACTION) {
                            realm.apply {
                                commitTransaction()
                                close()
                            }
                        }
                        sync()
                    }
                })
                .show()
    }

    fun remove(track: NowPlayingReceiver.NowPlayingTrack) {
        val transaction = PlaylistTransaction(track).apply { remove = true }

        val realm = Realm.getDefaultInstance().apply {
            beginTransaction()
            copyToRealmOrUpdate(transaction)
        }

        Snackbar.make(context.window.decorView.rootView, "REMOVED: ${track.name}", Snackbar.LENGTH_LONG)
                .setAction("UNDO", { realm.cancelTransaction() })
                .setCallback(object: Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar?, event: Int) {
                        super.onDismissed(snackbar, event)
                        if (event != DISMISS_EVENT_ACTION) {
                            realm.apply {
                                commitTransaction()
                                close()
                            }
                        } else {
                            nextTrack(context)
                        }
                        sync()
                    }
                })
                .show()
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

    fun nextTrack(context: Context) {
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_NEXT))
    }
}