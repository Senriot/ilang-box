package com.senriot.ilangbox.ui.langdu

import androidx.recyclerview.widget.GridLayoutManager
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdBgmFragmentBinding
import kotlinx.android.synthetic.main.ld_bgm_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class LdBgmFragment : MvvmFragment<LdBgmFragmentBinding, LdBgmViewModel>(R.layout.ld_bgm_fragment) {

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdBgmViewModel>()

    override fun createViewModel(): LdBgmViewModel = vm

    override fun performDataBinding() {
        super.performDataBinding()
        list.layoutManager = GridLayoutManager(activity, 4, GridLayoutManager.HORIZONTAL, false)
        list.adapter = vm.adapter
    }
}
