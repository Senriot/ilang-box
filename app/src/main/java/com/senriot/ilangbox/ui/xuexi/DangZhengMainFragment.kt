package com.senriot.ilangbox.ui.xuexi

import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.FragmentDangZhengMainBinding
import org.koin.android.viewmodel.ext.android.viewModel

class DangZhengMainFragment :
    MvvmFragment<FragmentDangZhengMainBinding, DangZhengMainViewModel>(R.layout.fragment_dang_zheng_main)
{
    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<DangZhengMainViewModel>()

    override fun createViewModel(): DangZhengMainViewModel = vm

}