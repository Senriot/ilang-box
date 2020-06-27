package com.android.karaoke.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.view.Display
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.events.ProfileDataInitEvent
import com.android.karaoke.common.models.*
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.databinding.VideoPresentationBinding
import com.android.karaoke.player.events.*
import com.android.karaoke.player.recorder.AudioRecorder
import com.apkfuns.logutils.LogUtils
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.save
import com.zlm.hp.lyrics.LyricsReader
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.kotlin.where
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
    private var userData: UserData? = null
    private val mTrackAudioIndex = Vector<Int>()
    private var trackNum = 0
    private var currentTrack: String? = null
    private var curAudioIndex: Int = 0
    private var accompany = Accompany.BC
    private var audioRecorder: AudioRecorder? = null
    private var mBinding: VideoPresentationBinding? = null
    private var readItem: ReadItem? = null
    private var readBgm: ReadBgm? = null

    private var mBaseTimer = SystemClock.elapsedRealtime();

    val displayType = ObservableInt(0)  //显示类别，1：朗读 2:K歌

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
        Realm.getInstance(userConfig)
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
            if (displayType.get() == 1)
            {
//                playBgm()
                audioRecorder?.stop()
            } else
            {
                EventBus.getDefault().post(PlayerStatusEvent(PLAYER_STATUS_COMPLETION))
                audioRecorder?.stop()
                audioRecorder = null
                userData?.let { data ->
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
        }

        override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int)
        {
        }

        override fun onPrepared(mp: MediaPlayer?)
        {
            if (displayType.get() == 1)
            {
                DspHelper.sendHex("484D00020200010501AA")
            } else
            {
                DspHelper.sendHex("484D00020200010307AA")
                mTrackAudioIndex.clear()
                mp?.trackInfo?.filter { it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO }
                    ?.forEachIndexed { index, _ ->
                        mTrackAudioIndex.add(index)
                        trackNum += 1
                    }
                currentTrack = userData?.currentPlay?.track
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
                        } else mp?.setAudioChannel(1)
                    }
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
        UserDataHelper.initUserData("Guest")
        var intent = Intent()
        intent.action = "com.android.audio_mode"
        intent.putExtra("audio_mode", 0)
        sendBroadcast(intent)

        intent = Intent()
        intent.action = "com.ynh.set_spdif_pass_on_off"
        intent.putExtra("pass_on", 1)
        sendBroadcast(intent)
        if (!DspHelper.isOpen)
        {
            DspHelper.open()
            DspHelper.sendHex("484D0001000004AA")
        }
    }

    override fun onDestroy()
    {
        timer.dispose()
        DspHelper.close()
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
        mBinding = VideoPresentationBinding.inflate(LayoutInflater.from(presoContext))
        val typeface = Typeface.createFromAsset(resources.assets, "font/xhei.ttf")
        mBinding!!.lrcView.apply {
            setTypeFace(typeface)
            setFontSize(38.0f)
        }
        mBinding!!.service = this
        mBinding!!.surfaceView.holder.addCallback(object : SurfaceHolder.Callback
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
//                mPlayer.setSurface(holder!!.surface)
            }

        })
        mWindowManager?.addView(mBinding!!.root, buildLayoutParams())
    }

    private fun playNext()
    {
        userData?.let { data ->
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
        mBinding?.lrcView?.pause()
        mBinding?.lrcView?.initLrcData()
        audioRecorder?.stop()
        displayType.set(2)
        mPlayer.stop()
        mPlayer.reset()
        mainDisplay?.let {
            mPlayer.setSurface(it.surface)
//            val path = File("/mnt/usb_storage/SATA/C/songs")
//            val files = path.listFiles()
//            val r = Random().nextInt(files.size)
//            val file = files[r].absolutePath
            mPlayer.setDataSource("/mnt/usb_storage/SATA/C/songs/" + song.file_name)
            mPlayer.prepareAsync()

//            if (!song.filePath.isNullOrEmpty())
//            {
//                if (File(song.filePath!!).exists())
//                {
//                    mainDisplay?.let { mPlayer.setDisplay(it) }
//                    mPlayer.setDataSource(song.filePath)
//                    mPlayer.prepareAsync()
//                }
//                else
//                {
//                    playNext()
//                }
//            }
//            else
//            {
//                playNext()
//            }
        }
    }

    /**
     * 播放背景音乐
     */
    private fun playBgm()
    {
        readBgm?.let {
            mPlayer.stop()
            mPlayer.reset()
            mPlayer.setDataSource(it.file)
            mPlayer.prepare()
            mPlayer.start()
            EventBus.getDefault().postSticky(BgmPlaying(it))
            realm.executeTransaction { userData?.currentPlay = null }
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
        if (!mPlayer.isPlaying && event.list.size == 1 && userData?.currentPlay == null)
        {
            playNext()
        }
    }

    @Subscribe
    fun onProfileDataInit(event: ProfileDataInitEvent)
    {
        userData = event.data
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
                LogUtils.e(event.data)
                if (event.data == 0)
                {
                    DspHelper.sendHex("484D000502002D032CAA")
                } else
                {
                    DspHelper.sendHex("484D000502002B032AAA")
                }
            }
            PlayEventType.PAUSE        ->
            {
                val b = (event.data as Boolean?) ?: false
                if (b)
                {
                    mPlayer.pause()
                    audioRecorder?.pause()
                } else
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
                userData?.currentPlay?.let { playSong(it) }
            }

            PlayEventType.START_RECORD ->
            {
                userData?.currentPlay?.let {
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
            } else
            {
                if (accompany == Accompany.BC) return
            }
//            userData?.currentPlay?.let {
//                when (it.track)
//                {
//                    "R"  -> mPlayer.setAudioChannel(if (isYC) 1 else 2)
//                    "L"  -> mPlayer.setAudioChannel(if (isYC) 2 else 1)
//                    else ->
//                    {
//                        if (trackNum > 1)
//                        {
//                            curAudioIndex = currentTrack?.toInt() ?: 0
//                            mPlayer.selectTrack(if (curAudioIndex == 0) 2 else 1)
//                        } else mPlayer.setAudioChannel(1)
//                    }
//                }
//            }
            val tracks =
                mPlayer.trackInfo?.filter { it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO }
            tracks?.let {
                if (it.isNotEmpty())
                {
                    when (it.size)
                    {
                        1    ->
                        {
                            accompany = if (accompany == Accompany.BC)
                            {
                                mPlayer.setAudioChannel(2)
                                Accompany.YC
                            } else
                            {
                                mPlayer.setAudioChannel(1)
                                Accompany.BC
                            }
                        }
                        else ->
                        {
                            accompany = if (accompany == Accompany.BC)
                            {
                                mPlayer.selectTrack(1)
                                Accompany.YC
                            } else
                            {
                                mPlayer.selectTrack(2)
                                Accompany.BC
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    fun onMinorDisplayInit(event: MinorDisplayInit)
    {
        mPlayer.setMinorDisplay(event.holder)
    }

    private var uuid = ""

    @Subscribe
    fun onStartReading(event: StartRecordingEvent)
    {
        displayType.set(1)
        readItem = event.item
        readBgm = if (event.item.bgm == null)
        {
            Realm.getDefaultInstance().where<ReadBgm>().findFirst()
        } else
        {
            event.item.bgm
        }
        playBgm()
        if (!event.item.lyric.isNullOrBlank())
        {
            LogUtils.i(event.item.lyric)
            val s = LyricsReader()
            s.loadLrc(File(event.item.lyric))
            mBinding?.lrcView?.lyricsReader = s
            mBinding?.lrcView?.play(0)
        }

        val dir = File("/sdcard/ilang-box/records")
        if (!dir.exists())
            dir.mkdirs()
        uuid = UUID.randomUUID().toString()
        val f = dir.absolutePath + "/" + uuid + ".mp3"
        if (audioRecorder != null)
        {
            audioRecorder?.stop()
            audioRecorder = null
        }
        audioRecorder = AudioRecorder(
            AudioRecorder.Format.MP3,
            File(f)
        )
        val record = Record().apply {
            id = uuid
            readItem = event.item
            file = f
        }
        record.save()
        audioRecorder?.start()
    }

    @Subscribe
    fun stopReading(event: StopReadEvent)
    {
        mPlayer.stop()
        mPlayer.reset()
        audioRecorder?.stop()
        mBinding?.lrcView?.pause()

        val record = Record().query { equalTo("id", uuid) }.first()
        EventBus.getDefault().post(ReadingEnd(record))
    }

    @Subscribe
    fun changeBgm(event: ChangeBgmEvent)
    {
        readBgm = event.bgm
        playBgm()
    }

    fun setPlayVolume(v: Float)
    {
        mPlayer.setVolume(v, v)
    }
}
