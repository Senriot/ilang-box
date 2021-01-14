package com.senriot.ilangbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.karaoke.common.preference.SPService
import com.android.karaoke.common.realm.UserDataHelper
import com.apkfuns.logutils.LogUtils
import com.senriot.ilangbox.event.LoginEvent
import com.senriot.ilangbox.ui.welcome.WelcomeFragments
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import universum.studios.android.fragment.annotation.FragmentAnnotations
import universum.studios.android.fragment.manage.FragmentController

class LoginActivity : AppCompatActivity()
{

    override fun onDestroy()
    {
        EventBus.getDefault().unregister(this)
        networkDisposable?.dispose()
        super.onDestroy()
    }

    private var networkDisposable: Disposable? = null
    lateinit var fragmentController: FragmentController

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        fragmentController = FragmentController(this, supportFragmentManager)
        fragmentController.viewContainerId = R.id.container
        fragmentController.factory = WelcomeFragments()
        when
        {
            SPService().userFirst ->
            {
                fragmentController.newRequest(WelcomeFragments.INIT).immediate(true).execute()
            }
            App.wxUser != null    ->
            {
                fragmentController.newRequest(WelcomeFragments.PROFILE).immediate(true).execute()
            }
            else                  ->
            {
                fragmentController.newRequest(WelcomeFragments.LOGIN).immediate(true).execute()
            }
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

    fun goBack(view: View)
    {
        onBackPressed()
    }

    fun guestLogin(view: View)
    {
        UserDataHelper.userId = GUEST
        App.wxUser = null
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun logout(view: View)
    {
        App.wxUser = null
        fragmentController.newRequest(WelcomeFragments.LOGIN).immediate(true).execute()
    }

}