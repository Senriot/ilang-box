package com.senriot.ilangbox.ui.karaoke

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.karaoke.player.Accompany
import com.android.karaoke.player.events.AccompanyChangedEvent
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeFragmentBinding
import kotlinx.android.synthetic.main.karaoke_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.viewmodel.ext.android.viewModel

class KaraokeFragment :
    MvvmFragment<KaraokeFragmentBinding, KaraokeViewModel>(R.layout.karaoke_fragment)
{

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
        val host = childFragmentManager.findFragmentById(R.id.okNavHost) as NavHostFragment
        btnBack.setOnClickListener {
            host.navController.popBackStack(
                R.id.karaokeMainFragment,
                false
            )
        }
    }

    @Subscribe
    fun onAccompanyChanged(event: AccompanyChangedEvent)
    {
        if (event.acc == Accompany.BC)
        {
            btnAccompany.isChecked = false
        }
    }
//    override fun p()
//    {
//        super.performDataBinding()
//        btnBack.setOnClickListener { (okNavHost as NavHostFragment).navController.popBackStack(R.id.karaokeMainFragment,true) }
//    }
}
