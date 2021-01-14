package com.senriot.ilangbox.ui.langdu

import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdMainFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class LdMainFragment :
    MvvmFragment<LdMainFragmentBinding, LdMainViewModel>(R.layout.ld_main_fragment) {

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdMainViewModel>()

    override fun createViewModel(): LdMainViewModel = vm
}
