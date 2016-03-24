package com.example.gordonyoon.perfectplaylist.ui.viewpager

import android.content.Context
import android.util.AttributeSet

class TrackViewPager(context: Context, attributeSet: AttributeSet): BaseInfiniteViewPager(context, attributeSet) {

    var internallyPaging: Boolean = false
    var previousPosition: Int = Integer.MIN_VALUE

    fun updateTrack(trackTitle: String, artistName: String) {
        adapter?.updateTrack(currentItem, trackTitle, artistName)
    }

    fun setOnPageChanged(onPageLeft: () -> Unit, onPageRight: () -> Unit) {
        addOnPageChangeListener(object: SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (!internallyPaging) {
                    if (position > previousPosition) {
                        onPageRight()
                    } else if (position in (Integer.MIN_VALUE + 1)..(previousPosition - 1)) {
                        onPageLeft()
                    }
                }
                previousPosition = position
                internallyPaging = false
            }
        })
    }

    fun pageLeft() {
        internallyPaging = true
        setCurrentItem(currentItem - 1, true)
    }

    fun pageRight() {
        internallyPaging = true
        setCurrentItem(currentItem + 1, true)
    }
}