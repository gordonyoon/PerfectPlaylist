package com.example.gordonyoon.perfectplaylist.spotify

import android.util.Log
import com.example.gordonyoon.perfectplaylist.ui.MainActivity
import kaaes.spotify.webapi.android.SpotifyService
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

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
                Log.d(MainActivity.TAG, "playlist name: ${playlist.name}")
        }
    }
}

fun SpotifyService.printFollowingPlaylists() {
    async() {
        val me = me
        val followingPlaylists = getPlaylists(me.id).items.filter { it.owner.id != me.id }
        uiThread {
            for (playlist in followingPlaylists)
                Log.d(MainActivity.TAG, "playlist name: ${playlist.name}")
        }
    }
}
