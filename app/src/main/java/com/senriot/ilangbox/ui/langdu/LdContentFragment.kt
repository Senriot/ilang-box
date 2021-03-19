package com.senriot.ilangbox.ui.langdu

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthurivanets.mvvm.MvvmFragment
import com.arthurivanets.mvvm.events.Command
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.LdContentFragmentBinding
import com.senriot.ilangbox.ui.GeneralViewModelCommands
import kotlinx.android.synthetic.main.ld_content_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class LdContentFragment :
    MvvmFragment<LdContentFragmentBinding, LdContentViewModel>(R.layout.ld_content_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LdContentViewModel>()

//    private val args by navArgs<LdContentFragmentArgs>()

    override fun createViewModel(): LdContentViewModel = vm

    override fun postInit()
    {
        super.postInit()
        vm.curSelectedId = arguments?.getString("categoryId") ?: "1"
        nav.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
        list.adapter = vm.itemsAdapter
    }

    override fun onHandleCommand(command: Command<*>)
    {
        super.onHandleCommand(command)
        if (command is GeneralViewModelCommands.showLangDuDetail)
        {
            val f = this.parentFragment
            if (f is LangDuFragment)
                f.fragmentController.newRequest(LangDuFragments.itemDetail)
                    .arguments(Bundle().apply { putParcelable("item", command.payload) })
                    .addToBackStack(true)
                    .execute()
        }
    }
}
