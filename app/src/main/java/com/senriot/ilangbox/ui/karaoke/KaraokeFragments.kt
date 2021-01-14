package com.senriot.ilangbox.ui.karaoke

import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class KaraokeFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(KaraokeMainFragment::class)
        val karaokeMain = 0x31

        @JvmStatic
        @FactoryFragment(KaraokeArtistListFragment::class)
        val artistList = 0x32

        @JvmStatic
        @FactoryFragment(KaraokeArtistSongsFragment::class)
        val artistSongs = 0x33

        @JvmStatic
        @FactoryFragment(KaraokeListFragment::class)
        val songList = 0x34

        @JvmStatic
        @FactoryFragment(SoundEffectFragment::class)
        val soundEffect = 0x35
    }
}