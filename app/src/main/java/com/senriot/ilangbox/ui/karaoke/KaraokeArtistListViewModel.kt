package com.senriot.ilangbox.ui.karaoke

import android.view.View
import androidx.navigation.findNavController
import com.android.karaoke.common.models.Artist
import com.android.karaoke.common.mvvm.BindingConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RealmAdapter
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class KaraokeArtistListViewModel : AbstractViewModel()
{
    val items by lazy {
        Realm.getDefaultInstance().where<Artist>().sort("hot", Sort.DESCENDING).findAll()
    }

    val adapter by lazy {
        RealmAdapter<Artist>(items, BindingConfig(R.layout.ok_artist_item, mapOf(Pair(BR.vm, this))), 2, 7)
    }

    fun showSongs(view: View, item: Artist)
    {
        view.findNavController().navigate(
                KaraokeArtistListFragmentDirections.actionKaraokeArtistListFragmentToKaraokeArtistSongsFragment(
                        item
                )
        )
    }
}
