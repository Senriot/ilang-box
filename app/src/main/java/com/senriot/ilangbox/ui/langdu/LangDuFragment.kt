package com.senriot.ilangbox.ui.langdu

import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.NavHostFragment
import com.android.karaoke.common.models.ReadCategory
import com.android.karaoke.player.DspHelper
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.LangDuFragmentBinding
import com.senriot.ilangbox.event.ShowReadListEvent
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.lang_du_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LangDuFragment :
    MvvmFragment<LangDuFragmentBinding, LangDuViewModel>(R.layout.lang_du_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LangDuViewModel>()

    override fun createViewModel(): LangDuViewModel = vm

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView()
    {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun postInit()
    {
        super.postInit()
//        Realm.getDefaultInstance().where<ReadCategory>().findAll()
    }

    override fun performDataBinding()
    {
        super.performDataBinding()
        bgmSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        micSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        btnDefault.setOnClickListener {
            bgmSeekBar.progress = 50
            micSeekBar.progress = 50
        }
    }

    @Subscribe
    fun onShowReadList(event: ShowReadListEvent)
    {
        LogUtils.e("onShowReadList")
        val host = childFragmentManager.findFragmentById(R.id.ldNavHost) as NavHostFragment
        host.navController.navigate(Uri.parse("https://www.senriot.com/ilang-box/readlist"))
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener
    {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
        {
            val sr = arrayListOf("48", "4D", "00", "06", "02", "00", progress.toString(16))
            if (seekBar.id == R.id.bgmSeekBar)
            {
                sr.add("03")
            } else
            {
                sr.add("02")
            }
            var sum = 0xff
            sr.forEach {
                sum = sum xor Integer.parseInt(it, 16)
            }
            sr.add(Integer.toHexString(sum))
            sr.add("AA")
            LogUtils.e(sr.joinToString(""))
            DspHelper.sendHex(sr.joinToString(""))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?)
        {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?)
        {
        }
    }
}
