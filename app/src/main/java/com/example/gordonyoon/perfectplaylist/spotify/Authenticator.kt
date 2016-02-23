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

    private val TAG = "Authenticator"

    private val CLIENT_ID: String by lazy { context.resources.getString(R.string.client_id) }
    private val REDIRECT_URI: String by lazy { context.resources.getString(R.string.redirect_uri) }
    private val REQUEST_CODE: Int by lazy { context.resources.getInteger(R.integer.request_code) }

    fun login() {
        val builder = AuthenticationRequest.Builder(
                CLIENT_ID, TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-private", "streaming"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(context, REQUEST_CODE, request)
    }

    fun onLoginResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                TOKEN -> {
                    Log.d(TAG, response.accessToken)
                }
                ERROR -> {
                }
                else -> {
                }
            }
        }
    }
}