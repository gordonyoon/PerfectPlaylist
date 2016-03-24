package com.example.gordonyoon.perfectplaylist.di.components

import android.app.Activity
import android.support.v4.app.FragmentManager
import com.example.gordonyoon.perfectplaylist.di.modules.ActivityModule
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.ui.MainActivity
import com.example.gordonyoon.perfectplaylist.ui.viewpager.adapter.InfinitePagerAdapterWrapper
import com.example.gordonyoon.perfectplaylist.ui.viewpager.adapter.TrackPagerAdapter
import dagger.Component

@PerActivity
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun activity(): Activity
    fun fragmentManager(): FragmentManager
    fun trackPagerAdapter(): TrackPagerAdapter
    fun infinitePagerAdapterWrapper(): InfinitePagerAdapterWrapper
}