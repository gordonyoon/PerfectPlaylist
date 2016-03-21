package com.example.gordonyoon.perfectplaylist.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.gordonyoon.perfectplaylist.R
import com.example.gordonyoon.perfectplaylist.di.HasComponent
import com.example.gordonyoon.perfectplaylist.di.components.ActivityComponent
import com.example.gordonyoon.perfectplaylist.di.components.DaggerActivityComponent
import com.example.gordonyoon.perfectplaylist.di.modules.ActivityModule
import com.example.gordonyoon.perfectplaylist.extensions.getAppContext
import com.example.gordonyoon.perfectplaylist.spotify.Authenticator
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingState
import com.example.gordonyoon.perfectplaylist.spotify.PlaylistController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnNowPlayingChangeListener, HasComponent<ActivityComponent> {

    override val component: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .appComponent(getAppContext().component)
                .activityModule(ActivityModule(MainActivity@this))
                .build()
    }

    @Inject lateinit var authenticator: Authenticator
    @Inject lateinit var controller: PlaylistController
    @Inject lateinit var nowPlayingState: NowPlayingState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        initializeUi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        authenticator.setAccessToken(requestCode, resultCode, intent)
    }

    override fun onResume() {
        super.onResume()
        authenticator.login(this)
    }

    override fun onStart() {
        super.onStart()
        nowPlayingState.register(this)
    }

    override fun onStop() {
        super.onStop()
        nowPlayingState.unregister()
    }

    fun initializeUi() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        trackTitle.apply {
            isSelected = false
            onClick { isSelected = !isSelected }
        }

        fab.setOnClickListener    { controller.refresh() }
        save.setOnClickListener   { controller.nowPlayingSave(nowPlayingState.nowPlaying) }
        delete.setOnClickListener { controller.nowPlayingDelete(nowPlayingState.nowPlaying) }
    }

    override fun updateUi(trackTitle: String, artistName: String) {
        this.artistName.text = artistName
        this.trackTitle.text = trackTitle

        this.trackTitle.isSelected = false
    }
}


