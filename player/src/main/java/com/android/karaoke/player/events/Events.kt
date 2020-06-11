package com.android.karaoke.player.events

import android.view.Display
import android.view.SurfaceHolder
import java.time.Duration


const val PLAYER_STATUS_COMPLETION = 100

class ShowPresoEvent(val display: Display)

class ClearPresoEvent(switchToInline: Boolean)

class PlayerControlEvent(val type: PlayEventType, val data: Any? = null)

class MinorDisplayInit(val holder: SurfaceHolder)

class CurrentPositionEvent(val duration: Int, val currentPosition: Int)

class PlayerStatusEvent(val status: Int)


enum class PlayEventType
{
    PLAY_NEXT,
    MUTE,
    VOLUME,
    PAUSE,
    CHANNEL,
    REPLAY,
    START_RECORD,
    STOP_RECORD
}