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
import com.senriot.ilangbox.App
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.LangDuFragmentBinding
import com.senriot.ilangbox.event.ShowReadListEvent
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
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
            vm.micVolume.set(34)
            vm.soundVolume.set(34)
            vm.sendEffectValue(34, "03")
            vm.sendEffectValue(34, "02")
            vm.sp.micVolume = 34
            vm.sp.soundVolume = 34
        }
    }

    @Subscribe
    fun onShowReadList(event: ShowReadListEvent)
    {
        val host = childFragmentManager.findFragmentById(R.id.ldNavHost) as NavHostFragment
        LogUtils.d(host.navController.currentDestination)
        if (host.navController.currentDestination?.label != "ReadListFragment")
            host.navController.navigate(Uri.parse("https://www.senriot.com/ilang-box/readlist"))
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener
    {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
        {
            if (fromUser)
            {
                if (seekBar.id == R.id.bgmSeekBar)
                {
                    vm.sendEffectValue(progress, "03")
                    vm.sp.soundVolume = progress
                }
                else
                {
                    vm.sendEffectValue(progress, "02")
                    vm.sp.micVolume = progress
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?)
        {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?)
        {
        }
    }
}
