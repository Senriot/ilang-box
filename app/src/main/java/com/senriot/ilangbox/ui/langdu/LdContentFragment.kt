package com.senriot.ilangbox.ui.langdu

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdContentFragmentBinding
import kotlinx.android.synthetic.main.ld_content_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class LdContentFragment :
    MvvmFragment<LdContentFragmentBinding, LdContentViewModel>(R.layout.ld_content_fragment) {

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdContentViewModel>()


    private val args by navArgs<LdContentFragmentArgs>()

    override fun createViewModel(): LdContentViewModel = vm

    override fun performDataBinding() {
        super.performDataBinding()
        vm.curSelectedId = args.categroyId
        nav.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
        list.adapter = vm.itemsAdapter
    }
}
