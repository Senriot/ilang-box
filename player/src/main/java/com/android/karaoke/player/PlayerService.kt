package com.android.karaoke.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.view.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.graphics.drawable.DrawableCompat
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
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import com.zlm.hp.lyrics.LyricsReader
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

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
    private var accompany by Delegates.observable(Accompany.BC, { _, old, new ->
        if (old != new)
        {
            getTracks()?.let {

                if (it.size > 1)
                {
                    if (new == Accompany.YC)
                    {
                        mPlayer?.selectTrack(1)
                    } else
                    {
                        mPlayer?.selectTrack(2)
                    }
                } else if (it.size == 1)
                {
                    mPlayer?.setAudioChannel(if (new == Accompany.BC) 2 else 1)
                } else
                {

                }
            }
        }
    })
    private var audioRecorder: AudioRecorder? = null
    private var mBinding: VideoPresentationBinding? = null

    var readItem: ReadItem by Delegates.observable(ReadItem(), { _, old, new ->
        if (new != old && new.id.isNotEmpty())
        {
            displayType.set(1)
            mBinding?.lrcView?.initLrcData()
            if (!new.lyric.isNullOrBlank())
            {
                val s = LyricsReader()
                s.loadLrc(File(new.lyric))
                mBinding?.lrcView?.lyricsReader = s
            }
            if (new.bgm != null)
            {
                playBgm(new.bgm)
            }
        }
    })
//    private var readItem: ReadItem? = null
//        set(value)
//        {
//            field = value
//            value?.let {
//                displayType.set(1)
//                mBinding?.lrcView?.initLrcData()
//                if (!it.lyric.isNullOrBlank())
//                {
//                    val s = LyricsReader()
//                    s.loadLrc(File(it.lyric))
//                    mBinding?.lrcView?.lyricsReader = s
//                }
//                if (it.bgm != null)
//                {
//                    playBgm(it.bgm)
//                }
//            }
//        }
    //private var readBgm: ReadBgm? = null

    private var dzxxItem by Delegates.observable(DzXueXi(), { _, old, new ->
        if (old != new && new.uuid.isNotEmpty())
        {
            displayType.set(4)
            mBinding?.lrcView?.initLrcData()
            val reader = LyricsReader()
            reader.loadLrc(File(this.dzContentsPath + "/" + new.contentFile))
            mBinding?.lrcView?.lyricsReader = reader
            playDz(new)
        }
    })

    private var mBaseTimer = SystemClock.elapsedRealtime();

    val displayType = ObservableInt(0)  //显示类别，1：朗读 2:K歌 3:播放录音 4:党政

    val currentPlay = ObservableField<Song>()

    private var record: Record? = null

    private var timer: Disposable? = null

    /**
     * 党政音频路径
     */
    private val dzAudiosPath by lazy {
        Dict().queryFirst {
            equalTo("key", "dzxx_audio_path")
        }!!.value
    }

    /**
     * 党政内容路径
     */
    private val dzContentsPath by lazy {
        Dict().queryFirst {
            equalTo("key", "dzxx_content_path")
        }!!.value
    }

    fun initTimer(): Disposable?
    {
        timer = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                mPlayer?.let {
                    EventBus.getDefault()
                        .post(CurrentPositionEvent(it.duration, it.currentPosition))
                }
            }
        return timer
    }

    private val realm by lazy {
        Realm.getInstance(userConfig)
    }

