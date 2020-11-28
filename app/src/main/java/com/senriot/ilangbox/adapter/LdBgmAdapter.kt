package com.senriot.ilangbox.adapter

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.karaoke.common.models.ReadBgm
import com.android.karaoke.player.events.ChangeBgmEvent
import com.apkfuns.logutils.LogUtils
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdBgmItemBinding
import com.senriot.ilangbox.model.DownloadViewModel
import com.yuan.library.dmanager.download.DownloadManager
import com.yuan.library.dmanager.download.DownloadTask
import com.yuan.library.dmanager.download.TaskEntity
import com.yuan.library.dmanager.download.TaskStatus
import io.realm.OrderedRealmCollection
import org.greenrobot.eventbus.EventBus
import java.io.File

class LdBgmAdapter(private val items: OrderedRealmCollection<ReadBgm>) :
    RealmRecyclerViewAdapter<ReadBgm, LdBgmAdapter.BgmViewHolder>(items, true)
{
    private val downloadManager = DownloadManager.getInstance()

    init
    {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BgmViewHolder
    {
        val binding = DataBindingUtil.inflate<LdBgmItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.ld_bgm_item,
            parent,
            false
        )
        return BgmViewHolder(binding)
    }


    override fun onBindViewHolder(holder: BgmViewHolder, position: Int)
    {
        val item = getItem(position)!!
        holder.binding.setVariable(BR.item, item)
        var itemTask = DownloadManager.getInstance().getTask(item.id)
        if (itemTask != null)
        {
            val dvm = DownloadViewModel(item.id, 2)
            holder.binding.setVariable(BR.downloadVm, dvm)
        } else
        {
            holder.binding.setVariable(BR.downloadVm, null)
        }
        holder.binding.btnDownload.setOnClickListener {
            if (itemTask == null)
            {
                val dir = File(item.filePath)
                if (!dir.exists())
                {
                    dir.mkdirs()
                }
                itemTask = DownloadTask(
                    TaskEntity.Builder().downloadId(item.id)
                        .filePath(item.filePath)
                        .fileName(item.file_name)
                        .url("http://aogevod.com:9000/group1/ilang/ld/bgm/吕秀龄-逆伦.mp3").build()
                )
                downloadManager.addTask(itemTask)
                val vm = DownloadViewModel(item.id, 2)
                holder.binding.setVariable(BR.downloadVm, vm)
            } else
            {
                val taskEntity = itemTask.taskEntity
                when (taskEntity.taskStatus)
                {
                    TaskStatus.TASK_STATUS_QUEUE -> downloadManager.pauseTask(itemTask)
                    TaskStatus.TASK_STATUS_INIT -> downloadManager.addTask(itemTask)
                    TaskStatus.TASK_STATUS_CONNECTING -> downloadManager.pauseTask(itemTask)
                    TaskStatus.TASK_STATUS_DOWNLOADING -> downloadManager.pauseTask(itemTask)
                    TaskStatus.TASK_STATUS_CANCEL -> downloadManager.addTask(itemTask)
                    TaskStatus.TASK_STATUS_PAUSE -> downloadManager.resumeTask(itemTask)
                    TaskStatus.TASK_STATUS_FINISH -> downloadManager.cancelTask(itemTask)
                    TaskStatus.TASK_STATUS_REQUEST_ERROR -> downloadManager.addTask(itemTask)
                    TaskStatus.TASK_STATUS_STORAGE_ERROR -> downloadManager.addTask(itemTask)
                }
            }
        }

        holder.binding.bgmRoot.setOnClickListener {
            LogUtils.d("选中背景音乐")
            LogUtils.d(item)
            if (item.fileExist == true)
            {
                EventBus.getDefault().post(ChangeBgmEvent(item))
                it.findNavController().popBackStack()
            }
        }
    }

    class BgmViewHolder(val binding: LdBgmItemBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }
}