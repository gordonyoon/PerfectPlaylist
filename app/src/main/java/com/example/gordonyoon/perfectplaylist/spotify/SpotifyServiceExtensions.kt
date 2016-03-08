package com.example.gordonyoon.perfectplaylist.spotify

import android.os.Looper
import android.util.Log
import kaaes.spotify.webapi.android.SpotifyService
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun SpotifyService.printMyPlaylists() {
    async() {
        // cache "me" locally for single call
        val me = me
        val myPlaylists = getPlaylists(me.id).items.filter {
            it.name != "Starred"
                    && it.name != "Liked from Radio"
                    && it.owner.id == me.id
        }
        uiThread {
            for (playlist in myPlaylists)
                Log.d("SpotifyService", "playlist name: ${playlist.name}")
        }
    }
}

fun SpotifyService.printFollowingPlaylistsSongs() {
    async() {
        val me = me
        val followingPlaylists = getPlaylists(me.id).items.filter { it.owner.id != me.id }
        val songs = ArrayList<String>()
        for (playlist in followingPlaylists) {
            var offset = 0
            do {
                val pager = getPlaylistTracks(playlist.owner.id, playlist.id, mapOf("offset" to offset))
                songs.addAll(pager.items.map { it.track.name })
                offset += pager.limit
            } while (pager.next != null)
        }
        uiThread {
            Timber.d("SpotifyService", "Total count: ${songs.size}")
        }
    }
}

fun SpotifyService.updatePPTemp(): Unit {
    async() {
        val ppTempId: String = getPPTempId() ?: throw NotImplementedError("Perfect Playlist - Temp does not exist yet.")
        val latestAddDate: Date = getPPTempLatestAddDate(ppTempId) ?: throw NotImplementedError("The playlist is empty.")
        val tracks = listOf("spotify:track:5KK05UNvYyH2QMyBlHGKtW")
        addTracksToPlaylist(me.id, ppTempId, null, mapOf("uris" to tracks))
    }
}

private fun SpotifyService.getPPTempId(): String? {
    throwIfOnMainThread()
    return getPlaylists(me.id).items.firstOrNull { it.name == "Perfect Playlist - Temp" }?.id
}

private fun SpotifyService.getPPTempLatestAddDate(ppTempId: String): Date? {
    throwIfOnMainThread()
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return getPlaylistTracks(me.id, ppTempId).items.map { sdf.parse(it.added_at) }.max()
}

private fun SpotifyService.throwIfOnMainThread() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        throw IllegalThreadStateException("hasPP() must be run on an asynchronous thread")
    }
}