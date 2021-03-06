package com.example.gordonyoon.perfectplaylist.ui.viewpager

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.AttributeSet
import com.antonyt.infiniteviewpager.InfiniteViewPager
import com.example.gordonyoon.perfectplaylist.ui.viewpager.adapter.InfinitePagerAdapterWrapper

abstract class BaseInfiniteViewPager(context: Context, attributeSet: AttributeSet): InfiniteViewPager(context, attributeSet) {

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

    override fun setAdapter(adapter: PagerAdapter?) {
        if (adapter !is InfinitePagerAdapterWrapper) {
            throw IllegalArgumentException("Adapter must be instance of InfinitePagerAdapterWrapper")
        }
        super.setAdapter(adapter)
    }

    override fun getAdapter(): InfinitePagerAdapterWrapper? {
        return super.getAdapter() as InfinitePagerAdapterWrapper
    }

    /**
     * Calculate the default starting position based on internal logic
     *
     * @see InfiniteViewPager#getOffsetAmount()
     */
    fun getDefaultPosition(): Int {
        return adapter?.realCount?.times(100) ?: Integer.MIN_VALUE
    }
}