package com.senriot.ilangbox.ui.langdu

import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.android.karaoke.player.events.StartRecordingEvent
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdItemDetailFragmentBinding
import kotlinx.android.synthetic.main.ld_item_detail_fragment.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel

class LdItemDetailFragment :
    MvvmFragment<LdItemDetailFragmentBinding, LdItemDetailViewModel>(R.layout.ld_item_detail_fragment) {

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdItemDetailViewModel>()
    private val args by navArgs<LdItemDetailFragmentArgs>()
    override fun createViewModel(): LdItemDetailViewModel = vm

    override fun performDataBinding() {
        super.performDataBinding()
        vm.item.set(args.item)
        btnStart.setOnClickListener {
            EventBus.getDefault().post(StartRecordingEvent(args.item))
            it.findNavController().navigate(LdItemDetailFragmentDirections.actionLdItemDetailFragmentToLdRecordingFragment(args.item))

        }
    }
}
