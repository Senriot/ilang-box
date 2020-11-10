package com.senriot.ilangbox

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import com.android.karaoke.common.MvvmActivity
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.api.updateApiService
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.realm.SongsModule
import com.android.karaoke.common.realm.UserDataHelper
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.events.Command
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.senriot.ilangbox.databinding.ActivityInitBinding
import com.senriot.ilangbox.event.StartMainActEvent
import com.yanzhenjie.kalle.Kalle
import com.yanzhenjie.kalle.download.SimpleCallback
import com.yuan.library.dmanager.download.DownloadManager
import com.yuan.library.dmanager.download.DownloadTask
import com.yuan.library.dmanager.download.TaskEntity
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.RealmConfiguration
import io.supercharge.rxsnappy.RxSnappyClient
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class InitActivity : MvvmActivity<ActivityInitBinding, InitActViewModel>(R.layout.activity_init)
{

    private var networkDisposable: Disposable? = null
    val vm by viewModel<InitActViewModel>()
    override val bindingVariable: Int = BR.vm

    private val rxSnappyClient = RxSnappyClient()

    private val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/ilang-box"
    private val fileName = "db.realm"

    @SuppressLint("CheckResult")
    override fun postInit()
    {
        super.postInit()

        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .onIO().filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
            .filter(ConnectivityPredicate.hasType(ConnectivityManager.TYPE_ETHERNET))
            .onUI({
                networkDisposable?.dispose()
                getUpdateInfo()
            }, {
                LogUtils.e(it)
            })
    }

    override fun createViewModel(): InitActViewModel
    {
        return vm
    }

    private fun getUpdateInfo()
    {
        LogUtils.d("检测版本更新")
        updateApiService.updateInfo().onIO().onUI({
            LogUtils.d(it)
            if (it.success)
            {
                checkApp(it.result!!)
            }
        }, {
            it.printStackTrace()
        })
    }

    private fun checkApp(updateInfo: UpdateInfo)
    {
        val ver = packageManager.getPackageInfo(packageName, 0).versionName
        if (ver != updateInfo.appVer)
        {
            downloadApk(updateInfo)
        } else
        {
            rxSnappyClient.getString("dbVer").subscribe({
                if (it != updateInfo.dbVer)
                    downDbFile(updateInfo)
                else startMainAct()
            }, {
                it.printStackTrace()
                downDbFile(updateInfo)
            })
        }
    }

    private fun downloadApk(updateInfo: UpdateInfo)
    {
        val downloadTask = DownloadTask(
            TaskEntity.Builder().downloadId("downloadApk").url(updateInfo.appUrl).build()
        )
        downloadTask.setListener(vm.downloadTaskListener)
        DownloadManager.getInstance().addTask(downloadTask)

    }

    private fun downDbFile(updateInfo: UpdateInfo)
    {
        Kalle.Download.get(updateInfo.downloadUrl)
            .fileName(fileName)
            .directory(dbDir)
            .onProgress { progress, byteCount, speed -> vm.downloadProgress.set(progress) }
            .perform(object : SimpleCallback()
            {
                override fun onFinish(path: String?)
                {
                    super.onFinish(path)
                    rxSnappyClient.setString("dbVer", updateInfo.dbVer).subscribe { startMainAct() }
                }
            })
    }

    override fun onHandleCommand(command: Command<*>)
    {
        super.onHandleCommand(command)
        if (command is StartMainActEvent)
        {
            startMainAct()
        }
        LogUtils.d(command)
    }

    fun startMainAct()
    {
        Realm.init(applicationContext)
        val songsConfig = RealmConfiguration.Builder().modules(SongsModule())
            .directory(File(dbDir))
            .name(fileName)
            .build()
        Realm.setDefaultConfiguration(songsConfig)
        UserDataHelper.initUserData(null)
        startActivity(Intent(this@InitActivity, MainActivity::class.java))
        finish()
    }
}