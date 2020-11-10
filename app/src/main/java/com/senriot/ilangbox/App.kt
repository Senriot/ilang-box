package com.senriot.ilangbox

import android.app.Application
import com.android.karaoke.common.realm.songsConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.senriot.ilangbox.koin.ViewModels
import com.yuan.library.dmanager.download.DownloadManager
import io.realm.Realm
import io.supercharge.rxsnappy.RxSnappy
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        Fresco.initialize(this)
        RxSnappy.init(this)
        DownloadManager.getInstance().init(this, 3)
        Realm.init(this)
        Realm.setDefaultConfiguration(songsConfig)
        startKoin {
            androidLogger()

            androidContext(this@App)

            modules(ViewModels.module)
        }
    }
}