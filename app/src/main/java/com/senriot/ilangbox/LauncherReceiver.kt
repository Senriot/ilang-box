package com.senriot.ilangbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.apkfuns.logutils.LogUtils

class LauncherReceiver : BroadcastReceiver()
{

    override fun onReceive(context: Context, intent: Intent)
    {
        LogUtils.e("启动APP ${intent.action}")
        when (intent.action)
        {
           Intent.ACTION_BOOT_COMPLETED ->
            {
                runApp(context)
            }
        }
    }

    private fun runApp(context: Context)
    {
        LogUtils.e("启动APP")
        val i = Intent(context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
//        context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
//            it.addFlags(FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(it)
//        }
    }
}