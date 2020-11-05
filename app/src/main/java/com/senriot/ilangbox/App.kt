package com.senriot.ilangbox

import android.app.Application
import com.android.karaoke.common.realm.userConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.senriot.ilangbox.koin.ViewModels
import io.realm.Realm
import io.supercharge.rxsnappy.RxSnappy
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        RxSnappy.init(this)
//        Realm.init(this)
//        Realm.setDefaultConfiguration(userConfig)
        startKoin {
            androidLogger()

            androidContext(this@App)

            modules(ViewModels.module)
        }
    }
}