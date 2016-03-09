package com.example.gordonyoon.perfectplaylist.spotify

import android.os.Looper
import android.util.Log
import android.widget.Toast
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
        val myId: String = me.id
        val ppTempId: String = getPlaylists(myId).items.firstOrNull { it.name == "Perfect Playlist - Temp" }?.id
                ?: throw NotImplementedError("Perfect Playlist - Temp does not exist yet.")
        val latestAddDate: Date = getPlaylistTracks(myId, ppTempId).items.map { it.added_at.toDate() }.max()
                ?: throw NotImplementedError("The playlist is empty.")
        Timber.d("The last track was added on $latestAddDate")
        val tracks = getNewTrackUris(myId, latestAddDate)
        if (!tracks.isEmpty()) {
            Timber.d("Now adding ${tracks.size} new tracks!")
            addTracksToPlaylist(myId, ppTempId, tracks)
        } else {
            Timber.d("No new tracks were found!")
        }
    }
}

private fun SpotifyService.getNewTrackUris(myId: String, latestAdd: Date): List<String> {
    throwIfOnMainThread()
    val followingPlaylists = getPlaylists(myId).items.filter { it.owner.id != myId }
    val trackIds: ArrayList<String> = ArrayList()
    for (playlist in followingPlaylists) {
        var offset = 0
        do {
            val pager = getPlaylistTracks(playlist.owner.id, playlist.id, mapOf("offset" to offset))
            trackIds.addAll(pager.items.filter { it.added_at.toDate().after(latestAdd) }.map { it.track.uri })
            offset += pager.limit
        } while (pager.next != null)
    }
    return trackIds
}

private fun SpotifyService.addTracksToPlaylist(myId: String, ppTempId: String, trackIds: List<String>): Unit {
    throwIfOnMainThread()
    if (trackIds.isEmpty()) return
    val ITEM_LIMIT: Int = 100
    var start: Int = 0
    do {
        val end = if (start + ITEM_LIMIT - 1 < trackIds.size) start + ITEM_LIMIT - 1 else trackIds.size - 1
        addTracksToPlaylist(myId, ppTempId, null, mapOf("uris" to trackIds.slice(start..end)))
        start += 100
    } while (end < trackIds.size - 1)
}

fun throwIfOnMainThread() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        throw IllegalThreadStateException("hasPP() must be run on an asynchronous thread")
    }
}

fun String.toDate(): Date {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return sdf.parse(this)
}