package com.senriot.ilangbox.services

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Binder
import android.os.IBinder
import com.android.karaoke.common.api.Auth
import com.android.karaoke.common.api.Result
import com.android.karaoke.common.getDeviceSN
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.apkfuns.logutils.LogUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.gson.Gson
import com.senriot.ilangbox.event.LoginEvent
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import io.reactivex.disposables.Disposable
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.greenrobot.eventbus.EventBus

class MessageService : Service()
{

    private val host = "tcp://47.103.89.201:1883"

    private val snappy by lazy { DBFactory.open(this) }

    private var client: MqttAndroidClient? = null

    private lateinit var networkDisposable: Disposable

    private val objectMapper by lazy { ObjectMapper() }

    private fun getClientId(): String
    {
        return try
        {
            snappy.get("MqttClientId")
        } catch (e: SnappydbException)
        {
            val id = getDeviceSN()
            snappy.put("MqttClientId", id)
            id
        }
    }

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
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .onIO().filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
            .filter(ConnectivityPredicate.hasType(ConnectivityManager.TYPE_ETHERNET))
            .onUI({
                networkDisposable.dispose()
                initMqtt()
            }, {
                LogUtils.e(it)
            })
    }

    override fun onBind(intent: Intent): IBinder
    {
        return MessageServiceBinder()
    }

    fun initMqtt()
    {
        val clientId = getClientId()
        client = MqttAndroidClient(this, host, clientId)
        client?.setCallback(mqttCallback)

        val opt = MqttConnectOptions().apply {
            isCleanSession = true
            connectionTimeout = 10
            keepAliveInterval = 30
            userName = "ilangbox"
            password = "888999".toCharArray()
        }
        client?.connect(opt, this, object : IMqttActionListener
        {
            override fun onSuccess(asyncActionToken: IMqttToken?)
            {
                LogUtils.d("mtqq 连接成功")
                val topics =
                    arrayOf("device/langdu/$clientId/login", "device/langdu/onLogin/$clientId")
                client?.subscribe(topics, intArrayOf(0, 0))
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?)
            {
                LogUtils.e("mtqq 连接失败 $exception")
            }

        })
    }

    private fun doConnect(options: MqttConnectOptions)
    {

    }

    private val mqttCallback = object : MqttCallback
    {
        override fun connectionLost(cause: Throwable?)
        {
        }

        override fun messageArrived(topic: String?, message: MqttMessage?)
        {
            LogUtils.d("接收消息 $topic $message")
            if (topic?.startsWith("device/langdu/onLogin/", true) == true)
            {
                LogUtils.d("登录成功 $message")
                val auth = Gson().fromJson<Auth>(message.toString(),Auth::class.java)
                LogUtils.d(auth)
                EventBus.getDefault().post(LoginEvent(auth))
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?)
        {
        }

    }
}