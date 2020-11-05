package com.senriot.ilangbox

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.android.karaoke.common.MvvmActivity
import com.android.karaoke.common.api.DbVer
import com.android.karaoke.common.api.dbVerService
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.realm.SongsModule
import com.apkfuns.logutils.LogUtils
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.senriot.ilangbox.databinding.ActivityInitBinding
import com.yanzhenjie.kalle.Kalle
import com.yanzhenjie.kalle.download.SimpleCallback
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import io.supercharge.rxsnappy.RxSnappyClient
import io.supercharge.rxsnappy.RxSnappyUtils
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class InitActivity : MvvmActivity<ActivityInitBinding, InitActViewModel>(R.layout.activity_init)
{

    private var networkDisposable: Disposable? = null
    val vm by viewModel<InitActViewModel>()
    override val bindingVariable: Int = BR.vm

    private val rxSnappyClient = RxSnappyClient()

    private val dbDir = "/sdcard/ilang-box"
    private val fileName = "db.realm"

    @SuppressLint("CheckResult")
    override fun postInit()
    {
        super.postInit()
        checkDbVer()
//        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
//            .onIO().filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
//            .filter(ConnectivityPredicate.hasType(ConnectivityManager.TYPE_WIFI))
//            .onUI({
//                networkDisposable?.dispose()
//                dbVerService.getDbVer().subscribeOn(Schedulers.io())
//                    .subscribe {
//                        if (it.success)
//                        {
//                            it.result?.downloadUrl?.let { url ->
//                                vm.downloadFile(url)
//                            }
//                        }
//                    }
//            }, {
//                LogUtils.e(it)
//            })
    }

    override fun createViewModel(): InitActViewModel
    {
        return vm
    }

    @SuppressLint("CheckResult")
    fun checkDbVer()
    {
        dbVerService.getDbVer().subscribeOn(Schedulers.io())
            .subscribe {
                if (it.success)
                {
                    rxSnappyClient.getString("dbver").subscribe({ ver ->
                        if (ver != it.result?.dbver)
                        {
                            it.result?.let { dbver ->
                                downloadFile(dbver)
                            }
                        } else
                        {
                            startMainAct()
                        }
                    }, { err ->
                        err.printStackTrace()
                        it.result?.let { dbver ->
                            downloadFile(dbver)
                        }
                    })
                }
            }
    }

    @SuppressLint("SdCardPath")
    fun downloadFile(dbVer: DbVer)
    {
        val dbDir = "/sdcard/ilang-box"
        val fileName = "db.realm"

        Kalle.Download.get(dbVer.downloadUrl)
            .directory(dbDir)
            .fileName(fileName)
            .onProgress { progress, byteCount, speed ->
                vm.downloadProgress.set(progress)
            }.perform(object : SimpleCallback()
            {
                override fun onFinish(path: String?)
                {
                    rxSnappyClient.setString("dbver", dbVer.dbver).subscribe {
                        startMainAct()
                    }
                }
            })
    }

    fun startMainAct()
    {
        Realm.init(applicationContext)
        val songsConfig = RealmConfiguration.Builder().modules(SongsModule())
            .directory(File(dbDir))
            .name(fileName)
            .build()
        Realm.setDefaultConfiguration(songsConfig)
        startActivity(Intent(this@InitActivity, MainActivity::class.java))
        finish()
    }
}