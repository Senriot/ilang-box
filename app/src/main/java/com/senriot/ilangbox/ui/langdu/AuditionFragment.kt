package com.senriot.ilangbox.ui.langdu

import com.android.karaoke.common.models.Record
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.AuditionFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class AuditionFragment :
    MvvmFragment<AuditionFragmentBinding, AuditionViewModel>(R.layout.audition_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<AuditionViewModel>()

    override fun createViewModel(): AuditionViewModel = vm

//    private val args by navArgs<AuditionFragmentArgs>()

    override fun postInit()
    {
        super.postInit()
        arguments?.getParcelable<Record>("item")?.let {
            vm.item = it
            vm.title.set(it.readItem!!.name)
        }
    }
}
