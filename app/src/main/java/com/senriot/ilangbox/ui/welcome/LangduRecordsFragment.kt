package com.senriot.ilangbox.ui.welcome

import androidx.recyclerview.widget.LinearLayoutManager
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.FragmentLangduRecordsBinding
import com.senriot.ilangbox.ui.langdu.ReadListViewModel
import kotlinx.android.synthetic.main.fragment_langdu_records.*
import org.koin.android.viewmodel.ext.android.viewModel


class LangduRecordsFragment :
    MvvmFragment<FragmentLangduRecordsBinding, ReadListViewModel>(R.layout.fragment_langdu_records)
{
    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<ReadListViewModel>()

    override fun createViewModel(): ReadListViewModel = vm

    override fun performDataBinding()
    {
        super.performDataBinding()
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = vm.adapter
    }

}