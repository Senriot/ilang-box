package com.senriot.ilangbox.ui.langdu

import android.view.View
import androidx.navigation.findNavController
import com.android.karaoke.common.models.ReadBgm
import com.android.karaoke.common.realm.songsConfig
import com.android.karaoke.player.events.ChangeBgmEvent
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.LdBgmAdapter
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.greenrobot.eventbus.EventBus

class LdBgmViewModel : AbstractViewModel()
{
    val items by lazy { Realm.getInstance(songsConfig).where<ReadBgm>().findAll() }
    val adapter by lazy { LdBgmAdapter(items) }
    val itemBinding by lazy {
        ItemBinding.of<ReadBgm>(
            BR.item,
            R.layout.ld_bgm_item
        ).bindExtra(BR.vm, this)
    }

    fun itemSelected(v: View, item: ReadBgm)
    {
        EventBus.getDefault().post(ChangeBgmEvent(item))
        v.findNavController().popBackStack()
    }
}

