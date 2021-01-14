package com.senriot.ilangbox.ui.xuexi

import androidx.databinding.ObservableField
import com.android.karaoke.common.models.DzXueXi
import com.android.karaoke.common.realm.songsConfig
import com.android.karaoke.player.events.PlayEventType
import com.android.karaoke.player.events.PlayerControlEvent
import com.android.karaoke.player.events.StartDzxxEvent
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import io.realm.Realm
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

class DzContentViewModel : AbstractViewModel()
{
    val item = ObservableField<DzXueXi>()

    var categoryId = ""

    private var curIndex = 0
    private val items by lazy {
        val r = Realm.getInstance(songsConfig).where<DzXueXi>().equalTo("type", categoryId).findAll()
        curIndex = r.indexOf(item.get())
        r
    }

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCleared()
    {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }


    fun onNext()
    {
        if (items.size > curIndex + 1)
        {
            try
            {
                items[curIndex + 1]?.let {
                    LogUtils.e(it)
                    curIndex += 1
                    val file = File(it.audioPath + it.audioFileName)
                    if (file.exists())
                    {
                        EventBus.getDefault().post(StartDzxxEvent(it, categoryId))
                    }
                    else
                    {
                        onNext()
                    }
                }
            }
            catch (e: Exception)
            {

            }
        }
        else
        {
            curIndex = items.size - 1
        }
    }

    fun onPrevious()
    {
        if (curIndex > 0)
        {
            try
            {


                items[curIndex - 1]?.let {
                    curIndex -= 1
                    val file = File(it.audioPath + it.audioFileName)
                    if (file.exists())
                    {
                        EventBus.getDefault().post(StartDzxxEvent(it, categoryId))
                    }
                    else
                    {
                        onPrevious()
                    }
                }
            }
            catch (e: Exception)
            {

            }
        }
        else
        {
            curIndex = items.size - 1
        }
    }

    val pause = fun(isPause: Boolean)
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.PAUSE, isPause))
    }

    @Subscribe
    fun startDzxx(event: StartDzxxEvent)
    {
        item.set(event.currentItem)
    }
}
