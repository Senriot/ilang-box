package com.senriot.ilangbox.ui.karaoke

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.models.Artist
import com.android.karaoke.common.mvvm.BindingConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RealmAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KaraokeArtistSongsViewModel : AbstractViewModel()
{
    val artist = ObservableField<Artist>()

    val adapter by lazy {
        RealmAdapter(artist.get()!!.songs, BindingConfig(R.layout.ok_song_item, mapOf()), 3, 3)
    }

    init {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onPlaylistChanged(event: PlaylistChangedEvent) {
        adapter.notifyDataSetChanged()
    }
}
