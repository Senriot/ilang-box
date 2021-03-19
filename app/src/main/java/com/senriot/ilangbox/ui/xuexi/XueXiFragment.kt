package com.senriot.ilangbox.ui.xuexi

import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.XueXiFragmentBinding
import com.senriot.ilangbox.ui.NavFragment
import org.koin.android.viewmodel.ext.android.viewModel
import universum.studios.android.fragment.manage.FragmentController

class XueXiFragment : NavFragment<XueXiFragmentBinding, XueXiViewModel>(R.layout.xue_xi_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<XueXiViewModel>()

    override fun createViewModel(): XueXiViewModel = vm

    override val fragmentController: FragmentController by lazy {
        FragmentController(context, childFragmentManager).apply {
            viewContainerId = R.id.container
            factory = DangZhengFragments()
        }
    }
    override fun postInit()
    {
        super.postInit()
        fragmentController.newRequest(DangZhengFragments.dzMain).immediate(true).execute()
    }
}
