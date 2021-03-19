package com.senriot.ilangbox.ui.xuexi

import com.senriot.ilangbox.ui.karaoke.SoundEffectFragment
import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class DangZhengFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(XueXiFragment::class)
        val dzHome = 0x41

        @JvmStatic
        @FactoryFragment(DzContentFragment::class)
        val dzContent = 0x42

        @JvmStatic
        @FactoryFragment(DangZhengMainFragment::class)
        val dzMain = 0x43

        @JvmStatic
        @FactoryFragment(DangZhengVideoListFragment::class)
        val videoList = 0x44

        @JvmStatic
        @FactoryFragment(SoundEffectFragment::class)
        val soundEffect = 0x45

    }
}