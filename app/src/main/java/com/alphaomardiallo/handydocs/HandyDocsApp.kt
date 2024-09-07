package com.alphaomardiallo.handydocs

import android.app.Application
import com.alphaomardiallo.handydocs.di.appModule
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.MobileAds.setRequestConfiguration
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@HandyDocsApp).apply {
                setRequestConfiguration(
                    RequestConfiguration.Builder()
                        .setTestDeviceIds(listOf("94de9264-156b-4b99-9198-5ff1749c1488")).build()
                )
            }
        }
    }
}
