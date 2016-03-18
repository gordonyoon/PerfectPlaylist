package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import android.content.Intent
import com.example.gordonyoon.perfectplaylist.App
import com.example.gordonyoon.perfectplaylist.R
import com.example.gordonyoon.perfectplaylist.spotify.constants.Scopes
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.ERROR
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Authenticator() {

    private var loggedIn = false

    lateinit var context: App

    @Inject
    constructor(context: App) : this() {
        this.context = context
    }

    private val CLIENT_ID: String by lazy { context.resources.getString(R.string.client_id) }
    private val REDIRECT_URI: String by lazy { context.resources.getString(R.string.redirect_uri) }
    private val REQUEST_CODE: Int by lazy { context.resources.getInteger(R.integer.request_code) }

    fun login(callbackActivity: Activity) {
        val request = AuthenticationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI).apply {
            setScopes(arrayOf(
                    Scopes.PLAYLIST_READ_PRIVATE,
                    Scopes.PLAYLIST_MODIFY_PRIVATE,
                    Scopes.PLAYLIST_MODIFY_PUBLIC,
                    Scopes.USER_LIBRARY_MODIFY,
                    Scopes.USER_LIBRARY_READ))
        }.build()

        AuthenticationClient.openLoginActivity(callbackActivity, REQUEST_CODE, request)
    }

    /**
     * Called in onActivityResult() when AuthenticationClient.openLoginActivity() returns
     */
    fun getAccessToken(requestCode: Int, resultCode: Int, intent: Intent?): String? {
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                TOKEN -> {
                    Timber.d("access token: ${response.accessToken}")

                    loggedIn = true
                    return response.accessToken
                }
                ERROR -> {
                    Timber.d("error response: ${response.error}")
                    loggedIn = false
                }
            }
        }
        return null
    }

    fun isLoggedIn(): Boolean {
        return loggedIn
    }
}