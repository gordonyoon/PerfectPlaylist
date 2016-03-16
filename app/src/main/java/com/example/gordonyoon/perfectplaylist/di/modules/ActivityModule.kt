package com.example.gordonyoon.perfectplaylist.di.modules

import android.app.Activity
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(val activity: Activity) {

    @Provides
    @PerActivity
    fun activity(): Activity = activity
}