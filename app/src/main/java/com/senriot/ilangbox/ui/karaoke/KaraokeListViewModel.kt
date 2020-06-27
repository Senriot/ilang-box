package com.senriot.ilangbox.ui.karaoke

import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.models.ReadItem
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
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KaraokeListViewModel : AbstractViewModel()
{

    var title = ""

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

    val items by lazy {
        val query = Realm.getDefaultInstance().where<Song>()
        when (title)
        {
            "红歌会"   -> query.`in`("type_id", arrayOf(120, 48))
            "军歌"    -> query.equalTo("type_id", 120L)
            "民歌"    -> query.equalTo("type_id", 48L)
            "经典永流传" -> query.equalTo("type_id", 44L)
            "抒情"    -> query.equalTo("type_id", 121L)
            else    ->
            {
            }
        }
        query.findAll()
    }

    val itemBinding by lazy {
        ItemBinding.of<Song>(BR.item, R.layout.ok_song_item)
    }

    val adapter by lazy {
        RealmAdapter(items, BindingConfig(R.layout.ok_song_item, mapOf()), 3, 3)
    }


    @Subscribe
    fun searchTextChanged(event: SearchTextChangedEvent)
    {
        val query = Realm.getDefaultInstance().where<Song>()
        when (title)
        {
            "红歌会"   -> query.`in`("type_id", arrayOf(120, 48))
            "军歌"    -> query.equalTo("type_id", 120L)
            "民歌"    -> query.equalTo("type_id", 48L)
            "经典永流传" -> query.equalTo("type_id", 44L)
            "抒情"    -> query.equalTo("type_id", 121L)
            else    ->
            {
            }
        }
        val songs = query.beginGroup().like("name", event.text + "*").or()
            .like("input_code", event.text + "*")
            .endGroup()
            .findAll()
        adapter.updateData(songs)
    }
}
