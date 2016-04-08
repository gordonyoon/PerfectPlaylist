package com.example.gordonyoon.perfectplaylist.extensions

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
            .distinctBy { it.id }
            .subtractWithBy(ppTempTracks) { it.id }
            .filterSavedTracks(this)
    return newTracks
}

fun PlaylistBase.getTracks(spotify: SpotifyService, newestAdd: Date = Date(0)): List<Track> {
    throwIfOnMainThread()
    return spotify.getAllPlaylistTracks(owner.id, id, newestAdd).filter { it != null }
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

fun List<Track>.filterSavedTracks(spotify: SpotifyService): List<Track> {
    throwIfOnMainThread()
    if (isEmpty()) return toList()
    val ITEM_LIMIT = 50
    val newMinusSavedTracks = ArrayList<Track>()
    split(ITEM_LIMIT).map {
        val trackIdsString = it.map { it.id }.joinToString(separator = ",")
        val contains = spotify.containsMySavedTracks(trackIdsString)
        it.filterIndexedTo(newMinusSavedTracks) { i, track -> !contains[i] }
    }
    return newMinusSavedTracks
}

fun SpotifyService.addDistinctTracksToPlaylist(userId: String, playlistId: String, trackUris: List<String>): Unit {
    throwIfOnMainThread()
    if (trackUris.isEmpty()) return
    val alreadyExist: List<String> = getAllPlaylistTracks(userId, playlistId).map { it.uri }
    val distinctTracks: List<String> = trackUris.subtract(alreadyExist).toList()

    addTracksToPlaylist(userId, playlistId, distinctTracks)
}

fun SpotifyService.addTracksToPlaylist(userId: String, playlistId: String, trackUris: List<String>): Unit {
    throwIfOnMainThread()
    if (trackUris.isEmpty()) return
    val ITEM_LIMIT = 100
    trackUris.split(ITEM_LIMIT).map {
        addTracksToPlaylist(userId, playlistId, null, mapOf("uris" to it))
    }
}

fun SpotifyService.addTracksToMyLibrary(ids: List<String>): Unit {
    throwIfOnMainThread()
    if (ids.isEmpty()) return
    val ITEM_LIMIT = 50
    ids.split(ITEM_LIMIT).map {
        val idsAsString = it.joinToString(separator = ",")
        addToMySavedTracks(idsAsString)
    }
}

fun SpotifyService.removeTracksFromPlaylist(userId: String, playlistId: String, trackUris: List<String>): Unit {
    throwIfOnMainThread()
    val ITEM_LIMIT = 100
    trackUris.split(ITEM_LIMIT).map {
        val tracks = trackUris.map { TrackToRemove().apply { uri = it } }
        val tracksToRemove = TracksToRemove().apply { this.tracks = tracks }

        removeTracksFromPlaylist(userId, playlistId, tracksToRemove)
    }
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
