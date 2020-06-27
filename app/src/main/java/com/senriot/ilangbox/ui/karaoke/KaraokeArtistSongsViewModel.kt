package com.senriot.ilangbox.ui.karaoke

import androidx.databinding.ObservableField
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.models.Artist
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.mvvm.BindingConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RealmAdapter
import com.senriot.ilangbox.event.SearchTextChangedEvent
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KaraokeArtistSongsViewModel : AbstractViewModel()
{


    val artist = ObservableField<Artist>()

    val adapter by lazy {
        val songs = Realm.getDefaultInstance().where<Song>().equalTo("singer_id", artist.get()!!.id)
            .sort("hot", Sort.DESCENDING).findAll()

        RealmAdapter(songs, BindingConfig(R.layout.ok_song_item, mapOf()), 3, 3)
    }

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCleared()
    {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onPlaylistChanged(event: PlaylistChangedEvent)
    {
        adapter.notifyDataSetChanged()
    }

    @Subscribe
    fun searchTextChanged(event: SearchTextChangedEvent)
    {
        val songs = Realm.getDefaultInstance().where<Song>().equalTo("singer_id", artist.get()!!.id)
            .beginGroup().like("name", event.text + "*").or()
            .like("input_code", event.text + "*")
            .endGroup()
            .sort("hot", Sort.DESCENDING).findAll()
        adapter.updateData(songs)
    }
}
