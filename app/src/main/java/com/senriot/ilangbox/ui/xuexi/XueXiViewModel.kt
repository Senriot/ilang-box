package com.senriot.ilangbox.ui.xuexi

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.karaoke.common.models.Category
import com.android.karaoke.common.models.Dict
import com.android.karaoke.common.models.DzXueXi
import com.android.karaoke.common.mvvm.BindingConfig
import com.android.karaoke.player.events.StartDzxxEvent
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.LdBgmAdapter
import com.senriot.ilangbox.adapter.RealmAdapter
import com.senriot.ilangbox.adapter.RealmRecyclerViewAdapter
import com.senriot.ilangbox.adapter.RecyclerViewRealmAdapter
import com.senriot.ilangbox.databinding.DzxxItemBinding
import com.senriot.ilangbox.databinding.LdBgmItemBinding
import com.senriot.ilangbox.model.DownloadViewModel
import com.yuan.library.dmanager.download.DownloadManager
import com.yuan.library.dmanager.download.DownloadTask
import com.yuan.library.dmanager.download.TaskEntity
import com.yuan.library.dmanager.download.TaskStatus
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import java.io.File
import kotlin.properties.Delegates

class XueXiViewModel : AbstractViewModel()
{
    //    private val defaultDict =
//        Realm.getDefaultInstance().where<Dict>().equalTo("id", 123L).findFirst()!!
    private val downloadManager = DownloadManager.getInstance()

    val adapter by lazy {
        val items =
            Realm.getDefaultInstance().where<DzXueXi>()
                .findAll()
//        RecyclerViewRealmAdapter(items, BindingConfig(R.layout.dzxx_item, mapOf(Pair(BR.vm, this))))
        object : RealmRecyclerViewAdapter<DzXueXi, DzItemViewHolder>(items, true)
        {
            init
            {
                setHasStableIds(true)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DzItemViewHolder
            {
                val binding = DataBindingUtil.inflate<DzxxItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.dzxx_item,
                    parent,
                    false
                )
                return DzItemViewHolder(binding)
            }

            override fun onBindViewHolder(holder: DzItemViewHolder, position: Int)
            {
                val item = getItem(position)!!
                holder.binding.setVariable(BR.item, item)
                var itemTask = DownloadManager.getInstance().getTask(item.id)
                holder.binding.btnDownload.visibility =
                    if (item.exist == true) View.GONE else View.VISIBLE
                if (itemTask != null)
                {
                    val dvm = DownloadViewModel(item.id, 3)
                    holder.binding.setVariable(BR.downloadVm, dvm)
                } else
                {
                    holder.binding.setVariable(BR.downloadVm, null)
                }
                holder.binding.root.setOnClickListener {
                    LogUtils.d(it)
                    if (item.exist == true)
                        EventBus.getDefault().post(StartDzxxEvent(item))
                }
                holder.binding.btnDownload.setOnClickListener {
                    if (itemTask == null)
                    {
                        val dir = File(item.audioPath)
                        if (!dir.exists())
                        {
                            dir.mkdirs()
                        }
                        itemTask = DownloadTask(
                            TaskEntity.Builder().downloadId(item.id)
                                .filePath(item.audioPath)
                                .fileName(item.audioFileName)
                                .url("http://aogevod.com:9000/group1/ilang/ld/bgm/吕秀龄-逆伦.mp3")
                                .build()
                        )
                        downloadManager.addTask(itemTask)
                        val vm = DownloadViewModel(item.id, 3)
                        holder.binding.setVariable(BR.downloadVm, vm)
                    } else
                    {
                        val taskEntity = itemTask.taskEntity
                        when (taskEntity.taskStatus)
                        {
                            TaskStatus.TASK_STATUS_QUEUE -> downloadManager.pauseTask(
                                itemTask
                            )
                            TaskStatus.TASK_STATUS_INIT -> downloadManager.addTask(itemTask)
                            TaskStatus.TASK_STATUS_CONNECTING -> downloadManager.pauseTask(
                                itemTask
                            )
                            TaskStatus.TASK_STATUS_DOWNLOADING -> downloadManager.pauseTask(
                                itemTask
                            )
                            TaskStatus.TASK_STATUS_CANCEL -> downloadManager.addTask(itemTask)
                            TaskStatus.TASK_STATUS_PAUSE -> downloadManager.resumeTask(
                                itemTask
                            )
                            TaskStatus.TASK_STATUS_FINISH -> downloadManager.cancelTask(
                                itemTask
                            )
                            TaskStatus.TASK_STATUS_REQUEST_ERROR -> downloadManager.addTask(itemTask)
                            TaskStatus.TASK_STATUS_STORAGE_ERROR -> downloadManager.addTask(itemTask)
                        }
                    }
                }
            }

        }
    }

    class DzItemViewHolder(val binding: DzxxItemBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }

    var selectedDict: Category by Delegates.observable(Category(), { _, old, new ->
        if (old != new)
        {
            val items =
                Realm.getDefaultInstance().where<DzXueXi>().equalTo("type", new.id).findAll()
            adapter.updateData(items)
        }
    })


    val categories by lazy {
        Realm.getDefaultInstance().where<Category>().equalTo("pid", "1277622648859443202")
            .sort("sort_no", Sort.ASCENDING).findAll()
    }

    fun startPlay(view: View, item: DzXueXi)
    {
//        view.findNavController().navigate(
//            XueXiFragmentDirections.actionXueXiFragmentToDzContentFragment(
//                item,
//                if (selectedDict.id <= 0) 123L else selectedDict.id
//            )
//        )
        if (item.exist == true)
            EventBus.getDefault().post(StartDzxxEvent(item))
    }
}
