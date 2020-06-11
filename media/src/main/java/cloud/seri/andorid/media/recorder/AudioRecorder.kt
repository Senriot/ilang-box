package cloud.seri.andorid.media.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.android.lameutil.LameUtil
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AudioRecorder(private val format: Format, private val file: File)
{

    init
    {
        LameUtil.init(44100, 1, 44100, 32, 7)
    }

    private val mediaRecorder by lazy {
        MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)
            setAudioEncodingBitRate(96000)
            setAudioSamplingRate(44100)
        }
    }

    private val mBufferSize = AudioRecord.getMinBufferSize(
        44100, AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private val mAudioRecord: AudioRecord by lazy {
        AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            mBufferSize * 2
        )
    }

    private val mExecutorService: ExecutorService by lazy {
        Executors.newCachedThreadPool()
    }

    private val outputStream by lazy { FileOutputStream(file) }

    private var state = State.ERROR

    fun start()
    {
        if (state == State.RECORDING) return
        if (file.exists())
            file.delete()
        file.createNewFile()

        when (format)
        {

            Format.AAC -> recordAAC()
            Format.MP3 -> recordMp3()
            else       ->
            {
            }
        }
    }

    private fun recordAAC()
    {
        mediaRecorder.setOutputFile(file.absolutePath)
        mediaRecorder.prepare()
        mediaRecorder.start()
    }

    private fun recordMp3()
    {
        mExecutorService.execute {
            val buffer =
                ShortArray(44100 * (16 / 8) * 1 * 5) // SampleRate[Hz] * 16bit * Mono * 5sec
            val mp3buffer = ByteArray((7200 + buffer.size.toDouble() * 2.0 * 1.25).toInt())
            state = State.RECORDING
            mAudioRecord.startRecording()
            while (state == State.RECORDING)
            {
                val readSize = mAudioRecord.read(buffer, 0, mBufferSize)
                if (readSize > 0)
                {
                    val encResult = LameUtil.encode(buffer, buffer, readSize, mp3buffer)
                    if (encResult > 0)
                        outputStream.write(mp3buffer, 0, encResult)
                }
            }
            val flushResult = LameUtil.flush(mp3buffer)
            if (flushResult > 0)
                outputStream.write(mp3buffer, 0, flushResult)
            if (state == State.STOPPED)
            {
                mAudioRecord.release()
                outputStream.close()
                LameUtil.close()
            }
            if (state == State.PAUSE)
            {
                mAudioRecord.stop()
            }
        }
    }

    fun pause()
    {
        state = State.PAUSE
    }

    fun resume()
    {
        recordMp3()
    }

    fun stop()
    {
        state = State.STOPPED
    }


    enum class Format
    {
        AAC,
        MP3,
        PCM
    }

    enum class State
    {
        PAUSE, RECORDING, ERROR, STOPPED
    }
}