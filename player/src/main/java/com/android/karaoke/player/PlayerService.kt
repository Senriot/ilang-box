package com.android.karaoke.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.view.Display
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.ObservableField
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.events.ProfileDataInitEvent
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.models.SystemParams
import com.android.karaoke.common.realm.appConfig
import com.android.karaoke.player.databinding.VideoPresentationBinding
import com.android.karaoke.player.events.*
import com.android.karaoke.player.recorder.AudioRecorder
import com.apkfuns.logutils.LogUtils
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.annotations.Ignore
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerService : Service(), PresentationHelper.Listener
{

    init
    {
        EventBus.getDefault().register(this)
    }

    private var mWindowManager: WindowManager? = null
    private var mainDisplay: SurfaceHolder? = null
    private var profileData: SystemParams? = null
    private val mTrackAudioIndex = Vector<Int>()
    private var trackNum = 0
    private var currentTrack: String? = null
    private var curAudioIndex: Int = 0
    private var accompany = Accompany.BC
    private var audioRecorder: AudioRecorder? = null

    val currentPlay = ObservableField<Song>()

    private val timer = Observable.interval(1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .subscribe {
            if (mPlayer.isPlaying)
            {
                EventBus.getDefault()
                    .post(CurrentPositionEvent(mPlayer.duration, mPlayer.currentPosition))
            }
        }


    private val realm by lazy {
        Realm.getInstance(appConfig)
    }

    private val mPlayer by lazy {
        MediaPlayer().apply {
            setOnErrorListener(mediaPlayerListeners)
            setOnCompletionListener(mediaPlayerListeners)
            setOnBufferingUpdateListener(mediaPlayerListeners)
            setOnPreparedListener(mediaPlayerListeners)
            setOnInfoListener(mediaPlayerListeners)
            setOnSeekCompleteListener(mediaPlayerListeners)
        }
    }

    private val mediaPlayerListeners = object : MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener
    {
        override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean
        {
            return false
        }

        override fun onCompletion(mp: MediaPlayer?)
        {
            EventBus.getDefault().post(PlayerStatusEvent(PLAYER_STATUS_COMPLETION))
            audioRecorder?.stop()
            audioRecorder = null
            profileData?.let { data ->
                if (data.currentPlay != null)
                {
                    realm.executeTransaction {
                        data.history.add(0, data.currentPlay)
                        data.currentPlay = null
                    }
                }
            }
            playNext()
        }

        override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int)
        {
        }

        override fun onPrepared(mp: MediaPlayer?)
        {
            mTrackAudioIndex.clear()
            mp?.trackInfo?.filter { it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO }
                ?.forEachIndexed { index, _ ->
                    mTrackAudioIndex.add(index)
                    trackNum += 1
                }
            currentTrack = profileData?.currentPlay?.amTrack
            mp?.start()
            when (currentTrack?.trim())
            {
                "R"  -> mp?.setAudioChannel(2)
                "L"  -> mp?.setAudioChannel(1)
                else ->
                {
                    if (trackNum > 1)
                    {
                        curAudioIndex = currentTrack?.toInt() ?: 0
                        mp?.selectTrack(if (curAudioIndex == 0) 2 else 1)
                    }
                    else mp?.setAudioChannel(1)
                }
            }
        }

        override fun onSeekComplete(mp: MediaPlayer?)
        {
        }

        override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean
        {
            return true
        }
    }

    private val presentationHelper by lazy {
        PresentationHelper(this, this)
    }

    override fun onBind(intent: Intent): IBinder
    {
        return PlayerServiceBinder()
    }

    override fun onCreate()
    {
        super.onCreate()
        presentationHelper.onResume()
    }

    override fun onDestroy()
    {
        timer.dispose()
        super.onDestroy()
    }


    inner class PlayerServiceBinder : Binder()
    {
        fun getService(): PlayerService
        {
            return this@PlayerService
        }
    }

    override fun showPreso(display: Display)
    {
        val presoContext = createPresoContext(display)
        mWindowManager = presoContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val viewBinding = VideoPresentationBinding.inflate(LayoutInflater.from(presoContext))
        viewBinding.service = this
        viewBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback
        {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            )
            {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?)
            {
                mainDisplay = null
            }

            override fun surfaceCreated(holder: SurfaceHolder?)
            {
                mainDisplay = holder
                mPlayer.setSurface(holder!!.surface)
            }

        })
        mWindowManager?.addView(viewBinding.root, buildLayoutParams())
    }

    private fun playNext()
    {
        profileData?.let { data ->
            data.currentPlay?.let { s ->
                realm.executeTransaction {
                    data.history.add(0, s)
                    data.currentPlay = null
                }
            }
            if (data.playlist.isNotEmpty())
            {
                realm.beginTransaction()
                val item = data.playlist.removeAt(0)
                data.currentPlay = item
                realm.commitTransaction()
                playSong(item)
                currentPlay.set(item)
            }
        }
    }

    private fun playSong(song: Song)
    {
        mPlayer.stop()
        mPlayer.reset()
        mainDisplay?.let {
            if (!song.filePath.isNullOrEmpty())
            {
                if (File(song.filePath!!).exists())
                {
                    mPlayer.setDataSource(song.filePath)
                    mPlayer.prepareAsync()
                }
                else
                {
                    playNext()
                }
            }
            else
            {
                playNext()
            }
        }
    }

    override fun clearPreso(switchToInline: Boolean)
    {
        EventBus.getDefault().post(ClearPresoEvent(switchToInline))
    }

    private fun buildLayoutParams(): WindowManager.LayoutParams
    {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            0,
            0,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            0, PixelFormat.OPAQUE
        )
    }

    private fun createPresoContext(display: Display): ContextThemeWrapper
    {
        val displayContext = createDisplayContext(display)
        val windowManager = displayContext.getSystemService(Context.WINDOW_SERVICE)
        return object : ContextThemeWrapper(displayContext, 0)
        {
            override fun getSystemService(name: String): Any
            {
                if (Context.WINDOW_SERVICE == name)
                    return windowManager
                return super.getSystemService(name)
            }
        }
    }

    @Subscribe
    fun playlistChanged(event: PlaylistChangedEvent)
    {
        if (!mPlayer.isPlaying && event.list.size == 1 && profileData?.currentPlay == null)
        {
            playNext()
        }
    }

    @Subscribe
    fun onProfileDataInit(event: ProfileDataInitEvent)
    {
        profileData = event.data
        mainDisplay?.let {
            playNext()
        }
    }

    @Subscribe
    fun onPlayControl(event: PlayerControlEvent)
    {
        LogUtils.d(event.type)
        when (event.type)
        {
            PlayEventType.PLAY_NEXT    -> playNext()
            PlayEventType.MUTE         ->
            {
            }
            PlayEventType.VOLUME       ->
            {
            }
            PlayEventType.PAUSE        ->
            {
                val b = (event.data as Boolean?) ?: false
                if (b)
                {
                    mPlayer.pause()
                    audioRecorder?.pause()
                }
                else
                {
                    mPlayer.start()
                    audioRecorder?.resume()
                }
            }
            PlayEventType.CHANNEL      ->
            {
                changeChanel((event.data ?: false) as Boolean)
            }
            PlayEventType.REPLAY       ->
            {
                profileData?.currentPlay?.let { playSong(it) }
            }

            PlayEventType.START_RECORD ->
            {
                profileData?.currentPlay?.let {
                    val file = File("/sdcard/AudioBank/AudioRecords")
                    if (!file.exists())
                        file.mkdirs()


                    val fileName = UUID.randomUUID().toString() + ".mp3"

                    if (audioRecorder != null)
                    {
                        audioRecorder?.stop()
                        audioRecorder = null
                    }
                    audioRecorder = AudioRecorder(
                        AudioRecorder.Format.MP3,
                        File(file.absolutePath + "/" + fileName)
                    )
                    playSong(it)
                    audioRecorder?.start()
                }

            }
            PlayEventType.STOP_RECORD  ->
            {
                audioRecorder?.stop()
                audioRecorder = null
            }
        }
    }

    private fun changeChanel(isYC: Boolean)
    {
        if (mPlayer.isPlaying)
        {
            if (isYC)
            {
                if (accompany == Accompany.YC) return
            }
            else
            {
                if (accompany == Accompany.BC) return
            }
            profileData?.currentPlay?.let {
                when (it.amTrack)
                {
                    "R"  -> mPlayer.setAudioChannel(if (isYC) 1 else 2)
                    "L"  -> mPlayer.setAudioChannel(if (isYC) 2 else 1)
                    else ->
                    {
//                        if (trackNum > 1)
//                        {
//                            curAudioIndex = currentTrack?.toInt() ?: 0
//                            mPlayer.selectTrack(if (curAudioIndex == 0) 2 else 1)
//                        } else mPlayer.setAudioChannel(1)
                    }
                }
            }
//            val tracks =
//                mPlayer.trackInfo?.filter { it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO }
//            tracks?.let {
//                if (it.isNotEmpty())
//                {
//                    when (it.size)
//                    {
//                        1    ->
//                        {
//                            accompany = if (accompany == Accompany.BC)
//                            {
//                                mPlayer.setAudioChannel(2)
//                                Accompany.YC
//                            } else
//                            {
//                                mPlayer.setAudioChannel(1)
//                                Accompany.BC
//                            }
//                        }
//                        else ->
//                        {
//                            accompany = if (accompany == Accompany.BC)
//                            {
//                                mPlayer.selectTrack(1)
//                                Accompany.YC
//                            } else
//                            {
//                                mPlayer.selectTrack(2)
//                                Accompany.BC
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    @Subscribe
    fun onMinorDisplayInit(event: MinorDisplayInit)
    {
        mPlayer.setMinorDisplay(event.holder)
    }


    fun setPlayVolume(v: Float)
    {
        mPlayer.setVolume(v, v)
    }
}
