package com.senriot.ilangbox.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.pm.IPackageManager
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.preference.SPService
import com.apkfuns.logutils.LogUtils
import com.drake.net.Download
import com.drake.net.Get
import com.drake.net.utils.scopeNet
import java.io.File
import java.lang.reflect.Method


class UpdateService : IntentService("UpdateService")
{
    private val fileName = "db.realm"
    private val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm"
    override fun onHandleIntent(intent: Intent?)
    {
        scopeNet {
            val updateInfo = Get<UpdateInfo>(
                "sysParams/queryByCode?code=app_update_info",
                uid = "updateInfo"
            ).await()
            checkApp(updateInfo)
        }.catch {
            it.printStackTrace()
        }
    }


    private fun checkApp(updateInfo: UpdateInfo)
    {
        val v = SPService().dbVer
        val dbVer = updateInfo.paramsItems["dbVer"] ?: error("参数错误")
        if (dbVer.paramsValue != v)
        {
            val downloadParams = updateInfo.paramsItems["downloadUrl"] ?: error("参数错误")
            downDbFile(dbVer, downloadParams)
        }
        val appVer = updateInfo.paramsItems["appVer"] ?: error("参数错误")
        val pa = this.packageManager.getPackageInfo(this.packageName, 0).versionName
        if (appVer.paramsValue != pa)
        {
            val downloadParams = updateInfo.paramsItems["appUrl"] ?: error("参数错误")
            downloadApk(appVer, downloadParams)
            //获取本地已经下载APK进行效验，或下载APK
        }
    }

    private fun downDbFile(
        dbParams: UpdateInfo.SysParamsItem,
        downloadParams: UpdateInfo.SysParamsItem
    )
    {
        scopeNet {
            val filePath = Download(
                downloadParams.paramsValue,
                dbDir + "/" + dbParams.paramsValue,
                absolutePath = true,
                uid = "downloadDb"
            ) {
                onProgress { progress, byteCount, speed -> LogUtils.d("下载数据库 $progress") }
                fileName(fileName)
            }.await()
            SPService().apply {
                this.dbFilePath = filePath
                this.dbVer = dbParams.paramsValue
            }
        }
    }

    private fun downloadApk(
        appParams: UpdateInfo.SysParamsItem,
        downloadParams: UpdateInfo.SysParamsItem
    )
    {
        scopeNet {
            val filePath =
                Download(downloadParams.paramsValue, absolutePath = true, uid = "downloadApk") {
                    onProgress { progress, byteCount, speed ->
                        LogUtils.d("下载APK $progress")
                    }
                }.await()
            LogUtils.d(filePath)
            installApk(filePath)
        }
    }


    private fun installApk(path: String)
    {
        val file = File(path)
        val clazz = Class.forName("android.os.ServiceManager")
        val method_getService: Method = clazz.getMethod(
            "getService",
            String::class.java
        )
        val bind: IBinder = method_getService.invoke(null, "package") as IBinder

        val iPm = IPackageManager.Stub.asInterface(bind)
        iPm.installPackage(Uri.fromFile(file), null, 2, file.name)
    }

    companion object
    {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startUpdate(context: Context)
        {
            val intent = Intent(context, UpdateService::class.java)
            context.startService(intent)
        }
    }
}