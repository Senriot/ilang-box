package com.senriot.ilangbox

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.realm.UserDataHelper
import com.apkfuns.logutils.LogUtils
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.senriot.ilangbox.databinding.FragmentProfileDialogBinding
import com.senriot.ilangbox.event.LoginEvent
import com.senriot.ilangbox.ui.welcome.KaraokeRecordsFragment
import com.senriot.ilangbox.ui.welcome.LangduRecordsFragment
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment
import io.reactivex.disposables.Disposable
import net.glxn.qrgen.android.QRCode
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import universum.studios.android.fragment.manage.FragmentController


class ProfileDialogFragment : SupportBlurDialogFragment()
{

    private var networkDisposable: Disposable? = null
    private lateinit var viewDataBinding: FragmentProfileDialogBinding


    private val vm by sharedViewModel<MainActViewModel>()

    private val fragmentController by lazy {
        FragmentController(childFragmentManager).apply {
            viewContainerId = R.id.records_container
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDismiss(dialog: DialogInterface)
    {
        EventBus.getDefault().unregister(this)
        super.onDismiss(dialog)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        // Inflate the layout for this fragment
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        viewDataBinding = FragmentProfileDialogBinding.inflate(inflater)
        viewDataBinding.vm = vm
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.btnBack.setOnClickListener { this.dismiss() }
        viewDataBinding.rbLangduRecords.isChecked = true
        fragmentController.newRequest(LangduRecordsFragment()).immediate(true).execute()
        viewDataBinding.profileNav.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb_langdu_records)
            {
                fragmentController.newRequest(LangduRecordsFragment()).replaceSame(true).execute()
            }
            else
            {
                fragmentController.newRequest(KaraokeRecordsFragment()).replaceSame(true).execute()
            }
        }
        viewDataBinding.btnLogout.setOnClickListener {
            App.wxUser = null
            UserDataHelper.userId = GUEST
            vm.avatar.set("1")
            initViews()
            val checkedId = viewDataBinding.profileNav.checkedRadioButtonId
            if (checkedId == R.id.rb_langdu_records)
            {
                fragmentController.newRequest(LangduRecordsFragment()).replaceSame(true).execute()
            }
            else
            {
                fragmentController.newRequest(KaraokeRecordsFragment()).replaceSame(true).execute()
            }
        }
        initViews()
    }


    private fun initViews()
    {
        if (App.wxUser == null)
        {
            with(viewDataBinding) {
                avatar.visibility = View.INVISIBLE
                loginQr.visibility = View.VISIBLE
                btnLogout.visibility = View.INVISIBLE
            }
            vm.userName.set("扫码登录")
            networkDisposable = ReactiveNetwork.observeNetworkConnectivity(context)
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
                        viewDataBinding.loginQr.setImageBitmap(bitmap)
                    }.catch {
                        Toast.makeText(context, "获取二维码失败：${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }, {
                    LogUtils.e(it)
                })
        }
        else
        {
            vm.avatar.set(App.wxUser?.headImgUrl)
            vm.userName.set(App.wxUser?.nickname)
            with(viewDataBinding) {
                avatar.visibility = View.VISIBLE
                loginQr.visibility = View.INVISIBLE
                btnLogout.visibility = View.VISIBLE
            }
        }
    }

    override fun getDownScaleFactor(): Float
    {
        return 5.0F
    }

    override fun getBlurRadius(): Int
    {
        return 7
    }

    override fun isActionBarBlurred(): Boolean
    {
        return true
    }

    override fun isDimmingEnable(): Boolean
    {
        // Enable or disable the dimming effect.
        // Disabled by default.
        return true
    }

    override fun isRenderScriptEnable(): Boolean
    {
        // Enable or disable the use of RenderScript for blurring effect
        // Disabled by default.
        return true
    }

    @Subscribe
    fun onLogin(event: LoginEvent)
    {
        LogUtils.d(event)
        App.wxUser = event.auth.user
        UserDataHelper.userId = event.auth.accessToken.openId
        vm.avatar.set(App.wxUser?.headImgUrl)
        vm.userName.set(App.wxUser?.nickname)
        initViews()
        val checkedId = viewDataBinding.profileNav.checkedRadioButtonId
        if (checkedId == R.id.rb_langdu_records)
        {
            fragmentController.newRequest(LangduRecordsFragment()).replaceSame(true).execute()
        }
        else
        {
            fragmentController.newRequest(KaraokeRecordsFragment()).replaceSame(true).execute()
        }
//        EventBus.getDefault().post(LoginEvent(event.auth))
    }
}