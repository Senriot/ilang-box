package com.senriot.ilangbox.ui.karaoke

import android.os.Bundle
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeListFragmentBinding
import kotlinx.android.synthetic.main.karaoke_list_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class KaraokeListFragment :
    MvvmFragment<KaraokeListFragmentBinding, KaraokeListViewModel>(R.layout.karaoke_list_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<KaraokeListViewModel>()

    // private val args by navArgs<KaraokeListFragmentArgs>()

    override fun createViewModel(): KaraokeListViewModel = vm

    override fun init(savedInstanceState: Bundle?)
    {
        super.init(savedInstanceState)
        val t = arguments?.getString("title") ?: ""
        vm.title = t
    }

    override fun postInit()
    {
        super.postInit()
        scrollView.setAdapter(vm.adapter)
    }
}
