package com.senriot.ilangbox

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.Bindable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.android.karaoke.player.DspHelper
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.drake.net.utils.scopeNet
import com.senriot.ilangbox.event.MainNavChangedEvent
import com.senriot.ilangbox.event.SearchTextChangedEvent
import com.wwengine.hw.PaintView
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.input_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MainActViewModel : AbstractViewModel()
{

    init
    {
        EventBus.getDefault().register(this)
    }

    var searchText = ObservableField<String>().apply { set("") }
    var charArray = ObservableArrayList<Char>()

    var inputWindowIsShow = false

    private var paintView: PaintView? = null
    private var searchKeyword by Delegates.observable("", onChange = { _, oldValue, newValue ->
        if (oldValue != newValue)
        {
            searchText.set(newValue)
            if (inputWindowIsShow)
                EventBus.getDefault().post(SearchTextChangedEvent(newValue))
        }
    })
    val hwListener: PaintView.OnResultListener = PaintView.OnResultListener { view, result ->
        paintView = view
        charArray.clear()
        charArray.addAll(result.toSet())
    }

    /**
     * 手写板选中单字
     */
    fun hwSelected(v: View)
    {
        val textView = v as TextView
        if (!textView.text.toString().isBlank())
        {
            searchKeyword = "${searchKeyword}${textView.text}"
            paintView?.resetRecognize()
        }
    }

    private val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    val curDate = ObservableField<String>()

    val backgroundUri = ObservableField<String>("res://com.senriot.ilangbox/" + R.mipmap.langdu_bg)

    val timer = Observable.interval(1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .subscribe {
            val date = df.format(System.currentTimeMillis())
            curDate.set(date)
        }

    @Subscribe
    fun pageChanged(event: MainNavChangedEvent)
    {
        val uri = when (event.id)
        {
            R.id.rb_xuexi -> R.mipmap.xuexi_bg
            R.id.rb_langdu -> R.mipmap.langdu_bg
            R.id.rb_hongge -> R.mipmap.kge_bg
            else           -> -1
        }
        backgroundUri.set("res://com.senriot.ilangbox/${uri}")
    }


    fun onDelete()
    {

        if (searchKeyword.isNotEmpty())
            searchKeyword =
                searchKeyword.removeRange(searchKeyword.length - 1, searchKeyword.length)
    }

    fun onClear()
    {
        searchKeyword = ""
    }

    fun pyInput(view: View)
    {
        val txt = (view as Button).text
        searchKeyword = "${searchKeyword}$txt"
        LogUtils.e(searchKeyword)
    }
}