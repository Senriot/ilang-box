package com.senriot.ilangbox.ui.langdu

import androidx.recyclerview.widget.LinearLayoutManager
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.ReadListFragmentBinding
import kotlinx.android.synthetic.main.read_list_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReadListFragment : MvvmFragment<ReadListFragmentBinding, ReadListViewModel>(R.layout.read_list_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<ReadListViewModel>()

    override fun createViewModel(): ReadListViewModel = vm

    override fun postInit()
    {
        super.postInit()
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = vm.adapter
    }
}
