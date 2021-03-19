package com.senriot.ilangbox.ui.karaoke

import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeMainFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class KaraokeMainFragment : MvvmFragment<KaraokeMainFragmentBinding, KaraokeMainViewModel>(R.layout.karaoke_main_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<KaraokeMainViewModel>()

    override fun createViewModel(): KaraokeMainViewModel = vm
}
