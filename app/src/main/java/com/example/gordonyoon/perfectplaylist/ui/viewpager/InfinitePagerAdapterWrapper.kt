package com.example.gordonyoon.perfectplaylist.ui.viewpager

import android.view.ViewGroup
import com.antonyt.infiniteviewpager.InfinitePagerAdapter
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import timber.log.Timber
import javax.inject.Inject

@PerActivity
class InfinitePagerAdapterWrapper: InfinitePagerAdapter {

    var position: Int = -1

    lateinit var adapter: TrackPagerAdapter

    @Inject
    constructor(adapter: TrackPagerAdapter): super(adapter)

    override fun instantiateItem(container: ViewGroup?, position: Int): Any? {
        try {
            this.position = position % adapter.NUM_PAGES
        } catch (e: UninitializedPropertyAccessException) {
            Timber.d("The adapter was not initialized yet.")
        }
        return super.instantiateItem(container, position)
    }

    fun updateTrack(trackTitle: String, artistName: String) {
//        val trackFragment = adapter.getItem(adapter.PAGE_MAIN)
//        trackFragment?.updateTrack(trackTitle, artistName)
    }

    fun clear() = adapter.clear()
}