package com.senriot.ilangbox.adapter

import android.view.View
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.mvvm.BindingConfig
import com.apkfuns.logutils.LogUtils
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.OkSongItemBinding
import com.senriot.ilangbox.model.DownloadViewModel
import com.yuan.library.dmanager.download.DownloadManager
import com.yuan.library.dmanager.download.DownloadTask
import com.yuan.library.dmanager.download.TaskEntity
import com.yuan.library.dmanager.download.TaskStatus.*
import io.realm.OrderedRealmCollection
import java.text.DecimalFormat


class SongListAdapter(
    items: OrderedRealmCollection<Song>,
    config: BindingConfig,
    row: Int,
    column: Int
) : RealmAdapter<Song>(items, config, row, column)
{
    private val downloadManager = DownloadManager.getInstance()

    override fun onBindViewHolder(holder: RealmViewHolder, position: Int)
    {
        super.onBindViewHolder(holder, position)
        val binding = (holder.binding as OkSongItemBinding)
        val item = getItem(position) ?: return

        var itemTask = downloadManager.getTask(item.id)
        binding.buttons.visibility = if (item.exist == true) View.VISIBLE else View.GONE
        binding.progressLayout.visibility = if (item.exist == true) View.GONE else View.VISIBLE
        if (itemTask != null)
        {
            val downloadViewModel = DownloadViewModel(item.id)
            holder.binding.setVariable(BR.downloadVm, downloadViewModel)
        } else
        {
            holder.binding.setVariable(BR.downloadVm, null)
        }
        binding.btnDownload.setOnClickListener {
            LogUtils.d("下载歌曲")
            if (itemTask == null)
            {
                itemTask = DownloadTask(
                    TaskEntity.Builder().downloadId(item.id).filePath("/sdcard/ilang-box")
                        .fileName(item.file_name)
                        .url("http://music.ilangbar.com:9000/ilang/appdata/db.realm").build()
                )
                downloadManager.addTask(itemTask)
                val vm = DownloadViewModel(item.id)
                binding.setVariable(BR.downloadVm, vm)
            } else
            {
                val taskEntity = itemTask.taskEntity
                when (taskEntity.taskStatus)
                {
                    TASK_STATUS_QUEUE -> downloadManager.pauseTask(itemTask)
                    TASK_STATUS_INIT -> downloadManager.addTask(itemTask)
                    TASK_STATUS_CONNECTING -> downloadManager.pauseTask(itemTask)
                    TASK_STATUS_DOWNLOADING -> downloadManager.pauseTask(itemTask)
                    TASK_STATUS_CANCEL -> downloadManager.addTask(itemTask)
                    TASK_STATUS_PAUSE -> downloadManager.resumeTask(itemTask)
                    TASK_STATUS_FINISH -> downloadManager.cancelTask(itemTask)
                    TASK_STATUS_REQUEST_ERROR -> downloadManager.addTask(itemTask)
                    TASK_STATUS_STORAGE_ERROR -> downloadManager.addTask(itemTask)
                }
            }
        }
    }
}