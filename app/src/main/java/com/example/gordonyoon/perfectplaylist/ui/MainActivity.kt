package com.example.gordonyoon.perfectplaylist.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.gordonyoon.perfectplaylist.R
import com.example.gordonyoon.perfectplaylist.androidextentions.getAppContext
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.spotify.*
import kaaes.spotify.webapi.android.SpotifyApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    val authenticator: Authenticator by lazy { Authenticator(this@MainActivity) }
    val api: SpotifyApi = SpotifyApi()

    @Inject lateinit var bus: RxBus

    var lastPlayedTrack: NowPlayingReceiver.NowPlayingTrack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppContext().spotifyComponent.inject(this)
        initializeUi()

        bus.toObserverable().subscribe {
            if (it is NowPlayingReceiver.NowPlayingTrack) {
                lastPlayedTrack = it
                trackTitle.text = it.name
                artistName.text = it.artist
            }
        }

        authenticator.login()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // TODO: refresh access token when it expires
        val accessToken: String? = authenticator.getAccessToken(requestCode, resultCode, intent)
        if (accessToken == null) {
            toast("Unsuccessful authentication")
        } else {
            api.setAccessToken(accessToken)
        }
    }

    fun initializeUi() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener    { api.service.updatePPTemp() }
        delete.setOnClickListener { api.service.removeNowPlaying(lastPlayedTrack?.uri) }
        save.setOnClickListener   { api.service.moveNowPlayingToPPFinal() }
    }
}
