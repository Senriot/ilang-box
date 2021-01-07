package com.senriot.ilangbox.ui.welcome

import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class WelcomeFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(InitFragment::class)
        val INIT = 0x01

        @JvmStatic
        @FactoryFragment(ProfileFragment::class)
        val PROFILE = 0x02

        @JvmStatic
        @FactoryFragment(LoginFragment::class)
        val LOGIN = 0x03
    }
}