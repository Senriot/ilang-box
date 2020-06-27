package com.senriot.ilangbox.ui.karaoke

import androidx.lifecycle.ViewModel
import com.android.karaoke.player.events.PlayEventType
import com.android.karaoke.player.events.PlayerControlEvent
import com.arthurivanets.mvvm.AbstractViewModel
import org.greenrobot.eventbus.EventBus

class KaraokeViewModel : AbstractViewModel()
{

    fun playNext()
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.PLAY_NEXT, null))
    }

    val pause = fun(isPause: Boolean)
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.PAUSE, isPause))
    }

    val mute = fun(isMUte: Boolean)
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.MUTE, isMUte))
    }

    val channel = fun(isBC: Boolean)
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.CHANNEL, isBC))
    }

    fun volUp()
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.VOLUME, 1))
    }

    fun volDown()
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.VOLUME, 0))
    }

    fun onReplay()
    {
        EventBus.getDefault().post(PlayerControlEvent(PlayEventType.REPLAY, null))
    }

}
