package com.example.gordonyoon.perfectplaylist

import android.app.Application
import com.example.gordonyoon.perfectplaylist.di.HasComponent
import com.example.gordonyoon.perfectplaylist.di.components.AppComponent
import com.example.gordonyoon.perfectplaylist.di.components.DaggerAppComponent
import com.example.gordonyoon.perfectplaylist.di.modules.AppModule
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import kotlin.reflect.KProperty

class App : Application(), HasComponent<AppComponent> {

    override val component: AppComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(App@this)).build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        val config = RealmConfiguration.Builder(this).build()
        Realm.setDefaultConfiguration(config)

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }
}