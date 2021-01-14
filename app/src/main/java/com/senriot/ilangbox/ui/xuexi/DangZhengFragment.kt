package com.senriot.ilangbox.ui.xuexi

import android.os.Bundle
import com.android.karaoke.player.events.StartDzxxEvent
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.DangZhengFragmentBinding
import com.senriot.ilangbox.ui.NavFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.viewmodel.ext.android.viewModel
import universum.studios.android.fragment.manage.FragmentController

class DangZhengFragment :
    NavFragment<DangZhengFragmentBinding, DangZhengViewModel>(R.layout.dang_zheng_fragment)
{
    override val fragmentController: FragmentController by lazy {
        FragmentController(context, childFragmentManager).apply {
            viewContainerId = R.id.container
            factory = DangZhengFragments()
        }
    }

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<DangZhengViewModel>()

    override fun createViewModel(): DangZhengViewModel = vm

    override fun init(savedInstanceState: Bundle?)
    {
        super.init(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView()
    {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun postInit()
    {
        super.postInit()
        fragmentController.newRequest(DangZhengFragments.dzMain).immediate(true).execute()
    }

    @Subscribe
    fun startDzxx(event: StartDzxxEvent)
    {
        fragmentController.newRequest(DangZhengFragments.dzContent).arguments(Bundle().apply {
            putParcelable("item", event.currentItem)
            putString("categoryId", event.categoryId)
        }).addToBackStack(fragmentController.findCurrentFragment() !is DzContentFragment)
            .execute()

    }

//    companion object
//    {
//        fun newInstance() = DangZhengFragment()
//    }
//
//    private lateinit var viewModel: DangZhengViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View?
//    {
//        return inflater.inflate(R.layout.dang_zheng_fragment, container, false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?)
//    {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(DangZhengViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}