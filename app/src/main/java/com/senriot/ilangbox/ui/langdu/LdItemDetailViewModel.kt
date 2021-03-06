package com.senriot.ilangbox.ui.langdu

import androidx.databinding.ObservableField
import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.common.realm.songsConfig
import com.android.karaoke.player.events.ChangeBgmEvent
import com.arthurivanets.mvvm.AbstractViewModel
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LdItemDetailViewModel : AbstractViewModel()
{
//    var item: ReadItem = ReadItem()

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCleared()
    {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    val item = ObservableField<ReadItem>()

    @Subscribe
    fun bgmChanged(event: ChangeBgmEvent)
    {
        val i = item.get()
        Realm.getInstance(songsConfig).executeTransaction {
            i?.bgMusic = event.bgm
        }
    }

}
