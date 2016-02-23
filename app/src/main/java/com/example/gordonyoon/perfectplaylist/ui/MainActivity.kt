package com.example.gordonyoon.perfectplaylist.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.gordonyoon.perfectplaylist.R
import com.example.gordonyoon.perfectplaylist.spotify.Authenticator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    val authenticator: Authenticator by lazy { Authenticator(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
        }

        authenticator.login()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        Log.d(TAG, "Successfully returned from logging in")

        authenticator.onLoginResult(requestCode, resultCode, intent)
    }
}
