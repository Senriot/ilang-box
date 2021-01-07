package com.senriot.ilangbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ApkInstalledReceiver : BroadcastReceiver()
{

    override fun onReceive(context: Context, intent: Intent)
    {
        when (intent.action)
        {
            Intent.ACTION_PACKAGE_REPLACED ->
            {
                reRunApp(intent, context)
            }
            Intent.ACTION_PACKAGE_ADDED ->
            {
                reRunApp(intent, context)
            }
        }
    }

    private fun reRunApp(intent: Intent, context: Context)
    {
        val packageName = intent.dataString
        val packages = context.packageName
        if (packageName == "package:$packages")
        {
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        }
    }
}