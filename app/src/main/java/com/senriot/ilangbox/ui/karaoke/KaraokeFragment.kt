package com.senriot.ilangbox.ui.karaoke

import com.android.karaoke.player.Accompany
import com.android.karaoke.player.events.AccompanyChangedEvent
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeFragmentBinding
import com.senriot.ilangbox.ui.NavFragment
import kotlinx.android.synthetic.main.karaoke_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.viewmodel.ext.android.viewModel
import universum.studios.android.fragment.manage.FragmentController

class KaraokeFragment :
    NavFragment<KaraokeFragmentBinding, KaraokeViewModel>(R.layout.karaoke_fragment)
{

//    private lateinit var fragmentController: FragmentController

    override val fragmentController: FragmentController by lazy {
        FragmentController(context, childFragmentManager).apply {
            viewContainerId = R.id.container
            factory = KaraokeFragments()
        }
    }

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<KaraokeViewModel>()

    override fun createViewModel(): KaraokeViewModel = vm

    init
    {
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
        fragmentController.newRequest(KaraokeFragments.karaokeMain).immediate(true).execute()
        btnSoundEffect.setOnClickListener {
            if (fragmentController.findCurrentFragment() !is SoundEffectFragment)
                fragmentController.newRequest(KaraokeFragments.soundEffect).addToBackStack(true)
                    .replaceSame(true).execute()
        }

        viewDataBinding!!.btnBack.setOnClickListener {
            fragmentController.fragmentManager.popBackStackImmediate()
        }
//        val host = childFragmentManager.findFragmentById(R.id.okNavHost) as NavHostFragment
//        btnBack.setOnClickListener {
//            host.navController.popBackStack(
//                R.id.karaokeMainFragment,
//                false
//            )
//        }
    }

    @Subscribe
    fun onAccompanyChanged(event: AccompanyChangedEvent)
    {
        btnAccompany.isChecked = event.acc != Accompany.BC
    }


//    override fun p()
//    {
//        super.performDataBinding()
//        btnBack.setOnClickListener { (okNavHost as NavHostFragment).navController.popBackStack(R.id.karaokeMainFragment,true) }
//    }
}
