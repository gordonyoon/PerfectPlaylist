package com.example.gordonyoon.perfectplaylist.controllers

import android.util.Log
import com.example.gordonyoon.perfectplaylist.ui.MainActivity
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

fun MainActivity.printPlaylists() {
    val spotify = api.service

    async() {
        val playlists = spotify.getPlaylists(spotify.me.id).items

        uiThread {
            for (playlist in playlists)
                Log.d(MainActivity.TAG, "playlist name: ${playlist.name}")
        }
    }
}