//    private val mPlayer by lazy {
//        MediaPlayer().apply {
//            setOnErrorListener(mediaPlayerListeners)
//            setOnCompletionListener(mediaPlayerListeners)
//            setOnBufferingUpdateListener(mediaPlayerListeners)
//            setOnPreparedListener(mediaPlayerListeners)
//            setOnInfoListener(mediaPlayerListeners)
//            setOnSeekCompleteListener(mediaPlayerListeners)
//        }
//    }

    private var mPlayer: MediaPlayer? = null


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
            timer?.dispose()
            when
            {
                displayType.get() == 1 ->
                {
                    audioRecorder?.stop()
                    mBinding?.lrcView?.pause()
                    EventBus.getDefault().post(ReadingStop(record))
                    readItem = ReadItem()
                }
                displayType.get() == 3 ->
                {
                    mBinding?.lrcView?.pause()
                }
                displayType.get() == 4 ->
                {
                    mBinding?.lrcView?.pause()
                }
                else                   ->
                {
                    try
                    {
                        EventBus.getDefault().post(PlayerStatusEvent(PLAYER_STATUS_COMPLETION))
//                    audioRecorder?.stop()
//                    audioRecorder = null
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
                    } catch (e: Exception)
                    {
                        e.printStackTrace()
                    }

                }
            }
        }

        override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int)
        {
        }

        override fun onPrepared(mp: MediaPlayer?)
        {
            when
            {
                displayType.get() == 1 ->
                {
                    DspHelper.sendHex("484D00020200010501AA")
                    startLdReading(readItem)
                    mBinding?.lrcView?.play(0)
                    initTimer()
                    mp?.start()
                }
                displayType.get() == 3 ->
                {
                    mBinding?.lrcView?.play(0)
                    initTimer()
                    mp?.start()
                }
                displayType.get() == 4 ->
                {
                    mBinding?.lrcView?.play(0)
                    initTimer()
                    mp?.start()
                }
                else                   ->
                {

//                    mTrackAudioIndex.clear()
//                    mp?.trackInfo?.filter { it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO }
//                        ?.forEachIndexed { index, _ ->
//                            mTrackAudioIndex.add(index)
//                            trackNum += 1
//                        }
//                    currentTrack = userData?.currentPlay?.track
                    try
                    {

                        accompany = Accompany.BC
                        EventBus.getDefault().post(AccompanyChangedEvent(accompany))
                        DspHelper.sendHex("484D00020200010307AA")
                        mp?.start()
                    } catch (e: Exception)
                    {
                        e.printStackTrace()
                    }

//                    when (currentTrack?.trim())
//                    {
//                        "R"  -> accompany = Accompany.YC
//                        "L"  -> accompany = Accompany.BC
//                        else ->
//                        {
//                            if (trackNum > 1)
//                            {
//                                curAudioIndex = currentTrack?.toInt() ?: 0
//                                mp?.selectTrack(if (curAudioIndex == 0) 2 else 1)
//                            } else mp?.setAudioChannel(1)
//                        }
//                    }
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
//        var intent = Intent()
//        intent.action = "com.android.audio_mode"
//        intent.putExtra("audio_mode", 0)
//        sendBroadcast(intent)
//
//        intent = Intent()
//        intent.action = "com.ynh.set_spdif_pass_on_off"
//        intent.putExtra("pass_on", 1)
//        sendBroadcast(intent)
        if (!DspHelper.isOpen)
        {
            DspHelper.open()
            DspHelper.sendHex("484D0001000004AA")
        }
    }

    override fun onDestroy()
    {
        timer?.dispose()
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
        mBinding?.surfaceView?.visibility = View.VISIBLE
        newPlayer("/mnt/usb_storage/SATA/C/songs/" + song.file_name)
        try
        {
            mPlayer?.prepareAsync()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

//        mainDisplay?.let {
//            mPlayer?.setSurface(it.surface)
//            mPlayer?.setDataSource("/mnt/usb_storage/SATA/C/songs/" + song.file_name)
//            mPlayer?.prepareAsync()

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
//        }
    }

    /**
     * 播放背景音乐
     */
    private fun playBgm(bgm: ReadBgm?)
    {
        bgm?.let {
            mBinding?.surfaceView?.visibility = View.INVISIBLE
            audioRecorder?.stop()
            newPlayer(it.file)
            mPlayer?.prepareAsync()
            realm.executeTransaction { userData?.currentPlay = null }
        }
    }

    /**
     * 播放党政学习
     * @param item DzXueXi
     */
    private fun playDz(item: DzXueXi)
    {
        mBinding?.surfaceView?.visibility = View.INVISIBLE
        audioRecorder?.stop()
        newPlayer(dzAudiosPath + "/" + item.audioFile)
        mPlayer?.prepareAsync()
        realm.executeTransaction { userData?.currentPlay = null }
    }

    private fun newPlayer(path: String)
    {
        LogUtils.e(path)
        if (mPlayer != null)
        {
            timer?.dispose()
            mPlayer?.stop()
            mPlayer?.release()
        }
        mPlayer = MediaPlayer.create(this, Uri.parse("file://$path"), mainDisplay)
        mPlayer?.apply {
            setOnErrorListener(mediaPlayerListeners)
            setOnCompletionListener(mediaPlayerListeners)
            setOnBufferingUpdateListener(mediaPlayerListeners)
            setOnPreparedListener(mediaPlayerListeners)
            setOnInfoListener(mediaPlayerListeners)
            setOnSeekCompleteListener(mediaPlayerListeners)
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
        if (event.list.size == 1 && userData?.currentPlay == null && readItem.id.isEmpty())
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
                if (displayType.get() == 2)
                {
                    val b = (event.data as Boolean?) ?: false
                    if (b)
                    {
                        mPlayer?.pause()
//                    audioRecorder?.pause()
                    } else
                    {
                        mPlayer?.start()
//                    audioRecorder?.resume()
                    }
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
        if (mPlayer?.isPlaying == true)
        {
            if (isYC) accompany == Accompany.YC else accompany == Accompany.BC
        }
    }


    private fun getTracks(): List<MediaPlayer.TrackInfo>?
    {
        return mPlayer?.trackInfo?.filter { it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO }
    }

    @Subscribe
    fun onMinorDisplayInit(event: MinorDisplayInit)
    {
        mPlayer?.setMinorDisplay(event.holder)
    }

    private var uuid = ""

    @Subscribe
    fun onStartReading(event: StartRecordingEvent)
    {
        readItem = event.item
    }

    private fun startLdReading(item: ReadItem?)
    {
        if (item == null) return
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
        record = Record().apply {
            id = uuid
            readItem = item
            file = f
        }
        record!!.save()
        audioRecorder?.start()
    }

    @Subscribe
    fun stopReading(event: ReadingStopOfUser)
    {
        mPlayer?.stop()
        audioRecorder?.stop()
        mBinding?.lrcView?.pause()
        EventBus.getDefault().post(ReadingStop(record))
        timer?.dispose()
        readItem = ReadItem()
//        val record = Record().query { equalTo("id", uuid) }.first()
//        EventBus.getDefault().post(ReadingStop(record))
    }

    @Subscribe
    fun playRecord(event: PlayRecordEvent)
    {
        displayType.set(3)
        mBinding?.lrcView?.initLrcData()
        audioRecorder?.stop()
        timer?.dispose()
        //初始化歌词
        if (!event.record.readItem?.lyric.isNullOrBlank())
        {
            val s = LyricsReader()
            s.loadLrc(File(event.record.readItem!!.lyric))
            mBinding?.lrcView?.lyricsReader = s
        }
        mBinding?.surfaceView?.visibility = View.INVISIBLE
        newPlayer(event.record.file)
        mPlayer?.prepareAsync()
        realm.executeTransaction { userData?.currentPlay = null }
    }

    /**
     * 结束试听
     */
    @Subscribe
    fun stopAudition(event: StopAuditionEvent)
    {
        mPlayer?.stop()
        mBinding?.lrcView?.pause()
        timer?.dispose()
    }

    /**
     * 开始党政学习
     * @param event StartDzxxEvent 事件
     */
    @Subscribe
    fun startDzxx(event: StartDzxxEvent)
    {
        this.dzxxItem = event.item
    }


}
