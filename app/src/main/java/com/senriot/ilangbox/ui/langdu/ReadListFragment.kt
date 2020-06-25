package com.senriot.ilangbox.ui.langdu

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.ReadListFragmentBinding
import kotlinx.android.synthetic.main.read_list_fragment.*

class ReadListFragment : MvvmFragment<ReadListFragmentBinding, ReadListViewModel>(R.layout.read_list_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<ReadListViewModel>()

    override fun createViewModel(): ReadListViewModel = vm

    override fun performDataBinding()
    {
        super.performDataBinding()
        list.layoutManager = LinearLayoutManager(activity)
    }
}
