package com.senriot.ilangbox.model

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.android.karaoke.common.models.DzXueXi
import com.android.karaoke.common.models.ReadBgm
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.realm.songsConfig
import com.apkfuns.logutils.LogUtils
import com.yuan.library.dmanager.download.DownloadManager
import com.yuan.library.dmanager.download.DownloadTask
import com.yuan.library.dmanager.download.DownloadTaskListener
import com.yuan.library.dmanager.download.TaskStatus.*
import io.realm.Realm
import io.realm.kotlin.where
import java.text.DecimalFormat


class DownloadViewModel(val downloadId: String, private val flag: Int = 0) :
    DownloadTaskListener
{

    private val itemTask: DownloadTask = DownloadManager.getInstance().getTask(downloadId)
    private val mDownloadManager = DownloadManager.getInstance()
    val statusText = ObservableField("云下载")
    val status = ObservableInt(-1)
    val progress = ObservableInt()

    init
    {
        DownloadManager.getInstance().getTask(downloadId)?.setListener(this)
        val taskEntity = itemTask.taskEntity
        val percent = getPercent(taskEntity.completedSize, taskEntity.totalSize)
        progress.set(percent.toInt())
        statusText.set("云下载")
        when (itemTask.taskEntity.taskStatus)
        {
            TASK_STATUS_INIT ->
            {
                val isPause: Boolean = mDownloadManager.isPauseTask(taskEntity.taskId)
                val isFinish: Boolean = mDownloadManager.isFinishTask(taskEntity.taskId)
//                statusText.set(if (isFinish) "删除" else if (!isPause) "云下载" else "继续")
                if (isPause)
                    status.set(2)
            }
            TASK_STATUS_QUEUE ->
            {
                statusText.set("等待中")
            }
            TASK_STATUS_CONNECTING ->
            {
                statusText.set("连接中")
            }
            TASK_STATUS_DOWNLOADING ->
            {
                statusText.set("暂停")
                status.set(1)
            }
            TASK_STATUS_PAUSE ->
            {
                statusText.set("继续")
                status.set(2)
            }
            TASK_STATUS_FINISH ->
            {
                progress.set(0)
            }
            TASK_STATUS_REQUEST_ERROR ->
            {
                statusText.set("重试")
                status.set(3)
            }
            TASK_STATUS_STORAGE_ERROR ->
            {
                statusText.set("重试")
                status.set(3)
            }
        }
    }


    override fun onQueue(downloadTask: DownloadTask?)
    {
        statusText.set("等待中")
    }

    override fun onConnecting(downloadTask: DownloadTask?)
    {
        statusText.set("连接中")
        LogUtils.d(downloadTask)
    }

    override fun onStart(downloadTask: DownloadTask)
    {
        val percent =
            getPercent(downloadTask.taskEntity.completedSize, downloadTask.taskEntity.totalSize)
        LogUtils.d("下载进度 $percent")
        progress.set(percent.toInt())
        statusText.set("暂停")
        status.set(1)
    }

    override fun onPause(downloadTask: DownloadTask?)
    {
        statusText.set("继续")
        status.set(2)
    }

    override fun onCancel(downloadTask: DownloadTask?)
    {
        progress.set(0)
        status.set(0)
    }

    override fun onFinish(downloadTask: DownloadTask?)
    {
        LogUtils.d(downloadTask)
        progress.set(0)
        status.set(0)
//        DownloadManager.getInstance().cancelTask(downloadTask)
        val realm = Realm.getInstance(songsConfig)
        when (flag)
        {
            0 ->
            {
                realm.executeTransaction { r ->
                    r.where<Song>().equalTo("id", downloadId).findFirst()?.let {
                        it.exist = true
                    }
                }
            }
            2 ->
            {
                realm.executeTransaction { r ->
                    r.where<ReadBgm>().equalTo("id", downloadId).findFirst()?.let {
                        it.fileExist = true
                    }
                }
            }
            3 ->
            {
                realm.executeTransaction { r ->
                    r.where<DzXueXi>().equalTo("id", downloadId).findFirst()?.let {
                        it.exist = true
                    }
                }
            }
        }
    }

    override fun onError(downloadTask: DownloadTask?, code: Int)
    {
    }

    private fun getPercent(completed: Long, total: Long): String
    {
        if (total > 0)
        {
            val fen = completed.toDouble() / total.toDouble() * 100
            val df1 = DecimalFormat("0")
            return df1.format(fen)
        }
        return "0"
    }

    fun downloadBgm(bgm: ReadBgm)
    {

    }
}