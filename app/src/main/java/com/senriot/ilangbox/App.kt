package com.senriot.ilangbox

import android.app.Application
import android.content.Intent
import com.alibaba.fastjson.JSON
import com.android.karaoke.common.api.WxUser
import com.android.karaoke.common.preference.Preference
import com.android.karaoke.common.preference.core.annotation.CorePreference
import com.android.karaoke.common.realm.songsConfig
import com.drake.net.convert.DefaultConvert
import com.drake.net.initNet
import com.facebook.drawee.backends.pipeline.Fresco
import com.senriot.ilangbox.koin.ViewModels
import com.senriot.ilangbox.services.MessageService
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import com.yuan.library.dmanager.download.DownloadManager
import io.reactivex.Observable
import io.realm.Realm
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import universum.studios.android.fragment.annotation.FragmentAnnotations
import java.lang.reflect.Type

@CorePreference("ilang_sp")
class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        Preference.init(this)
        Fresco.initialize(this)
        FragmentAnnotations.setEnabled(true)
//        RxSnappy.init(this)
        DownloadManager.getInstance().init(this, 3)
        Realm.init(this)
        initNet("https://ilang.senriot.com/sery/ilang/") {
            converter(FastJsonConvert())
        }
        startKoin {
            androidLogger()

            androidContext(this@App)

            modules(ViewModels.module)
        }

        val i = Intent(this, MessageService::class.java)
        startService(i)
    }

    companion object
    {
        var wxUser: WxUser? = null
    }
}

class FastJsonConvert : DefaultConvert(code = "code", message = "message", success = "200")
{

    override fun <S> String.parseBody(succeed: Type): S?
    {
        val b = JSONObject(this)
        val result = b.getString("result")
        return JSON.parseObject(result, succeed)
    }
}

fun getDeviceSN(): Observable<String>
{
    val cmd = "busybox ifconfig eth0 | grep 'HWaddr' | busybox awk '{print $5}'"
    return Observable.create {
        RootTools.getShell(true).add(object : Command(1, cmd)
        {
            override fun commandOutput(id: Int, line: String)
            {
                super.commandOutput(id, line)
                val result = line.replace(":", "")
                it.onNext(result)
                it.onComplete()
            }
        })
    }
}

const val GUEST = "Guest"
