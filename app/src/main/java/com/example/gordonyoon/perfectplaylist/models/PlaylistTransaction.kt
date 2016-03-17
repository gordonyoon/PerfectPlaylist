package com.example.gordonyoon.perfectplaylist.models

import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PlaylistTransaction(
        @PrimaryKey
        open var uri:    String  = "",
        open var artist: String  = "",
        open var album:  String  = "",
        open var name:   String  = "",
        open var length: Int     = 0,
        open var save:   Boolean = false,
        open var remove: Boolean = false
) : RealmObject() {

    constructor(track: NowPlayingReceiver.NowPlayingTrack) : this() {
        this.uri    = track.uri
        this.artist = track.artist
        this.album  = track.album
        this.name   = track.name
        this.length = track.length
    }

    fun toNowPlayingTrack(): NowPlayingReceiver.NowPlayingTrack {
        return NowPlayingReceiver.NowPlayingTrack(uri, artist, album, name, length)
    }
}
