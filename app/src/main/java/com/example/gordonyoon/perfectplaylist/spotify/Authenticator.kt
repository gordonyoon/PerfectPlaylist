package com.example.gordonyoon.perfectplaylist.spotify

import android.app.Activity
import android.content.Intent
import com.example.gordonyoon.perfectplaylist.App
import com.example.gordonyoon.perfectplaylist.R
import com.example.gordonyoon.perfectplaylist.extensions.hasInternetConnection
import com.example.gordonyoon.perfectplaylist.spotify.constants.Scopes
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.ERROR
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyCallback
import kaaes.spotify.webapi.android.models.UserPrivate
import org.jetbrains.anko.async
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Authenticator() {

    lateinit var context: App
    lateinit var api: SpotifyApi

    @Inject
    constructor(context: App, api: SpotifyApi) : this() {
        this.context = context
        this.api = api
    }

    private val CLIENT_ID: String by lazy { context.resources.getString(R.string.client_id) }
    private val REDIRECT_URI: String by lazy { context.resources.getString(R.string.redirect_uri) }
    private val REQUEST_CODE: Int by lazy { context.resources.getInteger(R.integer.request_code) }

    fun login(callbackActivity: Activity) {
        if (!context.hasInternetConnection()) {
            context.toast("No internet connection: Authentication failed.")
            return
        }

        verifiedLogin(callbackActivity) {
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
    }

    /**
     * Called in onActivityResult() when AuthenticationClient.openLoginActivity() returns
     */
    fun setAccessToken(requestCode: Int, resultCode: Int, intent: Intent?) {
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                TOKEN -> {
                    Timber.d("access token: ${response.accessToken}")
                    api.setAccessToken(response.accessToken)
                }
                ERROR -> {
                    Timber.d("error response: ${response.error}")
                    api.setAccessToken("")  // ensure that no transactions go through
                }
            }
        }
    }

    fun verifiedLogin(callbackActivity: Activity, login: (Activity) -> Unit) {
        api.service.getMe(object: Callback<UserPrivate> {
            override fun success(t: UserPrivate?, response: Response?) {
                Timber.d("Logged in!")
            }

            override fun failure(error: RetrofitError?) {
                login(callbackActivity)
            }

        })
    }
}