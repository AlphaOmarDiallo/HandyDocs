package com.alphaomardiallo.handydocs

import android.app.Application
import com.alphaomardiallo.handydocs.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.Forest.plant

class HandyDocsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@HandyDocsApp)
            modules(appModule)
        }

        // Add Timber for safe logging
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }
}
