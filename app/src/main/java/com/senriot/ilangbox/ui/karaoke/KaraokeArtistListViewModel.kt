package com.senriot.ilangbox.ui.karaoke

import android.view.View
import androidx.navigation.findNavController
import com.android.karaoke.common.models.Artist
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.mvvm.BindingConfig
import com.android.karaoke.common.realm.songsConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RealmAdapter
import com.senriot.ilangbox.event.SearchTextChangedEvent
import com.senriot.ilangbox.ui.GeneralViewModelCommands
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
        Realm.getInstance(songsConfig).where<Artist>()
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
        dispatchCommand(GeneralViewModelCommands.showArtistSongs(item))
//        view.findNavController().navigate(
//            KaraokeArtistListFragmentDirections.actionKaraokeArtistListFragmentToKaraokeArtistSongsFragment(
//                item
//            )
//        )
    }

    @Subscribe
    fun searchTextChanged(event: SearchTextChangedEvent)
    {
        val artists = Realm.getInstance(songsConfig).where<Artist>()
            .beginGroup().like("name", event.text + "*").or()
            .like("input_code", event.text + "*")
            .endGroup()
            .sort("hot", Sort.DESCENDING).findAll()
        adapter.updateData(artists)
    }
}
