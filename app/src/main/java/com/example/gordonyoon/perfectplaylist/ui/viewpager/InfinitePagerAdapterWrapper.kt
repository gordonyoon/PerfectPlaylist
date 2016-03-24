package com.example.gordonyoon.perfectplaylist.ui.viewpager

import com.antonyt.infiniteviewpager.InfinitePagerAdapter
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import javax.inject.Inject

@PerActivity
class InfinitePagerAdapterWrapper: InfinitePagerAdapter {

    lateinit var adapter: TrackPagerAdapter

    @Inject
    constructor(adapter: TrackPagerAdapter): super(adapter)

    fun updateTrack(trackTitle: String, artistName: String) {
//        val trackFragment = adapter.getItem(adapter.PAGE_MAIN)
//        trackFragment?.updateTrack(trackTitle, artistName)
    }
}