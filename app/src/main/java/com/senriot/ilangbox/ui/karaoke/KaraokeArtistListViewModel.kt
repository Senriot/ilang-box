package com.senriot.ilangbox.ui.karaoke

import android.view.View
import androidx.navigation.findNavController
import com.android.karaoke.common.models.Artist
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.mvvm.BindingConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RealmAdapter
import com.senriot.ilangbox.event.SearchTextChangedEvent
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KaraokeArtistListViewModel : AbstractViewModel()
{

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCleared()
    {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    val items by lazy {
        Realm.getDefaultInstance().where<Artist>().equalTo("status", 2L)
            .sort("hot", Sort.DESCENDING).findAll()
    }

    val adapter by lazy {
        RealmAdapter<Artist>(
            items,
            BindingConfig(R.layout.ok_artist_item, mapOf(Pair(BR.vm, this))),
            2,
            7
        )
    }

    fun showSongs(view: View, item: Artist)
    {
        view.findNavController().navigate(
            KaraokeArtistListFragmentDirections.actionKaraokeArtistListFragmentToKaraokeArtistSongsFragment(
                item
            )
        )
    }

    @Subscribe
    fun searchTextChanged(event: SearchTextChangedEvent)
    {
        val artists = Realm.getDefaultInstance().where<Artist>()
            .equalTo("status", 2L)
            .beginGroup().like("name",  event.text + "*").or()
            .like("input_code",  event.text + "*")
            .endGroup()
            .sort("hot", Sort.DESCENDING).findAll()
        adapter.updateData(artists)
    }
}
