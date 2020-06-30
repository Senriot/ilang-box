package com.senriot.ilangbox.ui.langdu

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.AuditionFragmentBinding

class AuditionFragment :
    MvvmFragment<AuditionFragmentBinding, AuditionViewModel>(R.layout.audition_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<AuditionViewModel>()

    override fun createViewModel(): AuditionViewModel = vm

    private val args by navArgs<AuditionFragmentArgs>()

    override fun postInit()
    {
        super.postInit()
        vm.item = args.record
    }
}
