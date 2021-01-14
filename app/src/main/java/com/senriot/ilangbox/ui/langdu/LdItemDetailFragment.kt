package com.senriot.ilangbox.ui.langdu

import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.player.events.StartRecordingEvent
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdItemDetailFragmentBinding
import kotlinx.android.synthetic.main.ld_item_detail_fragment.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel

class LdItemDetailFragment :
    MvvmFragment<LdItemDetailFragmentBinding, LdItemDetailViewModel>(R.layout.ld_item_detail_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdItemDetailViewModel>()
    override fun createViewModel(): LdItemDetailViewModel = vm

    override fun postInit()
    {
        super.postInit()
        val item = arguments?.getParcelable<ReadItem>("item")
        vm.item.set(item)
        btnStart.setOnClickListener {
            item?.let {
                EventBus.getDefault().post(StartRecordingEvent(item))
            }
//            EventBus.getDefault().post(StartRecordingEvent(item))
//            it.findNavController().navigate(
//                LdItemDetailFragmentDirections.actionLdItemDetailFragmentToLdRecordingFragment(args.item)
//            )
        }
    }
}
