package com.senriot.ilangbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.karaoke.common.api.Api
import com.android.karaoke.common.realm.UserDataHelper
import com.apkfuns.logutils.LogUtils
import com.senriot.ilangbox.event.LoginEvent
import com.senriot.ilangbox.services.MessageService
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.glxn.qrgen.android.QRCode
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LoginActivity : AppCompatActivity()
{

    override fun onDestroy()
    {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private val snappy by lazy { DBFactory.open(this) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        EventBus.getDefault().register(this)
        startService(Intent(this, MessageService::class.java))
        val id = snappy.get("MqttClientId")
        GlobalScope.launch(Dispatchers.Main){
            val resp = Api.updateApiService.authUrl(id).await()
            if (resp.success){
                val bitmap = QRCode.from(resp.result).withSize(350, 350).bitmap()
                loginQr.setImageBitmap(bitmap)
            }
        }

        guestLogin.setOnClickListener {
            UserDataHelper.initUserData("Guest")
            try
            {
                snappy.del("authInfo")
            } catch (e: SnappydbException)
            {
                e.printStackTrace()
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    @Subscribe
    fun onLogin(event: LoginEvent)
    {
        LogUtils.d(event)
        snappy.put("authInfo", event.auth)
        UserDataHelper.initUserData(event.auth.accessToken.openId)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}