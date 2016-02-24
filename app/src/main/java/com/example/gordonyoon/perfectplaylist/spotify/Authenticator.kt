package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.gordonyoon.perfectplaylist.R
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.ERROR
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN

class Authenticator(val context: Activity) {

    companion object {
        val TAG = Authenticator::class.java.name
    }

    private val CLIENT_ID: String by lazy { context.resources.getString(R.string.client_id) }
    private val REDIRECT_URI: String by lazy { context.resources.getString(R.string.redirect_uri) }
    private val REQUEST_CODE: Int by lazy { context.resources.getInteger(R.integer.request_code) }

    fun login() {
        val request = AuthenticationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI).apply {
            setScopes(arrayOf(Scopes.PLAYLIST_READ_PRIVATE))
        }.build()

        AuthenticationClient.openLoginActivity(context, REQUEST_CODE, request)
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
                    Log.d(TAG, "access token: ${response.accessToken}")
                    return response.accessToken
                }
                ERROR -> {
                    Log.d(TAG, "error response: ${response.error}")
                }
            }
        }
        return null
    }
}