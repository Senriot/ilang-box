package com.senriot.ilang.launcher

import android.app.smdt.SmdtManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.api.updateApiService
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.realm.songsConfig
import com.apkfuns.logutils.LogUtils
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import com.yanzhenjie.kalle.Kalle
import com.yanzhenjie.kalle.download.SimpleCallback
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.kotlin.where
import io.supercharge.rxsnappy.RxSnappyClient
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainActivity : AppCompatActivity()
{
    private lateinit var networkDisposable: Disposable
    private val rxSnappyClient = RxSnappyClient()

    private val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/ilang-box"
    private val fileName = "db.realm"
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<TextView>(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .onIO().filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
            .filter(ConnectivityPredicate.hasType(ConnectivityManager.TYPE_ETHERNET))
            .onUI({
                networkDisposable.dispose()
                getUpdateInfo()
            }, {
                LogUtils.e(it)
            })
    }

    private fun getUpdateInfo()
    {
        LogUtils.d("检测版本更新")
        textView.text = "检测版本更新"
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
        try
        {
            val ver = packageManager.getPackageInfo("com.senriot.ilangbox", 0).versionName
            if (ver != updateInfo.appVer)
            {
                textView.text = "正在下载App"
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
        } catch (e: PackageManager.NameNotFoundException)
        {
            downloadApk(updateInfo)
        }

    }

    private fun downloadApk(updateInfo: UpdateInfo)
    {
        Kalle.Download.get(updateInfo.appUrl)
            .fileName("ilang-box.apk")
            .directory("/sdcard/ilang-box/apk")
            .onProgress { progress, byteCount, speed -> progressBar.progress = progress }
            .perform(object : SimpleCallback()
            {
                override fun onFinish(path: String)
                {
                    super.onFinish(path)
                    LogUtils.d(path)
                    textView.text = "正在安装App..."
                    val cmd = RootTools.getShell(true)
                        .add(Command(0, "pm install -i com.senriot.ilangbox -r $path"))
                    while (cmd.isFinished)
                    {
                        Thread.sleep(300)
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
            })
    }

    private fun downDbFile(updateInfo: UpdateInfo)
    {
        textView.text = "更新数据库..."
        Kalle.Download.get(updateInfo.downloadUrl)
            .fileName(fileName)
            .directory(dbDir)
            .onProgress { progress, byteCount, speed -> progressBar.progress = progress }
            .perform(object : SimpleCallback()
            {
                override fun onFinish(path: String?)
                {
                    super.onFinish(path)
                    Realm.init(applicationContext)
                    val realm = Realm.getInstance(songsConfig)
                    val songs = realm.where<Song>().findAll()
                    realm.executeTransaction {
                        songs.forEach {
                            val file = File(it.file_path + it.file_name)
                            it.exist = file.exists()
                        }
                    }
                    realm.close()
                    rxSnappyClient.setString("dbVer", updateInfo.dbVer).subscribe { startMainAct() }
                }
            })
    }

    private fun startMainAct()
    {
        val component = ComponentName("com.senriot.ilangbox", "com.senriot.ilangbox.MainActivity")
        val intent = Intent().apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setComponent(component)
        }
        startActivity(intent)
    }
}