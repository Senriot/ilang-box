package com.senriot.ilangbox.ui.langdu

import android.view.View
import androidx.navigation.findNavController
import com.android.karaoke.common.models.Record
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.events.PlayRecordEvent
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import io.realm.Realm
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.greenrobot.eventbus.EventBus

class ReadListViewModel : AbstractViewModel()
{
    val items by lazy {
        Realm.getInstance(userConfig).where<Record>().findAll()
    }

    val itemBinding by lazy {
        ItemBinding.of<Record>(BR.item, R.layout.ld_record_item).bindExtra(BR.vm, this)
    }

    fun reRecording(view: View, item: Record)
    {
        view.findNavController()
            .navigate(ReadListFragmentDirections.actionReadListFragmentToLdItemDetailFragment(item.readItem!!))
    }

    fun playRecord(view: View, item: Record)
    {
        EventBus.getDefault().post(PlayRecordEvent(item))
        view.findNavController().navigate(
            ReadListFragmentDirections.actionReadListFragmentToAuditionFragment(item)
        )
    }
}
