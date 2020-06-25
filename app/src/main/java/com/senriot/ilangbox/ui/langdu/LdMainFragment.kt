package com.senriot.ilangbox.ui.langdu

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
import com.senriot.ilangbox.databinding.LdMainFragmentBinding

class LdMainFragment :
    MvvmFragment<LdMainFragmentBinding, LdMainViewModel>(R.layout.ld_main_fragment) {

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdMainViewModel>()

    override fun createViewModel(): LdMainViewModel = vm
}
