package com.android.karaoke.mqtt

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.apkfuns.logutils.LogUtils
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.Charset

class MqttService : Service()
{
    private val host = "tcp://47.103.89.201:1883";
    private val uName = "DeviceClient"
    private val pwd = "518918"

    private val clientId = getDeviceSN()

    private lateinit var conOpt: MqttConnectOptions
    private var client: MqttAndroidClient? = null

    override fun onBind(intent: Intent): IBinder
    {
        return MqttServiceBinder()
    }

    override fun onCreate()
    {
        super.onCreate()
        if (client == null || client?.isConnected == false)
        {
            client = MqttAndroidClient(this, host, clientId)
            client?.setCallback(mMqttCallback)

            conOpt = MqttConnectOptions().apply {
                isCleanSession = false
                connectionTimeout = 10
                keepAliveInterval = 60
                this.userName = uName
                password = pwd.toCharArray()
                isAutomaticReconnect = true
                setWill("willTopic", "offline".toByteArray(Charset.defaultCharset()), 2, false)
            }

            client?.connect(conOpt, null, mMqttActionListener)
        }

    }

    private val mMqttActionListener = object : IMqttActionListener
    {
        override fun onSuccess(asyncActionToken: IMqttToken?)
        {
            LogUtils.i("MQTT 连接成功")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?)
        {

            exception?.printStackTrace()
        }

    }


    private val mMqttCallback = object : MqttCallback
    {
        override fun messageArrived(topic: String?, message: MqttMessage?)
        {
        }

        override fun connectionLost(cause: Throwable?)
        {
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?)
        {
        }
    }

    inner class MqttServiceBinder : Binder()
    {
        fun getService(): MqttService
        {
            return this@MqttService
        }
    }
}
