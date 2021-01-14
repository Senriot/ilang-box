package com.senriot.ilangbox.ui.langdu

import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class LangDuFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(LdContentFragment::class)
        val content = 0x21

        @JvmStatic
        @FactoryFragment(LdBgmFragment::class)
        val bgm = 0x22

        @JvmStatic
        @FactoryFragment(AuditionFragment::class)
        val audition = 0x23

        @JvmStatic
        @FactoryFragment(LdMainFragment::class)
        val langduMain = 0x24

        @JvmStatic
        @FactoryFragment(LdItemDetailFragment::class)
        val itemDetail = 0x25

        @JvmStatic
        @FactoryFragment(LdRecordingFragment::class)
        val recording = 0x26
    }
}