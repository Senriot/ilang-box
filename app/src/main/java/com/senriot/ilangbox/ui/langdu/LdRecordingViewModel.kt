package com.senriot.ilangbox.ui.langdu

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.navigation.findNavController
import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.common.models.Record
import com.android.karaoke.player.events.*
import com.arthurivanets.mvvm.AbstractViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.ObjectStreamField
import java.text.DecimalFormat

class LdRecordingViewModel : AbstractViewModel()
{
    val timer = ObservableField<String>("00:00")
    val curBgmName = ObservableField<String>("")
    val recordCompletion = ObservableBoolean(false)
    val title = ObservableField("正在录音...")
    private var record: Record? = null

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
    fun onRecordStop(event: ReadingStop)
    {
        record = event.record
        recordCompletion.set(true)
    }

    @Subscribe
    fun onBgmPlaying(event: BgmPlaying)
    {
        curBgmName.set(event.bgm.name)
    }

    fun onCompletion()
    {
        recordCompletion.set(true)
        EventBus.getDefault().post(ReadingStopOfUser())
    }

    fun reStart()
    {
        EventBus.getDefault().post(StartRecordingEvent(item.get()!!))
        recordCompletion.set(false)
    }

    fun playRecord(view: View)
    {
        if (record != null)
        {
            EventBus.getDefault().post(PlayRecordEvent(record!!))
            view.findNavController().navigate(
                LdRecordingFragmentDirections.actionLdRecordingFragmentToAuditionFragment(record!!)
            )
        }
    }
}
