package com.senriot.ilangbox.ui.welcome

import android.view.View
import com.alibaba.fastjson.JSON
import com.android.karaoke.common.api.Api
import com.android.karaoke.common.api.RecordVO
import com.android.karaoke.common.api.UploadResult
import com.android.karaoke.common.models.SongRecord
import com.android.karaoke.common.mvvm.BindingConfig
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.events.PlaySongRecordEvent
import com.android.karaoke.player.events.StopPlaySongRecordEvent
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.drake.net.Post
import com.drake.net.utils.scopeNet
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RecyclerViewRealmAdapter
import com.yanzhenjie.kalle.FormBody
import io.realm.Realm
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import java.io.File

class KaraokeRecordsViewModel : AbstractViewModel()
{
    val items by lazy {
        Realm.getInstance(userConfig).where<SongRecord>()
            .equalTo("openId", UserDataHelper.userData.id).findAll()
    }

    val adapter by lazy {
        RecyclerViewRealmAdapter<SongRecord>(
            items,
            BindingConfig(R.layout.karaoke_record_item, mapOf(Pair(BR.vm, this)))
        )
    }

    fun playRecord(item: SongRecord)
    {
        if (item.playing)
        {
            EventBus.getDefault().post(StopPlaySongRecordEvent(item))
        }
        else
        {
            EventBus.getDefault().post(PlaySongRecordEvent(item))
        }
    }

    fun upload(view: View, item: SongRecord)
    {
        val file = File(item.filePath)
        scopeNet {
            val result = Post<String>("http://aogevod.com/group1/upload", absolutePath = true) {
                val form = FormBody.newBuilder()
                    .param("output", "json")
                    .file("file", file)
                    .build()
                form.onProgress { origin, progress -> LogUtils.i("上传进度 $progress") }
                body(form)
            }.await()
            LogUtils.d("上传结果 $result")
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
                    view.visibility = View.INVISIBLE
                }
                else
                {
                    view.visibility = View.VISIBLE
                }
            }
            else
            {
                view.visibility = View.VISIBLE
            }
        }.catch {
            view.visibility = View.VISIBLE
        }
    }
}