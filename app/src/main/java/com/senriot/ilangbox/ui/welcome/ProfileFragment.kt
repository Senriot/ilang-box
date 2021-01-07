package com.senriot.ilangbox.ui.welcome

import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.ProfileFragmentBinding
import com.senriot.ilangbox.ui.langdu.ReadListFragment
import kotlinx.android.synthetic.main.profile_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import universum.studios.android.fragment.manage.FragmentController

class ProfileFragment :
    MvvmFragment<ProfileFragmentBinding, ProfileViewModel>(R.layout.profile_fragment)
{

    companion object
    {
        fun newInstance() = ProfileFragment()
    }

    private val vm by viewModel<ProfileViewModel>()

    override fun createViewModel(): ProfileViewModel = vm

    override val bindingVariable: Int = BR.vm

    lateinit var fragmentController: FragmentController

    override fun performDataBinding()
    {
        super.performDataBinding()
        fragmentController = FragmentController(context, childFragmentManager)
        fragmentController.viewContainerId = R.id.records_container
        rb_langdu_records.isChecked = true
        fragmentController.newRequest(LangduRecordsFragment()).replaceSame(true).execute()
    }

}