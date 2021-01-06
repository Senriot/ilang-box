package com.senriot.ilangbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*

class LauncherReceiver : BroadcastReceiver()
{

    override fun onReceive(context: Context, intent: Intent)
    {
        val action = intent.action.toString()
        when (action)
        {
            ACTION_BOOT_COMPLETED ->
            {
                runApp(context)
            }
            ACTION_PACKAGE_REPLACED ->
            {
                reRunApp(intent, context)
            }
            ACTION_PACKAGE_ADDED ->
            {
                reRunApp(intent, context)
            }
            ACTION_PACKAGE_REMOVED ->
            {

            }
        }
    }

    private fun reRunApp(intent: Intent, context: Context)
    {
        val packageName = intent.dataString
        val packages = context.packageName
        if (packageName == "package:$packages")
        {
            runApp(context)
        }
    }

    private fun runApp(context: Context)
    {
        context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
            it.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}