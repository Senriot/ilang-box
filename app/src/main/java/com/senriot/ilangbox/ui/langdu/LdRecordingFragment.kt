package com.senriot.ilangbox.ui.langdu

import androidx.navigation.fragment.navArgs
import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.player.events.StopReadEvent
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdRecordingFragmentBinding
import kotlinx.android.synthetic.main.ld_recording_fragment.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel

class LdRecordingFragment :
    MvvmFragment<LdRecordingFragmentBinding, LdRecordingViewModel>(R.layout.ld_recording_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdRecordingViewModel>()

    override fun createViewModel(): LdRecordingViewModel = vm

    override fun postInit()
    {
        super.postInit()
        arguments?.getParcelable<ReadItem>("item")?.let { item ->
            vm.item.set(item)
            vm.title.set(item.name)
            item.bg_music?.let { vm.curBgmName.set(it) }
        }
    }
}
