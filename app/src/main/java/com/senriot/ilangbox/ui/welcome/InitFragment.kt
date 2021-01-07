package com.senriot.ilangbox.ui.welcome

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.preference.SPService
import com.arthurivanets.mvvm.MvvmFragment
import com.drake.net.Download
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.LoginActivity
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.InitFragmentBinding
import io.reactivex.disposables.Disposable
import org.koin.android.viewmodel.ext.android.viewModel

class InitFragment : MvvmFragment<InitFragmentBinding, InitViewModel>(R.layout.init_fragment)
{
    private val fileName = "db.realm"
    private val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm"
    private val vm by viewModel<InitViewModel>()

    override val bindingVariable: Int = BR.vm

    companion object
    {
        fun newInstance() = InitFragment()
    }

    override fun createViewModel(): InitViewModel = vm
    private lateinit var networkDisposable: Disposable
    override fun performDataBinding()
    {
        super.performDataBinding()
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(context)
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
        scopeNetLife {
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
        else
        {
            SPService().userFirst = false
            (activity as LoginActivity).fragmentController.newRequest(WelcomeFragments.LOGIN)
                .immediate(true).execute()
        }
    }

    private fun downDbFile(
        dbParams: UpdateInfo.SysParamsItem,
        downloadParams: UpdateInfo.SysParamsItem
    )
    {
        scopeNetLife {
            val filePath = Download(
                downloadParams.paramsValue,
                dbDir + "/" + dbParams.paramsValue,
                absolutePath = true,
                uid = "downloadDb"
            ) {
                onProgress { progress, byteCount, speed ->
                    vm.progress.set(progress)
                    vm.title.set("初始化数据 %$progress")
                }
                fileName(fileName)
            }.await()
            SPService().apply {
                this.dbFilePath = filePath
                this.dbVer = dbParams.paramsValue
                this.userFirst = false
            }
            (activity as LoginActivity).fragmentController.newRequest(WelcomeFragments.LOGIN)
                .immediate(true).execute()
        }
    }
}