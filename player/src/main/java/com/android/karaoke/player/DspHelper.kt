package com.android.karaoke.player

import com.apkfuns.logutils.LogUtils
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean

object DspHelper : SerialHelper("/dev/ttyS4", 115200)
{
    override fun onDataReceived(paramComBean: ComBean?)
    {
        LogUtils.i(paramComBean?.bRec)
    }
}