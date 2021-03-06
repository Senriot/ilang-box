package com.senriot.ilangbox.ui.langdu

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.alibaba.fastjson.JSON
import com.android.karaoke.common.api.Api
import com.android.karaoke.common.api.RecordVO
import com.android.karaoke.common.api.UploadResult
import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.common.models.Record
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.events.*
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.drake.net.Post
import com.drake.net.utils.scopeNet
import com.senriot.ilangbox.GUEST
import com.yanzhenjie.kalle.FormBody
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.text.DecimalFormat

class LdRecordingViewModel : AbstractViewModel()
{
    val timer = ObservableField<String>("00:00")
    val curBgmName = ObservableField<String>("")
    val recordCompletion = ObservableBoolean(false)
    val hasUpload = ObservableBoolean(false)
    val title = ObservableField("正在录音...")
    private var record: Record? = null

    val item = ObservableField<ReadItem>()

//    init
//    {
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onCleared()
//    {
//        super.onCleared()
//        EventBus.getDefault().unregister(this)
//    }

    override fun onStart()
    {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop()
    {
        EventBus.getDefault().unregister(this)
        super.onStop()

    }

    @Subscribe
    fun onCurrentPosition(currentPosition: CurrentPositionEvent)
    {
        val time = currentPosition.currentPosition / 1000
        val mm: String = DecimalFormat("00").format(time / 60)
        val ss: String = DecimalFormat("00").format(time % 60)
        timer.set("$mm:$ss")
    }

    @Subscribe
    fun onRecordStop(event: ReadingStop)
    {
        record = event.record
        recordCompletion.set(true)
        if (UserDataHelper.userData.id != GUEST)
            hasUpload.set(true)
    }

    @Subscribe
    fun onBgmPlaying(event: BgmPlaying)
    {
        curBgmName.set(event.bgm.name)
    }

    fun onCompletion()
    {
        recordCompletion.set(true)
        if (UserDataHelper.userData.id != GUEST)
            hasUpload.set(true)
        EventBus.getDefault().post(ReadingStopOfUser())
    }

    fun reStart()
    {
        EventBus.getDefault().post(StartRecordingEvent(item.get()!!))
        recordCompletion.set(false)
        hasUpload.set(false)
    }

    fun playRecord(view: View)
    {
        if (record != null)
        {
            EventBus.getDefault().post(PlayRecordEvent(record!!))
        }
    }

    fun upload(view: View)
    {
        if (UserDataHelper.userData.id != GUEST)
        {


            record?.let { item ->
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
                        hasUpload.set(false)
                    }
                }

//                GlobalScope.launch(Dispatchers.Main) {
//                    val file = File(item.file)
//                    val requestFile: RequestBody =
//                        RequestBody.create(MediaType.parse("application/otcet-stream"), file)
//                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//                    val resp = Api.recordApiService.upload(body).await()
//                    if (resp.success)
//                    {
//                        val recordVO = RecordVO(
//                            openId = UserDataHelper.userData.id,
//                            langduId = item.readItem!!.id,
//                            url = resp.result!!.url,
//                            recordType = "1"
//                        )
//                        val s = Api.recordApiService.add(recordVO).await()
//                        LogUtils.d(s)
//                        if (s.success)
//                        {
//                            Realm.getInstance(userConfig).executeTransaction { item.updated = true }
//                            hasUpload.set(false)
//                        }
//                    }
//                }
            }
        }

    }

}
