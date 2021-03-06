package com.android.karaoke.player.events

import android.view.Display
import android.view.SurfaceHolder
import com.android.karaoke.common.models.*
import com.android.karaoke.player.Accompany
import java.time.Duration


const val PLAYER_STATUS_COMPLETION = 100

class ShowPresoEvent(val display: Display)

class ClearPresoEvent(switchToInline: Boolean)

class PlayerControlEvent(val type: PlayEventType, val data: Any? = null)

class MinorDisplayInit(val holder: SurfaceHolder?)

class CurrentPositionEvent(val duration: Int, val currentPosition: Int)

class PlayerStatusEvent(val status: Int)

class StartRecordingEvent(val item: ReadItem)

class StopReadEvent()

class ReadingStop(val record: Record?)

class ReadingStopOfUser()

class BgmPlaying(val bgm: ReadBgm)

class ChangeBgmEvent(val bgm: ReadBgm)

class PlayRecordEvent(val record: Record)

class PlaySongRecordEvent(val songRecord: SongRecord)

class StopPlaySongRecordEvent(val songRecord: SongRecord)

class SongRecordPlaying(val songRecord: SongRecord)

class StopAuditionEvent()

class StartDzxxEvent(val currentItem: DzXueXi, val categoryId: String)

class AccompanyChangedEvent(val acc: Accompany)

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