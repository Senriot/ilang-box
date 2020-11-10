package com.senriot.ilang.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.apkfuns.logutils.LogUtils

class UpdateReceiver : BroadcastReceiver()
{

    override fun onReceive(context: Context, intent: Intent)
    {
        val packageName = intent.dataString
        LogUtils.e("安装：$packageName  ${context.packageName}")

        if (packageName == "package:" + context.packageName)
        {
            if (intent.action == Intent.ACTION_PACKAGE_REPLACED)
            {
                LogUtils.e("程序升级了")
                val i = context.packageManager
                        .getLaunchIntentForPackage(context.packageName)
                i?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(i)

            }
            if (intent.action == Intent.ACTION_PACKAGE_ADDED)
            {
                LogUtils.e("程序安装了")
            }
            if (intent.action == Intent.ACTION_PACKAGE_REMOVED)
            {
                LogUtils.e("程序卸载了")
            }
        }
    }
}
