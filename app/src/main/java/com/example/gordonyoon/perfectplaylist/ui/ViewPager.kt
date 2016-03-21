package com.example.gordonyoon.perfectplaylist.ui

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet

/**
 * Custom
 */
class TrackViewPager(context: Context, attributeSet: AttributeSet): ViewPager(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for (i in 0..(childCount - 1)) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            if (h > height) height = h
        }

        val adjustedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, adjustedHeightMeasureSpec)
    }
}