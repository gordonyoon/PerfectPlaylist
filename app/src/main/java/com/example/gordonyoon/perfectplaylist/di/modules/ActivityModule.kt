package com.example.gordonyoon.perfectplaylist.di.modules

import android.app.Activity
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(val activity: AppCompatActivity) {

    @Provides
    @PerActivity
    fun provideActivity(): Activity = activity

    @Provides
    @PerActivity
    fun provideAppCompatActivity(): AppCompatActivity = activity

    @Provides
    @PerActivity
    fun provideFragmentManager(activity: AppCompatActivity): FragmentManager {
        return activity.supportFragmentManager
    }
}