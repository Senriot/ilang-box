package com.android.karaoke.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.util.Base64
import android.view.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.ObservableField
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.events.ProfileDataInitEvent
import com.android.karaoke.common.models.*
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.databinding.VideoPresentationBinding
import com.android.karaoke.player.events.*
import com.android.karaoke.player.recorder.AudioRecorder
import com.apkfuns.logutils.LogUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.nio.charset.Charset
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

//    private val mTrackAudioIndex = Vector<Int>()
//    private var trackNum = 0
//    private var currentTrack: String? = null
//    private var curAudioIndex: Int = 0

    private var displayType by Delegates.observable(0, { _, oldValue, newValue ->
        if (oldValue != newValue)
        {
            when (newValue)
            {
                DISPLAY_TYPE_LANGDU -> DspHelper.sendHex("484D00020200010501AA")
                DISPLAY_TYPE_SONG   -> DspHelper.sendHex("484D00020200010307AA")
                else                ->
                {

                }
            }

        }
    })

    private var accompany by Delegates.observable(Accompany.Unknown, { _, old, new ->
        getTracks()?.let {

            if (it.size > 1)
            {
                if (new == Accompany.YC)
                {
                    mPlayer?.selectTrack(1)
                }
                else
                {
                    mPlayer?.selectTrack(2)
                }
            }
            else
            {
                mPlayer?.setAudioChannel(if (new == Accompany.BC) 2 else 1)
            }
        }
    })
    private var audioRecorder: AudioRecorder? = null
    private var mBinding: VideoPresentationBinding? = null


    var readItem: ReadItem by Delegates.observable(ReadItem(), { _, old, new ->
        if (new != old && new.id.isNotEmpty())
        {
            displayType = DISPLAY_TYPE_LANGDU
            if (!new.lyric.isNullOrBlank())
            {
                val lrc =
                    Base64.decode(new.lyric, Base64.NO_WRAP).toString(Charset.defaultCharset())
                mBinding?.lrcView?.loadLrc(lrc)
            }
            new.bgMusic?.let { playBgm(it) }
        }
    })

    private var dzxxItem by Delegates.observable(DzXueXi(), { _, old, new ->
        if (old != new && new.id.isNotEmpty())
        {
            displayType = DISPLAY_TYPE_DANGZHENG
            val lrc =
                Base64.decode(new.subtitle, Base64.NO_WRAP).toString(Charset.defaultCharset())
            mBinding?.lrcView?.loadLrc(lrc)
            playDz(new)
        }
    })

    private var curSong by Delegates.observable(Song(), { _, old, new ->
        audioRecorder?.stop()
        displayType = DISPLAY_TYPE_SONG
        mBinding?.surfaceView?.visibility = View.VISIBLE
        val path = "file://${new.file_path}${new.file_name}"
        LogUtils.d("播放 $new")
        newPlayer(path)
    })


    private var mBaseTimer = SystemClock.elapsedRealtime();


    val currentPlay = ObservableField<Song>()

    private var record: Record? = null

    private var songRecord: SongRecord? = null
    private var curSongRecord: SongRecord? = null

    private var timer: Disposable? = null

    fun initTimer(): Disposable?
    {
        LogUtils.d("初始化计时器")
        timer = Observable.interval(300, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {

                mPlayer?.let {
                    mBinding?.lrcView?.updateTime(it.currentPosition.toLong())
                    EventBus.getDefault()
                        .post(CurrentPositionEvent(it.duration, it.currentPosition))
                }
            }
        return timer
    }

    private val realm by lazy {
        Realm.getInstance(userConfig)
    }

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
            LogUtils.e("播放错误 $what $extra")
            return false
        }

        override fun onCompletion(mp: MediaPlayer?)
        {
            timer?.dispose()
            when (displayType)
            {
                DISPLAY_TYPE_LANGDU ->
                {
                    audioRecorder?.stop()
                    EventBus.getDefault().post(ReadingStop(record))
                    readItem = ReadItem()
                }
                DISPLAY_TYPE_SONGRECORD ->
                {
                    curSongRecord?.let { i -> realm.executeTransaction { i.playing = false } }
                    curSongRecord = null
                }
                DISPLAY_TYPE_SONG ->
                {
                    try
                    {
                        EventBus.getDefault().post(PlayerStatusEvent(PLAYER_STATUS_COMPLETION))
                        val data = UserDataHelper.userData
                        if (data.currentPlay != null)
                        {
                            realm.executeTransaction {
                                it.copyToRealmOrUpdate(songRecord)
                                songRecord = null
                                data.currentPlay = null
                            }
                        }
                        playNext()
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }

                }
                else                    ->
                {

                }
            }
        }

        override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int)
        {
        }

        override fun onPrepared(mp: MediaPlayer?)
        {

            if (displayType != DISPLAY_TYPE_SONGRECORD)
            {
                try
                {
                    curSongRecord?.let { i -> realm.executeTransaction { i.playing = false } }
                    curSongRecord = null
                }
                catch (e: Exception)
                {

                }
            }

            when (displayType)
            {
                DISPLAY_TYPE_SONGRECORD ->
                {
                    initTimer()
                    mp?.start()
                    curSongRecord?.let { i -> realm.executeTransaction { i.playing = true } }
                }
                DISPLAY_TYPE_LANGDU ->
                {
                    startLdReading(readItem)
                    initTimer()
                    mp?.start()
                }
                DISPLAY_TYPE_SONG ->
                {
                    try
                    {
                        mBinding?.lrcView?.loadLrc("")
                        startRecordSong(curSong)
                        accompany = Accompany.BC
                        EventBus.getDefault().post(AccompanyChangedEvent(accompany))
                        mp?.start()
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
                else                    ->
                {
                    initTimer()
                    mp?.start()
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
        if (!DspHelper.isOpen)
        {
            try
            {
                DspHelper.open()
                DspHelper.sendHex("484D0001000004AA")
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

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

    private var needPaint = true

    override fun showPreso(display: Display)
    {
        val presoContext = createPresoContext(display)
        mWindowManager = presoContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mBinding = VideoPresentationBinding.inflate(LayoutInflater.from(presoContext))
        mBinding!!.lrcView.apply {
            setLabel("")
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
                if (needPaint)
                {
                    val canvas: Canvas = holder!!.lockCanvas()
                    val bitmap = resources.getDrawable(R.mipmap.ld_tv_bg).toBitmap()
                    val mSrcRect = Rect(0, 0, bitmap.width, bitmap.height)
                    val mDestRect = Rect(0, 0, bitmap.width, bitmap.height)
                    val mBitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        isFilterBitmap = true
                        isDither = true
                    }
                    canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint)
                    holder.unlockCanvasAndPost(canvas)
                    needPaint = false
                }
            }

        })
        mWindowManager?.addView(mBinding!!.root, buildLayoutParams())
    }

    private fun playNext()
    {
        val data = UserDataHelper.userData

        if (data.currentPlay != null)
        {

            realm.executeTransaction { realm ->
                songRecord?.let {
                    realm.copyToRealmOrUpdate(it)
                }
                data.currentPlay = null
            }
        }
        if (data.playlist.isNotEmpty())
        {
            realm.beginTransaction()
            val item = data.playlist.removeAt(0)
            data.currentPlay = item
            realm.commitTransaction()
            curSong = item
            currentPlay.set(item)
        }
    }

    /**
     * 播放背景音乐
     */
    private fun playBgm(bgm: ReadBgm?)
    {
        bgm?.let {
            mBinding?.surfaceView?.visibility = View.INVISIBLE
            audioRecorder?.stop()

            if (!it.filePath.isNullOrBlank() && File(it.filePath).exists())
            {
                newPlayer("file://${bgm.filePath}${bgm.file_name}")
            }
            else
            {
                it.url?.let { newPlayer(it) }
            }
            realm.executeTransaction { UserDataHelper.userData.currentPlay = null }
        }
    }

    /**
     * 播放党政学习
     * @param item DzXueXi
     */
    private fun playDz(item: DzXueXi)
    {
        LogUtils.d(item)
        mBinding?.surfaceView?.visibility = View.INVISIBLE
        audioRecorder?.stop()
        newPlayer("file://${item.audioPath}${item.audioFileName}")
        realm.executeTransaction { UserDataHelper.userData.currentPlay = null }
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
        mPlayer = MediaPlayer.create(this, Uri.parse(path), mainDisplay)
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
        if (event.list.size == 1 && UserDataHelper.userData.currentPlay == null && readItem.id.isEmpty())
        {
            playNext()
        }
    }

    @Subscribe
    fun onProfileDataInit(event: ProfileDataInitEvent)
    {
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
            PlayEventType.PLAY_NEXT -> playNext()
            PlayEventType.MUTE ->
            {
            }
            PlayEventType.VOLUME ->
            {
                LogUtils.e(event.data)
                if (event.data == 0)
                {
                    DspHelper.sendHex("484D000502002D032CAA")
                }
                else
                {
                    DspHelper.sendHex("484D000502002B032AAA")
                }
            }
            PlayEventType.PAUSE ->
            {
                val b = (event.data as Boolean?) ?: false
                if (b)
                {
                    mPlayer?.pause()
                }
                else
                {
                    mPlayer?.start()
                }

            }
            PlayEventType.CHANNEL ->
            {
                changeChanel((event.data ?: false) as Boolean)
            }
            PlayEventType.REPLAY ->
            {
                UserDataHelper.userData.currentPlay?.let { curSong = it }
            }

            PlayEventType.START_RECORD ->
            {
                UserDataHelper.userData.currentPlay?.let {
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
                    curSong = it
                    audioRecorder?.start()
                }

            }
            PlayEventType.STOP_RECORD ->
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
            accompany = if (isYC) Accompany.YC else Accompany.BC
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
        val dir = File("/mnt/usb_storage/SATA/C/langdu/records")
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

        val records = UserDataHelper.userData.records

        Realm.getInstance(userConfig).executeTransaction {
            val i = it.copyToRealmOrUpdate(record)
            records.add(i)
        }
        audioRecorder?.start()
    }

    private fun startRecordSong(song: Song)
    {
        val dir = File("/mnt/usb_storage/SATA/C/songs/records")
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
        songRecord = SongRecord(id = uuid, song, UserDataHelper.userData.id, f)
        audioRecorder?.start()
    }

    @Subscribe
    fun stopReading(event: ReadingStopOfUser)
    {
        mPlayer?.stop()
        audioRecorder?.stop()
        EventBus.getDefault().post(ReadingStop(record))
        timer?.dispose()
        readItem = ReadItem()
    }

    @Subscribe
    fun playRecord(event: PlayRecordEvent)
    {
        displayType = DISPLAY_TYPE_LDRECORD
        audioRecorder?.stop()
        timer?.dispose()
        //初始化歌词
        if (!event.record.readItem?.lyric.isNullOrBlank())
        {
            val lrc = Base64.decode(event.record.readItem?.lyric, Base64.NO_WRAP)
                .toString(Charset.defaultCharset())
            mBinding?.lrcView?.loadLrc(lrc)
        }
        mBinding?.surfaceView?.visibility = View.INVISIBLE
        newPlayer(event.record.file)
        realm.executeTransaction { UserDataHelper.userData.currentPlay = null }
    }

    /**
     * 结束试听
     */
    @Subscribe
    fun stopAudition(event: StopAuditionEvent)
    {
        mPlayer?.stop()
        timer?.dispose()
    }

    /**
     * 开始党政学习
     * @param event StartDzxxEvent 事件
     */
    @Subscribe
    fun startDzxx(event: StartDzxxEvent)
    {
        this.dzxxItem = event.currentItem
    }

    /**
     * 播放歌曲录音
     */
    @Subscribe
    fun startPlaySongRecord(event: PlaySongRecordEvent)
    {
        displayType = DISPLAY_TYPE_SONGRECORD
        audioRecorder?.stop()
        timer?.dispose()
        mBinding?.surfaceView?.visibility = View.INVISIBLE
        curSongRecord?.let { i -> realm.executeTransaction { i.playing = false } }
        curSongRecord = event.songRecord
        newPlayer("file://${event.songRecord.filePath}")
        realm.executeTransaction { UserDataHelper.userData.currentPlay = null }
    }

    /**
     * 停止试听歌曲录音
     */
    @Subscribe
    fun stopPlaySongRecord(event: StopPlaySongRecordEvent)
    {
        realm.executeTransaction { event.songRecord.playing = false }
        mPlayer?.stop()
        timer?.dispose()
    }

    companion object
    {
        //1：朗读 2:K歌 3:播放录音 4:党政
        const val DISPLAY_TYPE_LANGDU = 1
        const val DISPLAY_TYPE_SONG = 2
        const val DISPLAY_TYPE_LDRECORD = 3
        const val DISPLAY_TYPE_SONGRECORD = 4
        const val DISPLAY_TYPE_DANGZHENG = 5
        const val DISPLAY_TYPE_DANGZHENG_VIDEO = 6
    }
}
