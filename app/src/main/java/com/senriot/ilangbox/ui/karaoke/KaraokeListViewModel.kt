package com.senriot.ilangbox.ui.karaoke

import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.mvvm.BindingConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RealmAdapter
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KaraokeListViewModel : AbstractViewModel() {

    var title = ""

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

    val items by lazy {
        Realm.getDefaultInstance().where<Song>().equalTo("typeId", 36L).or().equalTo("typeId", 40L)
            .sort("hot", Sort.DESCENDING).findAll()
    }

    val itemBinding by lazy {
        ItemBinding.of<Song>(BR.item, R.layout.ok_song_item)
    }

    val adapter by lazy {
        RealmAdapter(items, BindingConfig(R.layout.ok_song_item, mapOf()), 3, 3)
    }
}
