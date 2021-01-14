package com.senriot.ilangbox

import com.senriot.ilangbox.ui.karaoke.KaraokeFragment
import com.senriot.ilangbox.ui.langdu.LangDuFragment
import com.senriot.ilangbox.ui.welcome.ProfileFragment
import com.senriot.ilangbox.ui.xuexi.DangZhengFragment
import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class MainActFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(LangDuFragment::class)
        val LANGDU = 0x11

        @JvmStatic
        @FactoryFragment(KaraokeFragment::class)
        val KARAOKE = 0x12

        @JvmStatic
        @FactoryFragment(DangZhengFragment::class)
        val DZ = 0x13

        @JvmStatic
        @FactoryFragment(ProfileFragment::class)
        val profile = 0x14
    }
}