package com.senriot.ilangbox.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import com.android.karaoke.common.api.Auth
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.preference.SPService
import com.apkfuns.logutils.LogUtils
import com.drake.net.Download
import com.drake.net.Get
import com.drake.net.utils.scopeNet
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.gson.Gson
import com.senriot.ilangbox.event.LoginEvent
import com.senriot.ilangbox.getDeviceSN
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import io.reactivex.disposables.Disposable
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.greenrobot.eventbus.EventBus
import java.nio.charset.Charset

class MessageService : Service()
{

    private val host = "tcp://47.103.89.201:1883"

    private var client: MqttAndroidClient? = null

    private lateinit var networkDisposable: Disposable

    private val objectMapper by lazy { ObjectMapper() }
    private val fileName = "db.realm"
    private val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm"

    inner class MessageServiceBinder : Binder()
    {
        fun getService(): MessageService
        {
            return this@MessageService
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        LogUtils.d("启动消息服务")
        initMqtt()
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .onIO()
            .filter(
                ConnectivityPredicate.hasType(
                    ConnectivityManager.TYPE_ETHERNET,
                    ConnectivityManager.TYPE_WIFI
                )
            )
            .onUI({
                networkDisposable.dispose()
                when (it.state())
                {
                    NetworkInfo.State.CONNECTED ->
                    {
                        this.getUpdateInfo()
                    }
                    else                        ->
                    {
                    }
                }
            }, {

            })
    }

    private fun getUpdateInfo()
    {
        scopeNet {
            val updateInfo = Get<UpdateInfo>(
                "sysParams/queryByCode?code=app_update_info_v2",
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
        RootTools.getShell(true)
            .add(object : Command(0, "pm install -r -i $packageName $path \n")
            {
                override fun commandCompleted(id: Int, exitcode: Int)
                {
                    super.commandCompleted(id, exitcode)
                    LogUtils.d("安装完成")
                }

                override fun commandOutput(id: Int, line: String?)
                {
                    super.commandOutput(id, line)
                    LogUtils.d(line)
                }
            })
    }

    override fun onBind(intent: Intent): IBinder
    {
        return MessageServiceBinder()
    }

    @SuppressLint("CheckResult")
    fun initMqtt()
    {
        getDeviceSN().subscribe {
            val clientId = it
            client = MqttAndroidClient(this, host, clientId)
            client?.setCallback(mqttCallback)
            val opt = MqttConnectOptions().apply {
                isCleanSession = true
                connectionTimeout = 10
                keepAliveInterval = 30
                userName = "ilangbox"
                password = "888999".toCharArray()
                isAutomaticReconnect = true
                setWill("willTopic", "offline".toByteArray(Charset.defaultCharset()), 2, false)
            }
            client?.connect(opt, this, object : IMqttActionListener
            {
                override fun onSuccess(asyncActionToken: IMqttToken)
                {
                    LogUtils.d("mtqq 连接成功")
//                    val topics =
//                        arrayOf("device/langdu/$clientId/login", "device/langdu/onLogin/$clientId")
//                    asyncActionToken.client.subscribe(topics, intArrayOf(0, 0))
//                    client?.subscribe(topics, intArrayOf(0, 0))
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?)
                {
                    LogUtils.e("mtqq 连接失败 $exception")
                }
            })
        }
    }


    private val mqttCallback = object : MqttCallbackExtended
    {
        override fun connectionLost(cause: Throwable?)
        {
            cause?.printStackTrace()
        }

        override fun messageArrived(topic: String?, message: MqttMessage?)
        {
            LogUtils.d("接收消息 $topic $message")
            if (topic?.startsWith("device/langdu/onLogin/", true) == true)
            {
                LogUtils.d("登录成功 $message")
                val auth = Gson().fromJson<Auth>(message.toString(), Auth::class.java)
                LogUtils.d(auth)
                EventBus.getDefault().post(LoginEvent(auth))
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?)
        {
        }

        @SuppressLint("CheckResult")
        override fun connectComplete(reconnect: Boolean, serverURI: String?)
        {
            LogUtils.e("连接完成 serverURI: $serverURI reconnect:$reconnect")
            getDeviceSN().subscribe {
                val topics = arrayOf("device/langdu/$it/login", "device/langdu/onLogin/$it")
                client?.subscribe(topics, intArrayOf(0, 0))
            }
        }
    }
}