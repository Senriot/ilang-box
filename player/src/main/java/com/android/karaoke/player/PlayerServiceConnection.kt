package com.android.karaoke.player

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.apkfuns.logutils.LogUtils

class PlayerServiceConnection : ServiceConnection
{
    var service: PlayerService? = null

    override fun onServiceDisconnected(name: ComponentName?)
    {
        LogUtils.i("播放服务已经断开")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?)
    {
        LogUtils.i("播放服务已经启动")
        this.service = (service as PlayerService.PlayerServiceBinder).getService()
    }
}