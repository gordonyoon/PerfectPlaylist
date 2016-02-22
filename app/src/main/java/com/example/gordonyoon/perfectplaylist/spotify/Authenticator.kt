package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import com.example.gordonyoon.perfectplaylist.R
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

class Authenticator(val context: Activity) {

    private var CLIENT_ID: String
    private var REDIRECT_URI: String
    private var REQUEST_CODE: Int

    init {
        CLIENT_ID = context.resources.getString(R.string.client_id)
        REDIRECT_URI = context.resources.getString(R.string.redirect_uri)
        REQUEST_CODE = context.resources.getInteger(R.integer.request_code)
    }

    fun login() {
        val builder = AuthenticationRequest.Builder(
                CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-private", "streaming"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(context, REQUEST_CODE, request)
    }
}