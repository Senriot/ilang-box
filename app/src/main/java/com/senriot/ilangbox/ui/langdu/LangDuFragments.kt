package com.senriot.ilangbox.ui.langdu

import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class LangDuFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(LdContentFragment::class)
        val content = 0x01

        @JvmStatic
        @FactoryFragment(LdBgmFragment::class)
        val bgm = 0x02

        @JvmStatic
        @FactoryFragment(AuditionFragment::class)
        val audition = 0x03

        @JvmStatic
        @FactoryFragment(LdMainFragment::class)
        val main = 0x04

        @JvmStatic
        @FactoryFragment(LdItemDetailFragment::class)
        val itemDetail = 0x05

        @JvmStatic
        @FactoryFragment(LdRecordingFragment::class)
        val recording = 0x06
    }
}