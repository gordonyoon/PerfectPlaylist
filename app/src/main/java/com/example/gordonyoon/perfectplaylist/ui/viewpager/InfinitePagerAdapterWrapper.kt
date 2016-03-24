package com.example.gordonyoon.perfectplaylist.ui.viewpager

import com.antonyt.infiniteviewpager.InfinitePagerAdapter
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import javax.inject.Inject

@PerActivity
class InfinitePagerAdapterWrapper: InfinitePagerAdapter {

    @Inject lateinit var adapter: TrackPagerAdapter

    @Inject
    constructor(adapter: TrackPagerAdapter): super(adapter)

    fun updateTrack(position: Int, trackTitle: String, artistName: String) {
        adapter.getItem(position).updateTrack(trackTitle, artistName)
    }
}