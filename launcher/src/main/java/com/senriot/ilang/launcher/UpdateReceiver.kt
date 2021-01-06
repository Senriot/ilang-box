package com.senriot.ilang.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.apkfuns.logutils.LogUtils
import org.greenrobot.eventbus.EventBus

class UpdateReceiver : BroadcastReceiver()
{

    override fun onReceive(context: Context, intent: Intent)
    {
        val packageName = intent.dataString
        val packages = "com.senriot.ilangbox"
        LogUtils.e("安装：$packageName  ${context.packageName}")
        if (packageName == "package:$packages")
        {
            if (intent.action == Intent.ACTION_PACKAGE_REPLACED)
            {
                LogUtils.e("程序升级了")
                EventBus.getDefault().post(AppInstalledEvent())

            }
            if (intent.action == Intent.ACTION_PACKAGE_ADDED)
            {
                LogUtils.e("程序安装了")
                EventBus.getDefault().post(AppInstalledEvent())
            }
            if (intent.action == Intent.ACTION_PACKAGE_REMOVED)
            {
                LogUtils.e("程序卸载了")
            }
        }
    }
}
