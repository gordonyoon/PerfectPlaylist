package com.example.gordonyoon.perfectplaylist.spotify

import android.util.Log
import kaaes.spotify.webapi.android.SpotifyService
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
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
            Log.d("SpotifyService", "Total count: ${songs.size}")
        }
    }
}