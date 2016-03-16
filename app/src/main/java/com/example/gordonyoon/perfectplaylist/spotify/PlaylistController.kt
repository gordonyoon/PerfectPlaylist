package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.spotify.constants.BroadcastTypes
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.models.TrackToRemove
import kaaes.spotify.webapi.android.models.TracksToRemove
import org.jetbrains.anko.async
import org.jetbrains.anko.toast
import javax.inject.Inject

@PerActivity
class PlaylistController() {

    lateinit var context: Activity

    @Inject lateinit var api: SpotifyApi

    @Inject
    constructor(context: Activity) : this() {
        this.context = context
    }

    fun nowPlayingSave(track: NowPlayingReceiver.NowPlayingTrack?) {
        if (track == null) {
            context.toast("Play a song!")
            return
        }

        val spotify = api.service
        async() {
            val myId      = spotify.me.id
            val ppTempId  = spotify.getPpTempId(myId)
            val ppFinalId = spotify.getPpFinalId(myId)

            val uri = track.uri
            val id  = uri.removePrefix("spotify:track:")

            spotify.addToMySavedTracks(id)

            if (!spotify.playlistContainsTrack(myId, ppFinalId, uri)) {
                spotify.addTrackToPlaylist(myId, ppFinalId, uri)
                context.toast("1 track saved: ${track.name}")
            } else {
                context.toast("${track.name} is already saved!")
            }

            spotify.removeTrackFromPlaylist(myId, ppTempId, uri)
        }
    }

    fun nowPlayingDelete(track: NowPlayingReceiver.NowPlayingTrack?) {
        if (track == null) {
            context.toast("Play a song!")
            return
        }

        val spotify = api.service
        async() {
            val myId     = spotify.me.id
            val ppTempId = spotify.getPpTempId(myId)

            spotify.removeTrackFromPlaylist(myId, ppTempId, track.uri)
            context.toast("1 track removed: ${track.name}")

            nextTrack(context)
        }
    }

    fun refresh() {
        val spotify = api.service
        async() {
            val myId          = spotify.me.id
            val ppTempId      = spotify.getPpTempId(myId)
            val latestAddDate = spotify.getLatestAddDate(myId, ppTempId)
            val tracks        = spotify.getNewTracks(myId, ppTempId, latestAddDate)

            if (!tracks.isEmpty()) {
                spotify.addTracksToPlaylist(myId, ppTempId, tracks)
                context.toast("Now adding ${tracks.size} new track${if (tracks.size > 1) "s" else ""}")
            } else {
                context.toast("You're all up to date!")
            }
        }
    }

    fun nextTrack(context: Context) {
        context.sendBroadcast(Intent(BroadcastTypes.WIDGET_NEXT))
    }
}