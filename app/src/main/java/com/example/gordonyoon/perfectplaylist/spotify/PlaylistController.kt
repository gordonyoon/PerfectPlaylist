package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.extensions.addTracksToPlaylist
import com.example.gordonyoon.perfectplaylist.extensions.getLatestAddDate
import com.example.gordonyoon.perfectplaylist.extensions.getNewTracks
import com.example.gordonyoon.perfectplaylist.extensions.getPpTempId
import kaaes.spotify.webapi.android.SpotifyApi
import org.jetbrains.anko.async
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

@PerActivity
class PlaylistController() {

    lateinit var context: Activity
    lateinit var api: SpotifyApi
    lateinit var transactionManager: TransactionManager

    @Inject
    constructor(context: Activity, api: SpotifyApi, transactionManager: TransactionManager) : this() {
        this.context = context
        this.api = api
        this.transactionManager = transactionManager
    }

    fun nowPlayingSave(track: NowPlayingReceiver.NowPlayingTrack) {
        if (track.isEmpty()) {
            context.toast("Play a song!")
            return
        }
        transactionManager.save(track)
    }

    fun nowPlayingDelete(track: NowPlayingReceiver.NowPlayingTrack) {
        if (track.isEmpty()) {
            context.toast("Play a song!")
            return
        }
        transactionManager.remove(track)
    }

    fun refresh() {
        val spotify = api.service
        async() {
            val myId          = spotify.me.id
            val ppTempId      = spotify.getPpTempId(myId)
            val latestAddDate = spotify.getLatestAddDate(myId, ppTempId)
            val tracks        = spotify.getNewTracks(myId, ppTempId, latestAddDate)

            if (!tracks.isEmpty()) {
                spotify.addTracksToPlaylist(myId, ppTempId, tracks.map { it.uri })
            }

            uiThread {
                if (!tracks.isEmpty()) {
                    context.toast("Now adding ${tracks.size} new track${if (tracks.size > 1) "s" else ""}")
                } else {
                    context.toast("You're all up to date!")
                }
            }
        }
    }
}