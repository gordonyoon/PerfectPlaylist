package com.example.gordonyoon.perfectplaylist.ui

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import java.util.*
import javax.inject.Inject

@PerActivity
class TrackPagerAdapter: FragmentPagerAdapter {

    val NUM_PAGES = 2
    val PAGAE_MAIN = 0
    val PAGE_EXTRA = 1

    private val views = ArrayList<TrackFragment>()

    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager

    @Inject
    constructor(activity: Activity, fragmentManager: FragmentManager): super(fragmentManager) {
        this.activity = activity
        this.fragmentManager = fragmentManager

        for (i in 1..NUM_PAGES) views.add(TrackFragment())
    }

    override fun getCount(): Int = NUM_PAGES

    override fun getItem(position: Int): Fragment? = views[position]

    fun updateCurrentTrack(trackTitle: String, artistName: String) {
        views[PAGAE_MAIN].updateTrack(trackTitle, artistName)
    }
}