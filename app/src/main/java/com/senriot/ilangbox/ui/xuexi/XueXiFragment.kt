package com.senriot.ilangbox.ui.xuexi

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.XueXiFragmentBinding

class XueXiFragment : MvvmFragment<XueXiFragmentBinding, XueXiViewModel>(R.layout.xue_xi_fragment) {

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<XueXiViewModel>()

    override fun createViewModel(): XueXiViewModel = vm
}
