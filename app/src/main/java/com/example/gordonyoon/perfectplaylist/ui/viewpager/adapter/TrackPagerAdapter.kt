package com.example.gordonyoon.perfectplaylist.ui.viewpager.adapter

import android.app.Activity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import java.util.*
import javax.inject.Inject

@PerActivity
class TrackPagerAdapter : FragmentPagerAdapter {

    val NUM_PAGES = 4  // required for the InfinitePagerAdapter

    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager

    val trackFragments = ArrayList<TrackFragment>()

    @Inject
    constructor(activity: Activity, fragmentManager: FragmentManager) : super(fragmentManager) {
        this.activity = activity
        this.fragmentManager = fragmentManager

        // when the activity is recreated, the fragments are still saved in fragmentManager
        clearFragmentManager()

        for (i in 1..NUM_PAGES) {
            trackFragments.add(TrackFragment())
        }
        notifyDataSetChanged()
    }

    fun clearFragmentManager() {
        if (fragmentManager.fragments == null) return

        fragmentManager.beginTransaction().apply {
            for (fragment in fragmentManager.fragments) {
                remove(fragment)
            }
        }.commit()
    }

    override fun getCount(): Int = NUM_PAGES

    override fun getItem(position: Int): TrackFragment = trackFragments[position]

    fun clear() = trackFragments.map { it.clear() }
}