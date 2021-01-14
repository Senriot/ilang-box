package com.senriot.ilangbox.ui.welcome

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.MvvmFragment
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LoginFragmentBinding
import com.senriot.ilangbox.getDeviceSN
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.login_fragment.*
import net.glxn.qrgen.android.QRCode
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : MvvmFragment<LoginFragmentBinding, LoginViewModel>(R.layout.login_fragment)
{

    companion object
    {
        fun newInstance() = LoginFragment()
    }

    private val vm by viewModel<LoginViewModel>()

    override fun createViewModel(): LoginViewModel = vm

    override val bindingVariable: Int = BR.vm

    private var networkDisposable: Disposable? = null
    override fun postInit()
    {
        super.postInit()
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
                    loginQr.setImageBitmap(bitmap)
                }.catch {
                    Toast.makeText(context, "获取二维码失败：${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }, {
                LogUtils.e(it)
            })
    }

    override fun onDestroyView()
    {
        networkDisposable?.dispose()
        super.onDestroyView()

    }
}