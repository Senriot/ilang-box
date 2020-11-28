package com.senriot.ilang.launcher

import android.app.Application
import android.content.Context
import io.supercharge.rxsnappy.RxSnappy


class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        RxSnappy.init(this)
    }

    override fun attachBaseContext(base: Context?)
    {
        super.attachBaseContext(base)
    }
}