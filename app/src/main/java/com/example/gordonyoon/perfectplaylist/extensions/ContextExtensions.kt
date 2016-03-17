package com.example.gordonyoon.perfectplaylist.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.gordonyoon.perfectplaylist.App

fun Context.getAppContext(): App = applicationContext as App

fun Context.hasInternetConnection(): Boolean {
    val connectivityManager: ConnectivityManager? =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = connectivityManager?.activeNetworkInfo
    return networkInfo?.isConnected ?: false
}
