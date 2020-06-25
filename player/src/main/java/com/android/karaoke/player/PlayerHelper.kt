package com.android.karaoke.player

import com.android.karaoke.common.models.ReadBgm
import com.android.karaoke.player.events.ChangeBgmEvent
import org.greenrobot.eventbus.EventBus

object PlayerHelper
{
    @JvmStatic
    fun changeBgm(bgm: ReadBgm)
    {
        EventBus.getDefault().post(ChangeBgmEvent(bgm))
    }
}