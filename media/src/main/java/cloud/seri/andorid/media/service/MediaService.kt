package cloud.seri.andorid.media.service

import android.content.Context
import android.media.MediaPlayer
import android.view.SurfaceHolder
import cloud.seri.andorid.media.model.SelectList
import cloud.seri.andorid.media.model.Song
import cloud.seri.andorid.media.recorder.AudioRecorder
import com.firefly.api.FireflyApi
import io.reactivex.disposables.Disposable
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*


class MediaService(private val context: Context) :
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener
{
    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean
    {
        return true
    }

    override fun onSeekComplete(mp: MediaPlayer?)
    {
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean
    {
        next()
        return false
    }

    override fun onCompletion(mp: MediaPlayer?)
    {
        next()
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int)
    {
    }

    private var mRecorder: AudioRecorder? = null
    private var recordDis: Disposable? = null
    private val mTrackAudioIndex = Vector<Int>()
    private var trackNum = 0
    private var currentTrack: String? = null

    private var curAudioIndex: Int = 0

    override fun onPrepared(mp: MediaPlayer?)
    {
        mTrackAudioIndex.clear()
        mp?.trackInfo?.filter { it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO }
                ?.forEachIndexed { index, _ ->
                    mTrackAudioIndex.add(index)
                    trackNum += 1
                }
        currentTrack = playlistService.getCurrentPlay()?.amTrack
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

    var minorDisplay: SurfaceHolder? = null
        set(value)
        {
            if (field != value)
            {
                field = value
                mPlayer.setMinorDisplay(value)
            }
        }
    var mainDisplay: SurfaceHolder? = null
        set(value)
        {
            field = value
            mPlayer.setDisplay(value)
        }

    private val mPlayer: MediaPlayer by lazy {
        MediaPlayer().apply {
            setOnErrorListener(this@MediaService)
            setOnCompletionListener(this@MediaService)
            setOnBufferingUpdateListener(this@MediaService)
            setOnPreparedListener(this@MediaService)
            setOnInfoListener(this@MediaService)
            setOnSeekCompleteListener(this@MediaService)
        }
    }
    private var currentUrl: String? = null

    fun startPlay(url: String)
    {
        try
        {
            mPlayer.let {
                if (it.isPlaying) it.stop()
                it.reset()
            }
            if (mainDisplay == null && minorDisplay == null) return
            currentUrl = url
            mPlayer.setDataSource(url)
            mPlayer.prepareAsync()
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }

//        mainDisplay?.let {
//            currentUrl = url
//            mPlayer.setDataSource(url)
//            mPlayer.prepareAsync()
//        }
    }


    /**
     * 设置静音
     */
    fun toggleMute()
    {
    }

    /**
     * 设置音量
     */
    fun setVolume(v: Float)
    {
    }

    private var index = 0

    private val fireflyApi by lazy { FireflyApi.create(context) }

    fun next()
    {
//        val path = fireflyApi.getUSBPath(0)
//        LogUtils.e(path)
//        playlistService.next()?.let {
//
//            val path = "/mnt/usb_storage/USB_DISK0/C/songs/"
//            val filePath = "$path${it.id}.mpg"
//            if (File(filePath).exists())
//                startPlay(filePath)
//            if (it.path != null)
//            {
//                val path = fireflyApi.getUSBPath(1)
//                LogUtils.e(path)
////                startPlay(it.path!!)
//            } else next()
//        }
    }

    /**
     * 暂停
     */
    fun togglePause()
    {
        if (mPlayer.isPlaying)
        {
            mPlayer.pause()
        }
        else
        {
            mPlayer.start()
        }
    }

    private var isPause = false

    fun pause()
    {
        if (mPlayer.isPlaying)
        {
            mPlayer.pause()
            isPause = true
        }

    }

    fun play()
    {
        if (isPause)
            mPlayer.start()
    }

    /**
     * 重唱
     */
    fun onReplay()
    {
        currentUrl?.let { startPlay(it) }
    }

    private var accompany = Accompany.BC

    fun changeChanel()
    {
        if (mPlayer.isPlaying)
        {
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
                            }
                            else
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
                            }
                            else
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

    /**
     * 原伴唱
     */
    enum class Accompany
    {
        Unknown,
        YC,
        BC
    }

    @Subscribe
    fun receiveChangedEvent(event: ChangePlaylistEvent)
    {

        when (event.type)
        {
            PlayListChangeType.ADD    -> addPlaylist(event.item)
            PlayListChangeType.DELETE -> deleteSelectedItem(event.item)
            PlayListChangeType.TOP    -> onTop(event.item)
        }

    }

    private fun onTop(item: Song)
    {
        try
        {
            val list = Realm.getDefaultInstance().where(SelectList::class.java)
                    .findAll()
            if (list.isEmpty() && !mPlayer.isPlaying){
                val i = SelectList().apply {
                    id = item.id!!
                    song = item
                    updateTime = Date()
                }
                Realm.getDefaultInstance().executeTransaction {
                    list.add()
                }
            }
//            if (playlistService.getSelectedList()?.isEmpty() == true && !mPlayer.isPlaying)
//            {
//                playlistService.onTop(item)
//                next()
//            }
//            else
//            {
//                playlistService.onTop(item)
//            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }


    }

    private fun deleteSelectedItem(item: Song)
    {
        try
        {
            playlistService.deleteSelectedItem(item)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

    }

    private fun addPlaylist(item: Song)
    {
        try
        {
            if (playlistService.getSelectedList()?.isEmpty() == true && !mPlayer.isPlaying)
            {
                playlistService.addPlaylist(item)
                next()
            }
            else
            {
                playlistService.addPlaylist(item)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

    }
}
