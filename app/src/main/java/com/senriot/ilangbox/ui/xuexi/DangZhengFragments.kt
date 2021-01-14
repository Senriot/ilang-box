package com.senriot.ilangbox.ui.xuexi

import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class DangZhengFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(XueXiFragment::class)
        val dzMain = 0x41

        @JvmStatic
        @FactoryFragment(DzContentFragment::class)
        val dzContent = 0x42
    }
}