package com.example.gordonyoon.perfectplaylist.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.gordonyoon.perfectplaylist.R
import com.example.gordonyoon.perfectplaylist.spotify.*
import kaaes.spotify.webapi.android.SpotifyApi
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    val authenticator: Authenticator by lazy { Authenticator(this@MainActivity) }
    val api: SpotifyApi = SpotifyApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            async() {
                api.service.updatePPTemp()
            }
        }

        authenticator.login()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        Timber.d("Successfully returned from logging in")

        // TODO: refresh access token when it expires
        val accessToken: String? = authenticator.getAccessToken(requestCode, resultCode, intent)
        api.setAccessToken(accessToken!!)
    }
}
