package com.example.gordonyoon.perfectplaylist.ui.viewpager

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.AttributeSet
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingState.OnNowPlayingExpiredListener

class TrackViewPager(context: Context,
                     attributeSet: AttributeSet
): BaseInfiniteViewPager(context, attributeSet), OnNowPlayingExpiredListener {

    var previousPosition: Int = Integer.MIN_VALUE

    fun updateTrack(trackTitle: String, artistName: String) {
        adapter?.updateTrack(currentItem, trackTitle, artistName)
    }

    fun setOnPageChanged(onPageLeft: () -> Unit, onPageRight: () -> Unit) {
        addOnPageChangeListener(object: SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (previousPosition == Integer.MIN_VALUE) {
                    previousPosition = getDefaultPosition()
                }

                if (position > previousPosition) {
                    onPageRight()
                } else if (position in (Integer.MIN_VALUE + 1)..(previousPosition - 1)) {
                    onPageLeft()
                }
                previousPosition = position
            }
        })
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        previousPosition = getAdapter()?.realCount?.times(100) ?: 0
    }

    override fun expire() {
        adapter?.expire(currentItem)
    }

    override fun unexpire() {
        adapter?.unexpire(currentItem)
    }
}