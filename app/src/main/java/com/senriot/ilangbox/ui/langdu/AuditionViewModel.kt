package com.senriot.ilangbox.ui.langdu

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.android.karaoke.common.models.Record
import com.android.karaoke.player.events.CurrentPositionEvent
import com.android.karaoke.player.events.ReadingStopOfUser
import com.android.karaoke.player.events.StopAuditionEvent
import com.arthurivanets.mvvm.AbstractViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat

class AuditionViewModel : AbstractViewModel()
{
    val timer = ObservableField<String>("00:00")

    lateinit var item: Record

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

    fun onCompletion(view: View)
    {
        view.findNavController().popBackStack()
        EventBus.getDefault().post(StopAuditionEvent())
    }
}