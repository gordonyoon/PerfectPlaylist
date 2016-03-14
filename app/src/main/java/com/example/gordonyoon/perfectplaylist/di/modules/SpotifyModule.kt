package com.example.gordonyoon.perfectplaylist.di.modules

import com.example.gordonyoon.perfectplaylist.rx.RxBus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SpotifyModule {

    @Provides
    @Singleton
    fun provideRxBus(): RxBus {
        return RxBus()
    }
}