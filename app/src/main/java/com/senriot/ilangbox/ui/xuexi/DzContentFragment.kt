package com.senriot.ilangbox.ui.xuexi

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.karaoke.common.models.DzXueXi
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.DzContentFragmentBinding

class DzContentFragment :
    MvvmFragment<DzContentFragmentBinding, DzContentViewModel>(R.layout.dz_content_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<DzContentViewModel>()

    private val args by navArgs<DzContentFragmentArgs>()

    override fun createViewModel(): DzContentViewModel = vm

    override fun performDataBinding()
    {
        super.performDataBinding()
        vm.item.set(args.item)
        vm.categoryId = args.categoryId
    }
}
