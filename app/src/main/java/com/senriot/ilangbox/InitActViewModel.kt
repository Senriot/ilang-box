package com.senriot.ilangbox

import androidx.databinding.ObservableField
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.arthurivanets.mvvm.events.Command
import com.senriot.ilangbox.event.StartMainActEvent
import com.yuan.library.dmanager.download.DownloadManager
import com.yuan.library.dmanager.download.DownloadTask
import com.yuan.library.dmanager.download.DownloadTaskListener
import java.text.DecimalFormat

class InitActViewModel : AbstractViewModel()
{
    var downloadProgress: ObservableField<Int> = ObservableField(0)

    override fun onStart()
    {
        super.onStart()
    }

    val downloadTaskListener = object : DownloadTaskListener
    {
        override fun onQueue(downloadTask: DownloadTask?)
        {
        }

        override fun onConnecting(downloadTask: DownloadTask?)
        {
        }

        override fun onStart(downloadTask: DownloadTask)
        {
            val percent =
                getPercent(downloadTask.taskEntity.completedSize, downloadTask.taskEntity.totalSize)
            LogUtils.d("开始下载 $percent")
            downloadProgress.set(percent.toInt())
        }

        override fun onPause(downloadTask: DownloadTask?)
        {
        }

        override fun onCancel(downloadTask: DownloadTask?)
        {
        }

        override fun onFinish(downloadTask: DownloadTask)
        {
            LogUtils.d("下载完成")
            LogUtils.d(downloadTask.taskEntity)
//            DownloadManager.getInstance().cancelTask(downloadTask)
            if (downloadTask.taskEntity.taskId == "downloadDb")
            {
                commandBus.post(StartMainActEvent())
            }
        }

        override fun onError(downloadTask: DownloadTask?, code: Int)
        {
        }

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
}