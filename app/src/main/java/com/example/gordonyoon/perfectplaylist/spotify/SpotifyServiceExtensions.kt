package com.example.gordonyoon.perfectplaylist.spotify

import android.os.Looper
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun SpotifyService.getPpTempId(myId: String): String {
    throwIfOnMainThread()
    return getPlaylists(myId).items.firstOrNull { it.name == "Perfect Playlist - Temp" }?.id
            ?: throw NotImplementedError("Perfect Playlist - Temp does not exist yet.")
}

fun SpotifyService.getPpFinalId(myId: String): String {
    throwIfOnMainThread()
    return getPlaylists(myId).items.firstOrNull { it.name == "Perfect Playlist - Final" }?.id
            ?: throw NotImplementedError("Perfect Playlist - Final does not exist yet.")
}

fun SpotifyService.getLatestAddDate(myId: String, ppTempId: String): Date {
    throwIfOnMainThread()
    return getPlaylistTracks(myId, ppTempId).items.map { it.added_at.toDate() }.max()
            ?: throw NotImplementedError("The playlist is empty.")
}

fun SpotifyService.getNewTracks(myId: String, ppTempId: String, latestAdd: Date): List<Track> {
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

fun SpotifyService.playlistContainsTrack(ownerId: String, playlistId: String, trackUri: String): Boolean {
    throwIfOnMainThread()
    val foundTrack = findPlaylistTrack(ownerId, playlistId, trackUri) ?: return false
    Timber.d("Found track: ${foundTrack.name}")
    return true;
}

fun SpotifyService.findPlaylistTrack(ownerId: String, playlistId: String, trackUri: String): Track? {
    throwIfOnMainThread()
    return getAllPlaylistTracks(ownerId, playlistId).find { it.uri == trackUri }
}

fun PlaylistBase.getTracks(spotify: SpotifyService, newestAdd: Date = Date(0)): List<Track> {
    throwIfOnMainThread()
    return spotify.getAllPlaylistTracks(owner.id, id, newestAdd)
}

fun SpotifyService.getAllPlaylistTracks(ownerId: String, playlistId: String, newestAdd: Date = Date(0)): List<Track> {
    throwIfOnMainThread()
    val tracks: ArrayList<PlaylistTrack> = ArrayList()
    var offset = 0
    do {
        val pager = getPlaylistTracks(ownerId, playlistId, mapOf("offset" to offset))
        tracks.addAll(pager.items.filter { it.added_at.toDate().after(newestAdd) })
        offset += pager.limit
    } while (pager.next != null)
    return tracks.map { it.track }
}

fun Set<Track>.filterSavedTracks(spotify: SpotifyService): List<Track> {
    throwIfOnMainThread()
    if (isEmpty()) return toList()
    val ITEM_LIMIT = 50
    val newMinusSavedTracks = ArrayList<Track>()
    toList().split(ITEM_LIMIT).map {
        val trackIdsString = it.map { it.id }.joinToString(separator = ",")
        val contains = spotify.containsMySavedTracks(trackIdsString)
        it.filterIndexedTo(newMinusSavedTracks) { i, track -> !contains[i] }
    }
    return newMinusSavedTracks
}

fun SpotifyService.addTrackToPlaylist(userId: String, playlistId: String, trackUri: String): Unit {
    throwIfOnMainThread()
    addTracksToPlaylist(userId, playlistId, null, mapOf("uris" to listOf(trackUri)))
}

fun SpotifyService.addTrackToPlaylist(userId: String, playlistId: String, track: Track): Unit {
    throwIfOnMainThread()
    addTracksToPlaylist(userId, playlistId, listOf(track))
}

fun SpotifyService.addTracksToPlaylist(userId: String, playlistId: String, tracks: List<Track>): Unit {
    throwIfOnMainThread()
    if (tracks.isEmpty()) return
    val ITEM_LIMIT = 100
    tracks.split(ITEM_LIMIT).map {
        val trackUris = it.map { it.uri }
        addTracksToPlaylist(userId, playlistId, null, mapOf("uris" to trackUris))
    }
}

fun SpotifyService.removeTrackFromPlaylist(userId: String, playlistId: String, trackUri: String): Unit {
    throwIfOnMainThread()
    val trackToRemove  = TrackToRemove().apply { this.uri = trackUri }
    val tracksToRemove = TracksToRemove().apply { tracks = listOf(trackToRemove) }

    removeTracksFromPlaylist(userId, playlistId, tracksToRemove)
}

fun <E> List<E>.split(increment: Int = 1): List<List<E>> {
    val result: ArrayList<List<E>> = ArrayList()
    var start: Int = 0
    do {
        val end = if (start + increment - 1 < size) start + increment - 1 else size - 1
        result.add(slice(start..end))
        start += increment
    } while (start < size)
    return result
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
