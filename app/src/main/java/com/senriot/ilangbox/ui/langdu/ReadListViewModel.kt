package com.senriot.ilangbox.ui.langdu

import android.view.View
import androidx.navigation.findNavController
import com.alibaba.fastjson.JSON
import com.android.karaoke.common.api.Api
import com.android.karaoke.common.api.RecordVO
import com.android.karaoke.common.api.UpdateInfo
import com.android.karaoke.common.api.UploadResult
import com.android.karaoke.common.models.Record
import com.android.karaoke.common.mvvm.BindingConfig
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.events.PlayRecordEvent
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.drake.net.Post
import com.drake.net.utils.scopeNet
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RecyclerViewRealmAdapter
import com.yanzhenjie.kalle.FormBody
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import java.io.File


class ReadListViewModel : AbstractViewModel()
{
    val items by lazy {
        UserDataHelper.userData.records
    }

    val itemBinding by lazy {
        ItemBinding.of<Record>(BR.item, R.layout.ld_record_item).bindExtra(BR.vm, this)
    }


    val adapter by lazy {
        RecyclerViewRealmAdapter<Record>(
            items,
            BindingConfig(R.layout.ld_record_item, mapOf(Pair(BR.vm, this)))
        )
    }

    fun reRecording(view: View, item: Record)
    {
        view.findNavController()
            .navigate(ReadListFragmentDirections.actionReadListFragmentToLdItemDetailFragment(item.readItem!!))
    }

    fun upload(item: Record)
    {
        val file = File(item.file)
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
            val recordVO = RecordVO(
                openId = UserDataHelper.userData.id,
                langduId = item.readItem!!.id,
                url = info.url,
                recordType = "1"
            )

            val s = Api.recordApiService.add(recordVO).await()
            LogUtils.d(s)
            if (s.success)
            {
                Realm.getInstance(userConfig).executeTransaction { item.updated = true }
            }
        }
//        GlobalScope.launch(Dispatchers.Main) {
//            val file = File(item.file)
//            val requestFile: RequestBody =
//                RequestBody.create(MediaType.parse("application/otcet-stream"), file)
//            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//            val resp = Api.recordApiService.upload(body).await()
//            if (resp.success)
//            {
//                val recordVO = RecordVO(
//                    openid = UserDataHelper.userData.id,
//                    itemid = item.readItem!!.id,
//                    url = resp.result!!.url,
//                    recordType = "1"
//                )
//                val s = Api.recordApiService.add(recordVO).await()
//                LogUtils.d(s)
//                if (s.success)
//                {
//                    Realm.getInstance(userConfig).executeTransaction { item.updated = true }
//                }
//            }
//            LogUtils.d(resp)
//        }
    }


    fun playRecord(view: View, item: Record)
    {
        EventBus.getDefault().post(PlayRecordEvent(item))
        view.findNavController().navigate(
            ReadListFragmentDirections.actionReadListFragmentToAuditionFragment(item)
        )
    }

    fun delete(item: Record)
    {
        UserDataHelper.delRecord(item)
    }


}
