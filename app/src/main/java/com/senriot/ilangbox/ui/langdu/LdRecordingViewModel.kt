package com.senriot.ilangbox.ui.langdu

import androidx.databinding.ObservableField
import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.player.events.BgmPlaying
import com.android.karaoke.player.events.CurrentPositionEvent
import com.arthurivanets.mvvm.AbstractViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.ObjectStreamField
import java.text.DecimalFormat

class LdRecordingViewModel : AbstractViewModel()
{


    val timer = ObservableField<String>("00:00")
    val curBgmName = ObservableField<String>("")

    val item = ObservableField<ReadItem>()

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCleared()
    {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onCurrentPosition(currentPosition: CurrentPositionEvent)
    {
        val time = currentPosition.currentPosition / 1000
        val mm: String = DecimalFormat("00").format(time / 60)
        val ss: String = DecimalFormat("00").format(time % 60)
        timer.set("$mm:$ss")
    }

    @Subscribe
    fun onBgmPlaying(event: BgmPlaying)
    {
        curBgmName.set(event.bgm.name)
    }
}
