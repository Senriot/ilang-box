package com.senriot.ilangbox.ui.xuexi

import android.os.Bundle
import com.android.karaoke.common.models.DzXueXi
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.DzContentFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class DzContentFragment :
    MvvmFragment<DzContentFragmentBinding, DzContentViewModel>(R.layout.dz_content_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<DzContentViewModel>()

//    private val args by navArgs<DzContentFragmentArgs>()

    override fun createViewModel(): DzContentViewModel = vm

    override fun init(savedInstanceState: Bundle?)
    {
        super.init(savedInstanceState)
        arguments?.getParcelable<DzXueXi>("item")?.let { vm.item.set(it) }
        arguments?.getString("categoryId")?.let { vm.categoryId = it }
    }

}
