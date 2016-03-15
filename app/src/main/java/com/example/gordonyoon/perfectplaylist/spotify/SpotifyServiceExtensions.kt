package com.example.gordonyoon.perfectplaylist.spotify

import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.TrackToRemove
import kaaes.spotify.webapi.android.models.TracksToRemove
import org.jetbrains.anko.async
import timber.log.Timber

fun SpotifyService.updatePPTemp(): Unit {
    async() {
        val myId: String = me.id
        val ppTempId: String = getPpTempId(myId)
        val latestAddDate = getLatestAddDate(myId, ppTempId)
        Timber.d("The last track was added on $latestAddDate")
        val tracks = getNewTracks(myId, ppTempId, latestAddDate)
        if (!tracks.isEmpty()) {
            Timber.d("Now adding ${tracks.size} new tracks!\n${tracks.map { it.name }}")
            addTracksToPlaylist(myId, ppTempId, tracks)
        } else {
            Timber.d("No new tracks were found!")
        }
    }
}

fun SpotifyService.removeNowPlaying(trackUri: String?): Unit {
    if (trackUri == null) {
        Timber.d("Did not capture track data.")
        return
    }

    async() {
        val myId     = me.id
        val ppTempId = getPpTempId(myId)

        if (playlistContainsTrack(myId, ppTempId, trackUri)) {
            val trackToRemove  = TrackToRemove().apply { this.uri = trackUri }
            val tracksToRemove = TracksToRemove().apply { tracks = listOf(trackToRemove) }

            removeTracksFromPlaylist(myId, getPpTempId(myId), tracksToRemove)
        }
    }
}

fun SpotifyService.moveNowPlayingToPPFinal(trackUri: String?): Unit {
    if (trackUri == null) {
        Timber.d("Did not capture track data.")
        return
    }

    async() {
        val myId      = me.id
        val ppFinalId = getPpFinalId(myId)

        removeNowPlaying(trackUri)
        addToMySavedTracks(trackUri.removePrefix("spotify:track:"))
        if (!playlistContainsTrack(myId, ppFinalId, trackUri)) {
            addTracksToPlaylist(myId, ppFinalId, null, mapOf("uris" to listOf(trackUri)))
            Timber.d("Go check Perfect Playlist - Temp!")
        }
    }
}