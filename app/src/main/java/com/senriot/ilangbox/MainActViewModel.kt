package com.senriot.ilangbox

import androidx.databinding.ObservableField
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.event.MainNavChangedEvent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class MainActViewModel : AbstractViewModel()
{

    init
    {
        EventBus.getDefault().register(this)
    }

    private val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    val curDate = ObservableField<String>()

    val backgroundUri = ObservableField<String>("res://com.senriot.ilangbox/" + R.mipmap.xuexi_bg)

    val timer = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                val date = df.format(System.currentTimeMillis())
                curDate.set(date)
            }

    @Subscribe
    fun pageChanged(event: MainNavChangedEvent)
    {
        val uri = when (event.id)
        {
            R.id.rb_xuexi  -> R.mipmap.xuexi_bg
            R.id.rb_langdu -> R.mipmap.langdu_bg
            R.id.rb_hongge -> R.mipmap.kge_bg
            else           -> -1
        }
        backgroundUri.set("res://com.senriot.ilangbox/${uri}")
    }
}