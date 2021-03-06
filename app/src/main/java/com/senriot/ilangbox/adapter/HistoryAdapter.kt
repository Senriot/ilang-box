package com.senriot.ilangbox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.android.karaoke.common.api.Api
import com.android.karaoke.common.api.RecordVO
import com.android.karaoke.common.api.UploadResult
import com.android.karaoke.common.models.SongRecord
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.events.PlaySongRecordEvent
import com.android.karaoke.player.events.StopPlaySongRecordEvent
import com.apkfuns.logutils.LogUtils
import com.drake.net.Post
import com.drake.net.utils.scopeNet
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.GUEST
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.HistoryItemBinding
import com.yanzhenjie.kalle.FormBody
import io.realm.OrderedRealmCollection
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import java.io.File

class HistoryAdapter(items: OrderedRealmCollection<SongRecord>) :
    RealmRecyclerViewAdapter<SongRecord, HistoryAdapter.HistoryViewHolder>(items, true, true)
{

    class HistoryViewHolder(val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    init
    {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long
    {
        return getItem(position)?.id?.hashCode()?.toLong() ?: 0L
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder
    {
        val binding =
            DataBindingUtil.inflate<HistoryItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.history_item,
                parent,
                false
            )

        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int)
    {
        val item = getItem(position)!!
        data?.let {
            holder.binding.setVariable(BR.item, it[position])
        }

        holder.binding.btnShiTing.text = if (item.playing) "??????" else "??????"

        holder.binding.btnShiTing.setOnClickListener {
            if (item.playing)
            {
                EventBus.getDefault().post(StopPlaySongRecordEvent(item))
            }
            else
            {
                EventBus.getDefault().post(PlaySongRecordEvent(item))
            }
        }

        holder.binding.btnUpload.text = "??????"
        holder.binding.btnUpload.isEnabled = true
        holder.binding.btnUpload.visibility =
            if (!item.updated && UserDataHelper.userData.id != GUEST) View.VISIBLE else View.GONE
        holder.binding.btnUpload.setOnClickListener {
            holder.binding.btnUpload.text = "?????????..."
            holder.binding.btnUpload.isEnabled = false
            val file = File(item.filePath)
            scopeNet {
                val result = Post<String>("http://aogevod.com/group1/upload", absolutePath = true) {
                    val form = FormBody.newBuilder()
                        .param("output", "json")
                        .file("file", file)
                        .build()
                    form.onProgress { origin, progress -> LogUtils.i("???????????? $progress") }
                    body(form)
                }.await()
                LogUtils.d("???????????? $result")
                val info = JSON.parseObject(result, UploadResult::class.java)
                if (info.retcode == 0)
                {
                    val recordVO = RecordVO(
                        openId = UserDataHelper.userData.id,
                        songId = item.song!!.id,
                        url = info.url,
                        recordType = "2"
                    )
                    val s = Api.recordApiService.add(recordVO).await()
                    LogUtils.d(s)
                    if (s.success)
                    {
                        Realm.getInstance(userConfig).executeTransaction { item.updated = true }
                        notifyItemChanged(position)
                    }
                    else
                    {
                        holder.binding.btnUpload.text = "??????"
                        holder.binding.btnUpload.isEnabled = true
                    }
                }
                else
                {
                    holder.binding.btnUpload.text = "??????"
                    holder.binding.btnUpload.isEnabled = true
                }
            }.catch {
                holder.binding.btnUpload.text = "??????"
                holder.binding.btnUpload.isEnabled = true
            }
//            val requestFile: RequestBody =
//                RequestBody.create(MediaType.parse("application/otcet-stream"), file)
//            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//            GlobalScope.launch(Dispatchers.Main) {
//                try
//                {
//                    val resp = Api.recordApiService.upload(body).await()
//                    if (resp.success)
//                    {
//                        val recordVO = RecordVO(
//                            openid = UserDataHelper.userData.id,
//                            itemid = item.song!!.id,
//                            url = resp.result!!.url,
//                            recordType = "2"
//                        )
//                        val s = Api.recordApiService.add(recordVO).await()
//                        LogUtils.d(s)
//                        if (s.success)
//                        {
//                            Realm.getInstance(userConfig).executeTransaction { item.updated = true }
//                            notifyItemChanged(position)
//                        }
//                        else
//                        {
//                            holder.binding.btnUpload.text = "??????"
//                            holder.binding.btnUpload.isEnabled = true
//                        }
//                    }
//                    else
//                    {
//                        holder.binding.btnUpload.text = "??????"
//                        holder.binding.btnUpload.isEnabled = true
//                    }
//                }
//                catch (e: Exception)
//                {
//                    holder.binding.btnUpload.text = "??????"
//                    holder.binding.btnUpload.isEnabled = true
//                }
//            }

        }
    }
}