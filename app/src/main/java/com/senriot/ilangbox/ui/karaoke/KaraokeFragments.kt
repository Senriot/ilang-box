package com.senriot.ilangbox.ui.karaoke

import universum.studios.android.fragment.annotation.FactoryFragment
import universum.studios.android.fragment.manage.BaseFragmentFactory

class KaraokeFragments : BaseFragmentFactory()
{
    companion object
    {
        @JvmStatic
        @FactoryFragment(KaraokeMainFragment::class)
        val main = 0x01

        @JvmStatic
        @FactoryFragment(KaraokeArtistListFragment::class)
        val artistList = 0x02

        @JvmStatic
        @FactoryFragment(KaraokeArtistSongsFragment::class)
        var artistSongs = 0x03

        @JvmStatic
        @FactoryFragment(KaraokeListFragment::class)
        val songList = 0x04

        @JvmStatic
        @FactoryFragment(KaraokeListFragment::class)
        val soundEffect = 0x05
    }
}