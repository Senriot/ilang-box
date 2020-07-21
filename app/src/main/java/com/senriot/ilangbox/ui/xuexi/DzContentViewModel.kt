package com.senriot.ilangbox.ui.xuexi

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.android.karaoke.common.models.DzXueXi
import com.android.karaoke.player.events.PlayEventType
import com.android.karaoke.player.events.PlayerControlEvent
import com.android.karaoke.player.events.StartDzxxEvent
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import io.realm.Realm
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DzContentViewModel : AbstractViewModel()
{
    val item = ObservableField<DzXueXi>()

    var categoryId = 123L


    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCleared()
    {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

//    private val items by lazy {
//        Realm.getDefaultInstance().where<DzXueXi>().equalTo("category.id", categoryId).findAll()
//    }

    fun onNext()
    {
        val items = Realm.getDefaultInstance().where<DzXueXi>().equalTo("category.id", categoryId).findAll()
        val index = items.indexOf(item.get())
        LogUtils.e("${index}  ${items.size}")
        if (items.size > index + 1)
            items[index + 1]?.let {
                LogUtils.e(it)
                EventBus.getDefault().post(StartDzxxEvent(it))
            }
    }

    fun onPrevious()
    {
        val items = Realm.getDefaultInstance().where<DzXueXi>().equalTo("category.id", categoryId).findAll()
        val index = items.indexOf(item.get())
        if (index > 0)
            items[index - 1]?.let {
                LogUtils.e(it)
                EventBus.getDefault().post(StartDzxxEvent(it))
            }
    }

    val pause = fun(isPause: Boolean)
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.PAUSE, isPause))
    }

    @Subscribe
    fun startDzxx(event: StartDzxxEvent)
    {
        item.set(event.item)
    }
}
