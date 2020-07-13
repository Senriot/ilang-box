package com.senriot.ilangbox.ui.langdu

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.android.karaoke.common.models.Record
import com.android.karaoke.player.events.CurrentPositionEvent
import com.android.karaoke.player.events.ReadingStopOfUser
import com.android.karaoke.player.events.StartRecordingEvent
import com.android.karaoke.player.events.StopAuditionEvent
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat

class AuditionViewModel : AbstractViewModel()
{
    val timer = ObservableField<String>("00:00:00")

    val title = ObservableField<String>("")

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
        val hh = DecimalFormat("00").format(time / 60 / 60)
        val mm: String = DecimalFormat("00").format(time / 60)
        val ss: String = DecimalFormat("00").format(time % 60)
        timer.set("$hh:$mm:$ss")
    }

    fun onCompletion(view: View)
    {
        view.findNavController().popBackStack()
        EventBus.getDefault().post(StopAuditionEvent())
    }

    fun reRecording(view: View)
    {
        EventBus.getDefault().post(StartRecordingEvent(item.readItem!!))
        val args = Bundle()
        args.putSerializable("item",item.readItem!!)
        view.findNavController().navigate(
           R.id.ldRecordingFragment,args
        )
//        EventBus.getDefault().post(StartRecordingEvent(item.get()!!))
//        view.findNavController()
//            .navigate(ReadListFragmentDirections.actionReadListFragmentToLdItemDetailFragment(item.readItem!!))
    }
}
