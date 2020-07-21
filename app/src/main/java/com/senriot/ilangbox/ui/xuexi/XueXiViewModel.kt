package com.senriot.ilangbox.ui.xuexi

import android.view.View
import androidx.navigation.findNavController
import com.android.karaoke.common.models.Dict
import com.android.karaoke.common.models.DzXueXi
import com.android.karaoke.common.mvvm.BindingConfig
import com.android.karaoke.player.events.StartDzxxEvent
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RecyclerViewRealmAdapter
import io.realm.Realm
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates

class XueXiViewModel : AbstractViewModel()
{
    private val defaultDict =
        Realm.getDefaultInstance().where<Dict>().equalTo("id", 123L).findFirst()!!


    val adapter by lazy {
        val items =
            Realm.getDefaultInstance().where<DzXueXi>().equalTo("category.id", 123L).findAll()
        RecyclerViewRealmAdapter(items, BindingConfig(R.layout.dzxx_item, mapOf(Pair(BR.vm, this))))
    }


    var selectedDict: Dict by Delegates.observable(Dict(), { _, old, new ->
        if (old != new)
        {
            val items =
                Realm.getDefaultInstance().where<DzXueXi>().equalTo("category.id", new.id).findAll()
            adapter.updateData(items)
        }
    })

    fun startPlay(view: View, item: DzXueXi)
    {
        view.findNavController().navigate(
            XueXiFragmentDirections.actionXueXiFragmentToDzContentFragment(
                item,
                if (selectedDict.id <= 0) 123L else selectedDict.id
            )
        )
        EventBus.getDefault().post(StartDzxxEvent(item))
    }
}
