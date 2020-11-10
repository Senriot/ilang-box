package com.senriot.ilang.launcher

import android.app.Application
import io.supercharge.rxsnappy.RxSnappy

class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        RxSnappy.init(this)
    }
}