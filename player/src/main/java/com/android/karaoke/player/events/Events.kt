package com.android.karaoke.player.events

import android.view.Display
import android.view.SurfaceHolder
import com.android.karaoke.common.models.ReadBgm
import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.common.models.Record
import java.time.Duration


const val PLAYER_STATUS_COMPLETION = 100

class ShowPresoEvent(val display: Display)

class ClearPresoEvent(switchToInline: Boolean)

class PlayerControlEvent(val type: PlayEventType, val data: Any? = null)

class MinorDisplayInit(val holder: SurfaceHolder)

class CurrentPositionEvent(val duration: Int, val currentPosition: Int)

class PlayerStatusEvent(val status: Int)

class StartRecordingEvent(val item: ReadItem)

class StopReadEvent()

class ReadingStop(val record: Record?)

class ReadingStopOfUser()

class BgmPlaying(val bgm: ReadBgm)

class ChangeBgmEvent(val bgm: ReadBgm)

class PlayRecordEvent(val record: Record)

class StopAuditionEvent()

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