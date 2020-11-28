package com.senriot.ilang.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.karaoke.common.api.Api
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.models.DzXueXi
import com.android.karaoke.common.models.ReadBgm
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.realm.songsConfig
import com.android.karaoke.common.realm.userConfig
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

class MainActivity : AppCompatActivity()
{
    private lateinit var networkDisposable: Disposable
    private val rxSnappyClient = RxSnappyClient()

    private val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm"
    private val fileName = "db.realm"
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar
    private var updateInfo: UpdateInfo? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .onIO().filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
            .filter(ConnectivityPredicate.hasType(ConnectivityManager.TYPE_ETHERNET))
            .onUI({
                networkDisposable.dispose()
                this.getUpdateInfo()
            }, {
                LogUtils.e(it)
            })
    }

    override fun onDestroy()
    {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun getUpdateInfo()
    {
        LogUtils.d("检测版本更新")
        textView.text = "检测版本更新"
        GlobalScope.launch(Dispatchers.Main) {
            val res = Api.updateApiService.updateInfo().await()
            if (res.success)
            {
                updateInfo = res.result
                checkApp(res.result!!)
            }
        }

//        updateApiService.updateInfo().onIO().onUI({
//            LogUtils.d(it)
//            if (it.success)
//            {
//                updateInfo = it.result
//                checkApp(it.result!!)
//            }
//        }, {
//            it.printStackTrace()
//        })
    }

    private fun checkApp(updateInfo: UpdateInfo)
    {
        try
        {
            val ver = packageManager.getPackageInfo("com.senriot.ilangbox", 0).versionName
            if (ver != updateInfo.appVer)
            {
                textView.text = "正在下载App..."
                downloadApk(updateInfo)
            } else
            {
                val dbFile = File("$dbDir/$fileName")
                if (!dbFile.exists())
                {
                    downDbFile(updateInfo)
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
                    RootTools.getShell(true)
                        .add(Command(0, "pm install -i com.senriot.ilangbox -r $path"))
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
                    textView.text = "扫描本地文件..."
                    Realm.init(applicationContext)
                    var realm = Realm.getInstance(songsConfig)
                    val songs = realm.where<Song>().findAll()
                    realm.executeTransaction {
                        songs.forEach {
                            val file = File(it.file_path + it.file_name)
                            it.exist = file.exists()
                        }
                        it.where<DzXueXi>().findAll().forEach {
                            val file = File(it.audioPath + it.audioFileName)
                            it.exist = file.exists()
                        }
                        it.where<ReadBgm>().findAll().forEach {
                            val file = File(it.filePath + it.file_name)
                            it.fileExist = file.exists()
                        }
                    }
                    realm.close()
                    realm = Realm.getInstance(userConfig)
                    realm.executeTransaction {
                        it.deleteAll()
                    }
                    File("/mnt/usb_storage/SATA/C/langdu/records").delete()
                    File("/mnt/usb_storage/SATA/C/langdu/songs/records").delete()
                    rxSnappyClient.setString("dbVer", updateInfo.dbVer).subscribe { startMainAct() }
                }
            })
    }

    private fun startMainAct()
    {
        val i = packageManager.getLaunchIntentForPackage("com.senriot.ilangbox")
        i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        finish()
//        RePlugin.startActivity(
//            this, RePlugin.createIntent(
//                "ilangbox",
//                "com.senriot.ilangbox.MainActivity"
//            )
//        );
    }

    @Subscribe
    fun appInstalled(event: AppInstalledEvent)
    {
        updateInfo?.let { i ->
            rxSnappyClient.getString("dbVer").subscribe({
                if (it != i.dbVer)
                    downDbFile(i)
                else startMainAct()
            }, {
                it.printStackTrace()
                downDbFile(i)
            })
        }

    }
}