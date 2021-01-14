package com.senriot.ilangbox.ui.welcome

import androidx.recyclerview.widget.LinearLayoutManager
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeRecordsFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class KaraokeRecordsFragment :
    MvvmFragment<KaraokeRecordsFragmentBinding, KaraokeRecordsViewModel>(R.layout.karaoke_records_fragment)
{

    companion object
    {
        fun newInstance() = KaraokeRecordsFragment()
    }

    private val viewModel: KaraokeRecordsViewModel by viewModel()


    override fun createViewModel(): KaraokeRecordsViewModel = viewModel

    override val bindingVariable: Int = BR.vm


    override fun postInit()
    {
        super.postInit()
        viewDataBinding?.let {
            it.list.layoutManager = LinearLayoutManager(activity)
            it.list.adapter = viewModel.adapter
        }
    }
}