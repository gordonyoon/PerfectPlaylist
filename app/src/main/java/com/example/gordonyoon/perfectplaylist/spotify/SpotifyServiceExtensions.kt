package com.example.gordonyoon.perfectplaylist.spotify

import android.os.Looper
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun SpotifyService.updatePPTemp(): Unit {
    throwIfOnMainThread()
    val myId: String = me.id
    val ppTempId: String = getPpTempId(myId)
    val latestAddDate = getLatestAddDate(myId, ppTempId)
    Timber.d("The last track was added on $latestAddDate")
    val tracks = getNewTracks(myId, ppTempId, latestAddDate)
    if (!tracks.isEmpty()) {
        Timber.d("Now adding ${tracks.size} new tracks!\n${tracks.map { it.name }}")
        //        addTracksToPlaylist(myId, ppTempId, tracks)
    } else {
        Timber.d("No new tracks were found!")
    }
}

private fun SpotifyService.getPpTempId(myId: String): String {
    throwIfOnMainThread()
    return getPlaylists(myId).items.firstOrNull { it.name == "Perfect Playlist - Temp" }?.id
            ?: throw NotImplementedError("Perfect Playlist - Temp does not exist yet.")
}

private fun SpotifyService.getLatestAddDate(myId: String, ppTempId: String): Date {
    throwIfOnMainThread()
    return getPlaylistTracks(myId, ppTempId).items.map { it.added_at.toDate() }.max()
            ?: throw NotImplementedError("The playlist is empty.")
}

private fun SpotifyService.getNewTracks(myId: String, ppTempId: String, latestAdd: Date): List<Track> {
    throwIfOnMainThread()
    val followingPlaylists = getPlaylists(myId).items.filter { it.owner.id != myId }
    val ppTempTracks = getPlaylist(myId, ppTempId).getTracks(this)
    val newTracks = followingPlaylists
            .flatMap { it.getTracks(this, latestAdd) }
            .distinct()
            .subtract(ppTempTracks)
            .filterSavedTracks(this)
    return newTracks
}

fun PlaylistBase.getTracks(spotify: SpotifyService, newestAdd: Date = Date(0)): List<Track> {
    throwIfOnMainThread()
    val tracks: ArrayList<PlaylistTrack> = ArrayList()
    var offset = 0
    do {
        val pager = spotify.getPlaylistTracks(owner.id, id, mapOf("offset" to offset))
        tracks.addAll(pager.items.filter { it.added_at.toDate().after(newestAdd) })
        offset += pager.limit
    } while (pager.next != null)
    return tracks.map { it.track }
}

fun Set<Track>.filterSavedTracks(spotify: SpotifyService): List<Track> {
    throwIfOnMainThread()
    val INC = 50
    val newMinusSavedTracks = ArrayList<Track>()
    var offset = 0
    do {
        val start = offset * INC
        val end = if (start + INC - 1 < size) start + INC - 1 else size - 1
        val slicedTracks = this.toList().slice(start..end)
        val trackUrisString = slicedTracks.map { it.id }.joinToString(separator = ",")
        val contains = spotify.containsMySavedTracks(trackUrisString)
        slicedTracks.filterIndexedTo(newMinusSavedTracks) { i, track -> !contains[i] }
        offset++
    } while (start + INC < size)
    return newMinusSavedTracks
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
        throw IllegalThreadStateException("Must be run on an asynchronous thread.")
    }
}

fun String.toDate(): Date {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return sdf.parse(this)
}