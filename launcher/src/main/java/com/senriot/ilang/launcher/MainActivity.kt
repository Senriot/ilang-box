package com.senriot.ilang.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.onIO
import com.android.karaoke.common.onUI
import com.android.karaoke.common.realm.snappyClient
import com.android.karaoke.common.realm.sysParams
import com.android.karaoke.common.realm.userConfig
import com.apkfuns.logutils.LogUtils
import com.drake.net.Download
import com.drake.net.Get
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.snappydb.SnappydbException
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import com.yanzhenjie.kalle.NetCancel
import io.reactivex.disposables.Disposable
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File


class MainActivity : AppCompatActivity()
{
    private lateinit var networkDisposable: Disposable

    //    private val db =
//        DBFactory.open(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm")
//    private val rxSnappyClient = RxSnappyClient(db)
//    private val snappyClient by lazy { snappyClient(this) }
    private val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm"
    private val fileName = "db.realm"
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar
    private var updateInfo: UpdateInfo? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        LogcatViewer.showLogcatLoggerView(this);
        EventBus.getDefault().register(this)
        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
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
                        Toast.makeText(this, "初始化网络连接失败.", Toast.LENGTH_LONG).show()
                        try
                        {
                            packageManager.getPackageInfo("com.senriot.ilangbox", 0)
                            startMainAct()
                        }
                        catch (e: PackageManager.NameNotFoundException)
                        {
                            textView.text = "APP未安装，请连接网络更新安装";
                        }
                    }
                }
            }, {
                Toast.makeText(this, "初始化网络连接失败.", Toast.LENGTH_LONG).show()
                try
                {
                    packageManager.getPackageInfo("com.senriot.ilangbox", 0)
                    startMainAct()
                }
                catch (e: PackageManager.NameNotFoundException)
                {
                    textView.text = "APP未安装，请连接网络更新安装";
                }
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
        scopeDialog {
            updateInfo = Get<UpdateInfo>(
                "sysParams/queryByCode?code=app_update_info",
                uid = "updateInfo"
            ).await()
            updateInfo?.let { checkApp(it) }
        }.catch {
            startMainAct()
        }
    }

    private fun checkApp(updateInfo: UpdateInfo)
    {
        try
        {
            val ver = packageManager.getPackageInfo("com.senriot.ilangbox", 0).versionName
            val dbVer = updateInfo.paramsItems["dbVer"] ?: error("参数错误")
            updateInfo.paramsItems["appVer"]?.let { appVer ->
                if (ver != appVer.paramsValue)
                {
                    textView.text = "正在下载App..."
                    downloadApk(updateInfo)
                }
                else
                {
                    val dbFile = File("$dbDir/${dbVer.paramsValue}/$fileName")
                    if (!dbFile.exists())
                    {
                        downDbFile(updateInfo)
                    }
                    else
                    {
                        updateInfo.paramsItems["dbVer"]
                            ?.let { dbVer ->
                                val db = sysParams["dbVer"]
                                if (db != dbVer.paramsValue)
                                {
                                    downDbFile(updateInfo)
                                }
                                else
                                {
                                    startMainAct()
                                }
                            }
                    }
                }
            }

        }
        catch (e: PackageManager.NameNotFoundException)
        {
            downloadApk(updateInfo)
        }

    }

    private fun downloadApk(updateInfo: UpdateInfo)
    {
        scopeNetLife {
            updateInfo.paramsItems["appUrl"]?.let {
                val filePath = Download(it.paramsValue, absolutePath = true, uid = "downloadApk") {
                    onProgress { progress, byteCount, speed ->
                        progressBar.progress = progress
                    }
                }.await()
                LogUtils.d(filePath)
                textView.text = "正在安装App..."
                installApk(filePath)
            }
        }
    }

    private fun installApk(path: String)
    {
//        RootTools.
        RootTools.getShell(true)
            .add(object : Command(0, "pm install -r $path")
            {
                override fun commandCompleted(id: Int, exitcode: Int)
                {
                    super.commandCompleted(id, exitcode)
                    LogUtils.d("安装完成")
                }

                override fun commandOutput(id: Int, line: String?)
                {
                    super.commandOutput(id, line)
                    LogUtils.d(line)
//                    if (line == "Success")
//                    {
//
//                        val dbVer = sysParams.get("dbVer")
//                        updateInfo?.paramsItems?.get("dbVer")?.let {
//                            if (it.paramsValue != dbVer)
//                            {
//                                downDbFile(updateInfo!!)
//                            }
//                            else
//                            {
//                                startMainAct()
//                            }
//                        }
//                    }
                }
            })
    }

    private fun downDbFile(updateInfo: UpdateInfo)
    {
        textView.text = "更新数据库..."
        val dbVer = updateInfo.paramsItems["dbVer"] ?: error("aa")
        updateInfo.paramsItems["downloadUrl"]?.let {
            scopeNetLife {
                val filePath =
                    Download(
                        it.paramsValue,
                        dbDir + "/" + dbVer.paramsValue,
                        absolutePath = true,
                        uid = "downloadDb"
                    ) {
                        onProgress { progress, byteCount, speed -> progressBar.progress = progress }
                        fileName(fileName)
                    }.await()

                LogUtils.d(filePath)
                sysParams["DbFilePath"] = filePath
                sysParams["dbVer"] = dbVer.paramsValue
                startMainAct()
            }
        }
    }

    private fun startMainAct()
    {
        textView.text = ""
        progressBar.visibility = View.INVISIBLE
        sysParams.close()
        val realm = Realm.getInstance(userConfig)
        realm.executeTransaction {
            it.deleteAll()
        }
        realm.close()
        File("/mnt/usb_storage/SATA/C/langdu/records").delete()
        File("/mnt/usb_storage/SATA/C/songs/records").delete()
        val i = packageManager.getLaunchIntentForPackage("com.senriot.ilangbox")
        i?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(i)
    }

    @Subscribe
    fun appInstalled(event: AppInstalledEvent)
    {

        val dbVer = sysParams.get("dbVer")
        updateInfo?.paramsItems?.get("dbVer")?.let {
            if (it.paramsValue != dbVer)
            {
                downDbFile(updateInfo!!)
            }
            else
            {
                startMainAct()
            }
        }

//        updateInfo?.let { i ->
//            rxSnappyClient.getString("dbVer").subscribe({
//                if (it != i.dbVer)
//                    downDbFile(i)
//                else startMainAct()
//            }, {
//                it.printStackTrace()
//                downDbFile(i)
//            })
//        }

    }

    fun jumpUpdate(v: View)
    {
        try
        {
            NetCancel.cancel("updateInfo")
            NetCancel.cancel("downloadApk")
            NetCancel.cancel("downloadDb")
            startMainAct()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            startMainAct()
        }
    }
}