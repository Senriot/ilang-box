package com.senriot.ilangbox.ui.langdu

import android.os.Bundle
import android.widget.SeekBar
import com.android.karaoke.player.events.ChangeBgmEvent
import com.android.karaoke.player.events.PlayRecordEvent
import com.android.karaoke.player.events.StartRecordingEvent
import com.android.karaoke.player.events.StopAuditionEvent
import com.arthurivanets.mvvm.MvvmFragment
import com.arthurivanets.mvvm.events.Command
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LangDuFragmentBinding
import com.senriot.ilangbox.event.ShowReadListEvent
import com.senriot.ilangbox.ui.GeneralViewModelCommands
import com.senriot.ilangbox.ui.NavFragment
import com.senriot.ilangbox.ui.karaoke.KaraokeFragments
import kotlinx.android.synthetic.main.lang_du_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.viewmodel.ext.android.viewModel
import universum.studios.android.fragment.manage.FragmentController

class LangDuFragment :
    NavFragment<LangDuFragmentBinding, LangDuViewModel>(R.layout.lang_du_fragment)
{

    override val fragmentController: FragmentController by lazy {
        FragmentController(context, childFragmentManager).apply {
            viewContainerId = R.id.container
            factory = LangDuFragments()
        }
    }

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
        fragmentController.newRequest(LangDuFragments.langduMain).immediate(true).execute()
        bgmSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        micSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        btnDefault.setOnClickListener {
            vm.micVolume.set(34)
            vm.soundVolume.set(34)
            vm.sendEffectValue(34, "03")
            vm.sendEffectValue(34, "02")
            vm.sp.micVolume = 34
            vm.sp.headsetVolume = 34
        }
    }

    @Subscribe
    fun onShowReadList(event: ShowReadListEvent)
    {
//        val host = childFragmentManager.findFragmentById(R.id.ldNavHost) as NavHostFragment
//        LogUtils.d(host.navController.currentDestination)
//        if (host.navController.currentDestination?.label != "ReadListFragment")
//            host.navController.navigate(Uri.parse("https://www.senriot.com/ilang-box/readlist"))
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

    @Subscribe
    fun showReading(event: StartRecordingEvent)
    {
        fragmentController.newRequest(LangDuFragments.recording)
            .arguments(Bundle().apply { putParcelable("item", event.item) })
            .replaceSame(true)
            .addToBackStack(true)
            .execute()
    }

    @Subscribe
    fun showAudition(event: PlayRecordEvent)
    {
        fragmentController.newRequest(LangDuFragments.audition)
            .arguments(Bundle().apply { putParcelable("item", event.record) })
            .addToBackStack(true)
            .replaceSame(true)
            .execute()
    }

    @Subscribe
    fun stopAudition(event: StopAuditionEvent)
    {
        if (fragmentController.findCurrentFragment() is AuditionFragment)
        {
            fragmentController.fragmentManager.popBackStack()
        }
    }

    @Subscribe
    fun changeBgmEvent(event: ChangeBgmEvent)
    {
        if (fragmentController.findCurrentFragment() is LdBgmFragment)
        {
            fragmentController.fragmentManager.popBackStack()
        }
    }
}
