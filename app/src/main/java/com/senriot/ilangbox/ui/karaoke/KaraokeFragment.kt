package com.senriot.ilangbox.ui.karaoke

import android.widget.SeekBar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.karaoke.player.Accompany
import com.android.karaoke.player.DspHelper
import com.android.karaoke.player.events.AccompanyChangedEvent
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeFragmentBinding
import kotlinx.android.synthetic.main.karaoke_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.viewmodel.ext.android.viewModel

class KaraokeFragment :
    MvvmFragment<KaraokeFragmentBinding, KaraokeViewModel>(R.layout.karaoke_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<KaraokeViewModel>()

    override fun createViewModel(): KaraokeViewModel = vm

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
        val host = childFragmentManager.findFragmentById(R.id.okNavHost) as NavHostFragment
        btnBack.setOnClickListener {
            host.navController.popBackStack(
                R.id.karaokeMainFragment,
                false
            )
        }
        volSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
            {
                if (fromUser)
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
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
            }

        })
    }

    @Subscribe
    fun onAccompanyChanged(event: AccompanyChangedEvent)
    {
        if (event.acc == Accompany.BC)
        {
            btnAccompany.isChecked = false
        }
    }
//    override fun p()
//    {
//        super.performDataBinding()
//        btnBack.setOnClickListener { (okNavHost as NavHostFragment).navController.popBackStack(R.id.karaokeMainFragment,true) }
//    }
}
