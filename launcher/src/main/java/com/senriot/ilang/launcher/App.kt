package com.senriot.ilang.launcher

import android.app.Application
import android.content.Context
import com.drake.net.initNet
import io.realm.Realm
import io.supercharge.rxsnappy.RxSnappy


class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        RxSnappy.init(this)
        Realm.init(this)
        initNet("https://ilang.senriot.com/sery/ilang/") {
            converter(MoshiConvert())
        }
    }

    override fun attachBaseContext(base: Context?)
    {
        super.attachBaseContext(base)
    }
}