package com.senriot.ilangbox

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.karaoke.common.getDeviceSN
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.realm.UserDataHelper
import com.apkfuns.logutils.LogUtils
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.senriot.ilangbox.event.LoginEvent
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*
import net.glxn.qrgen.android.QRCode
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class LoginActivity : AppCompatActivity()
{

    override fun onDestroy()
    {
        EventBus.getDefault().unregister(this)
        networkDisposable?.dispose()
        super.onDestroy()
    }

    private var networkDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        EventBus.getDefault().register(this)
        //startService(Intent(this, MessageService::class.java))
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .onIO()
            .filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
            .filter(
                ConnectivityPredicate.hasType(
                    ConnectivityManager.TYPE_ETHERNET,
                    ConnectivityManager.TYPE_WIFI
                )
            ).flatMap { getDeviceSN() }
            .onUI({
                scopeNetLife {
                    val res = Get<String>("wx/device/login/${it}").await()
                    val obj = JSONObject(res)
                    val qrUrl = obj.get("result").toString()
                    val bitmap = QRCode.from(qrUrl).withSize(350, 350).bitmap()
                    loginQr.setImageBitmap(bitmap)
                }.catch {
                    Toast.makeText(this@LoginActivity, "获取二维码失败：${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }, {
                LogUtils.e(it)
            })
        guestLogin.setOnClickListener {

            UserDataHelper.userId = "guest"
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    @Subscribe
    fun onLogin(event: LoginEvent)
    {
        LogUtils.d(event)
        App.wxUser = event.auth.user
        UserDataHelper.userId = event.auth.accessToken.openId
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}