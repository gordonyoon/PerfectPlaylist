package com.example.gordonyoon.perfectplaylist.ui.viewpager

import android.content.Context
import android.util.AttributeSet

class TrackViewPager(context: Context, attributeSet: AttributeSet): BaseInfiniteViewPager(context, attributeSet) {

    fun updateTrack(trackTitle: String, artistName: String) {
        adapter?.updateTrack(currentItem, trackTitle, artistName)
    }
}