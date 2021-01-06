package com.senriot.ilangbox.ui.karaoke

import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.SoundEffectFragmentBinding
import kotlinx.android.synthetic.main.sound_effect_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class SoundEffectFragment :
    MvvmFragment<SoundEffectFragmentBinding, SoundEffectViewModel>(R.layout.sound_effect_fragment)
{
    private val vm by viewModel<SoundEffectViewModel>()

    override fun createViewModel(): SoundEffectViewModel = vm

    override val bindingVariable: Int = BR.vm

    override fun performDataBinding()
    {
        super.performDataBinding()
        micSeekBar.setOnSeekBarChangeListener(vm.volumeChangedListener)
        headsetSeekBar.setOnSeekBarChangeListener(vm.volumeChangedListener)
        soundSeekBar.setOnSeekBarChangeListener(vm.volumeChangedListener)
    }

}