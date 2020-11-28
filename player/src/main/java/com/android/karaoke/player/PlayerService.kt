package com.android.karaoke.player

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.os.SystemClock
import android.util.Base64
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

    //    private var userData: UserData? = null
    private val mTrackAudioIndex = Vector<Int>()
    private var trackNum = 0
    private var currentTrack: String? = null
    private var curAudioIndex: Int = 0
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
            displayType.set(1)
//            mBinding?.lrcView?.loadLrc()
            if (!new.lyric.isNullOrBlank())
            {
                val lrc =
                    Base64.decode(new.lyric, Base64.NO_WRAP).toString(Charset.defaultCharset())
                mBinding?.lrcView?.loadLrc(lrc)
//                val s = LyricsReader()
//                s.loadLrc(
//                    new.lyric,
//                    File("${Environment.getDataDirectory().absolutePath}/${new.id}.lrc"),
//                    new.id + ".lrc"
//                )
//                mBinding?.lrcView?.lyricsReader = s
            }
            new.bgMusic?.let { playBgm(it) }
        }
    })

    private var dzxxItem by Delegates.observable(DzXueXi(), { _, old, new ->
        if (old != new && new.id.isNotEmpty())
        {
            displayType.set(4)
            val lrc =
                Base64.decode(new.subtitle, Base64.NO_WRAP).toString(Charset.defaultCharset())
            mBinding?.lrcView?.loadLrc(lrc)
//            mBinding?.lrcView?.initLrcData()
//            val reader = LyricsReader()
//            reader.loadLrc(
//                new.subtitle,
//                File("${Environment.getDataDirectory().absolutePath}/${new.id}.lrc"),
//                new.id + ".lrc"
//            )
//            mBinding?.lrcView?.lyricsReader = reader
            playDz(new)
        }
    })

    private var curSong by Delegates.observable(Song(), { _, old, new ->
        audioRecorder?.stop()
        displayType.set(2)
        mBinding?.surfaceView?.visibility = View.VISIBLE
        val path = "file://${new.file_path}${new.file_name}"
        LogUtils.d("播放 $new")
        newPlayer(path)
    })

    private var mBaseTimer = SystemClock.elapsedRealtime();

    val displayType = ObservableInt(0)  //显示类别，1：朗读 2:K歌 3:播放录音 4:党政

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
            LogUtils.e("播放错误 $what $extra")
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
//                    mBinding?.lrcView?.pause()
                    EventBus.getDefault().post(ReadingStop(record))
                    readItem = ReadItem()
                }
                displayType.get() == 3 ->
                {
//                    mBinding?.lrcView?.pause()
                }
                displayType.get() == 4 ->
                {
//                    mBinding?.lrcView?.pause()
                }
                displayType.get() == 5 ->
                {
                    curSongRecord?.let { i -> realm.executeTransaction { i.playing = false } }
                    curSongRecord = null
                }
                else                   ->
                {
                    try
                    {
                        EventBus.getDefault().post(PlayerStatusEvent(PLAYER_STATUS_COMPLETION))
//                    audioRecorder?.stop()
//                    audioRecorder = null
                        val data = UserDataHelper.userData
                        if (data.currentPlay != null)
                        {
                            realm.executeTransaction {
                                it.copyToRealmOrUpdate(songRecord)
//                                    data.history.add(0, data.currentPlay)
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
            }
        }

        override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int)
        {
        }

        override fun onPrepared(mp: MediaPlayer?)
        {
            if (displayType.get() != 5)
            {
                curSongRecord?.let { i -> realm.executeTransaction { i.playing = false } }
                curSongRecord = null
            }
            when
            {
                displayType.get() == 1 ->
                {
                    DspHelper.sendHex("484D00020200010501AA")
                    startLdReading(readItem)
//                    mBinding?.lrcView?.play(0)
                    initTimer()
                    mp?.start()
                }
                displayType.get() == 3 ->
                {
//                    mBinding?.lrcView?.play(0)
                    initTimer()
                    mp?.start()
                }
                displayType.get() == 4 ->
                {
//                    mBinding?.lrcView?.play(0)
                    initTimer()
                    mp?.start()
                }
                displayType.get() == 5 ->
                {
//                    mBinding?.lrcView?.play(0)
                    initTimer()
                    mp?.start()
                    curSongRecord?.let { i -> realm.executeTransaction { i.playing = true } }
                }
                else                   ->
                {
                    try
                    {
                        mBinding?.lrcView?.loadLrc("")
                        startRecordSong(curSong)
                        accompany = Accompany.BC
                        EventBus.getDefault().post(AccompanyChangedEvent(accompany))
                        DspHelper.sendHex("484D00020200010307AA")
                        mp?.start()
                    }
                    catch (e: Exception)
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
//        UserDataHelper.initUserData("Guest")
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

    override fun showPreso(display: Display)
    {
        val presoContext = createPresoContext(display)
        mWindowManager = presoContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mBinding = VideoPresentationBinding.inflate(LayoutInflater.from(presoContext))
//        val typeface = Typeface.createFromAsset(resources.assets, "font/xhei.ttf")
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
//                mPlayer.setSurface(holder!!.surface)
            }

        })
        mWindowManager?.addView(mBinding!!.root, buildLayoutParams())
    }

    private fun playNext()
    {
        val data = UserDataHelper.userData

        if (data.currentPlay != null)
        {
            realm.executeTransaction {
                it.copyToRealmOrUpdate(songRecord)
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
//        mPlayer?.prepareAsync()
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
                    if (displayType.get() != 2)
                    {
//                        mBinding?.lrcView?.pause()
                    }
//                    audioRecorder?.pause()
                }
                else
                {
                    mPlayer?.start()
                    if (displayType.get() != 2)
                    {
//                        mBinding?.lrcView?.resume()
                    }
//                    audioRecorder?.resume()
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
        Realm.getInstance(userConfig).executeTransaction {
            val i = it.copyToRealmOrUpdate(record)
            UserDataHelper.userData.records.add(i)
        }
        audioRecorder?.start()
    }

    private fun startRecordSong(song: Song)
    {
        val dir = File("/mnt/usb_storage/SATA/C/langdu/songs/records")
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
//        Realm.getInstance(userConfig).executeTransaction { it.copyToRealmOrUpdate(songRecord) }
        audioRecorder?.start()
    }

    @Subscribe
    fun stopReading(event: ReadingStopOfUser)
    {
        mPlayer?.stop()
        audioRecorder?.stop()
//        mBinding?.lrcView?.pause()
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
        audioRecorder?.stop()
        timer?.dispose()
        //初始化歌词
        if (!event.record.readItem?.lyric.isNullOrBlank())
        {
            val lrc = Base64.decode(event.record.readItem?.lyric, Base64.NO_WRAP)
                .toString(Charset.defaultCharset())
            mBinding?.lrcView?.loadLrc(lrc)
//            val s = LyricsReader()
//            s.loadLrc(
//                event.record.readItem!!.lyric,
//                File("${Environment.getDataDirectory().absolutePath}/${event.record.readItem!!.id}.lrc"),
//                event.record.readItem!!.id + ".lrc"
//            )
//            mBinding?.lrcView?.lyricsReader = s
        }
        mBinding?.surfaceView?.visibility = View.INVISIBLE
        newPlayer(event.record.file)
//        mPlayer?.prepareAsync()
        realm.executeTransaction { UserDataHelper.userData.currentPlay = null }
    }

    /**
     * 结束试听
     */
    @Subscribe
    fun stopAudition(event: StopAuditionEvent)
    {
        mPlayer?.stop()
//        mBinding?.lrcView?.pause()
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

    /**
     * 播放歌曲录音
     */
    @Subscribe
    fun startPlaySongRecord(event: PlaySongRecordEvent)
    {
        displayType.set(5)
        audioRecorder?.stop()
        timer?.dispose()
        mBinding?.surfaceView?.visibility = View.INVISIBLE
        curSongRecord?.let { i -> realm.executeTransaction { i.playing = false } }
        curSongRecord = event.songRecord
        newPlayer("file://${event.songRecord.filePath}")
//        mPlayer?.prepareAsync()
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
        const val DISPLAY_TYPE_SONG = 0
    }
}
