package com.senriot.ilangbox.ui.xuexi

import android.os.Bundle
import com.android.karaoke.common.models.Category
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.DangZhengVideoListFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class DangZhengVideoListFragment :
    MvvmFragment<DangZhengVideoListFragmentBinding, DangZhengVideoListViewModel>(R.layout.dang_zheng_video_list_fragment)
{


    private val viewModel: DangZhengVideoListViewModel by viewModel()


    override fun createViewModel(): DangZhengVideoListViewModel = viewModel

    override val bindingVariable: Int = BR.vm


    override fun init(savedInstanceState: Bundle?)
    {
        super.init(savedInstanceState)
        val c: Category? = arguments?.getParcelable("category")
        c?.let { viewModel.category.set(it) }
    }
}