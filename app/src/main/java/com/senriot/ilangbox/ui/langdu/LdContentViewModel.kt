package com.senriot.ilangbox.ui.langdu

import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.android.karaoke.common.models.CategoryItem
import com.android.karaoke.common.models.ReadCategory
import com.android.karaoke.common.models.ReadItem
import com.android.karaoke.common.mvvm.BindingConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RecyclerViewRealmAdapter
import com.senriot.ilangbox.event.SearchTextChangedEvent
import io.realm.Realm
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LdContentViewModel : AbstractViewModel()
{

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onCleared()
    {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    var curSelectedId = "1"

    val categories by lazy {
        Realm.getDefaultInstance().where<ReadCategory>().sort(ReadCategory.COL_ID).findAll()
    }

    val categoryBinding by lazy {
        ItemBinding.of<ReadCategory>(
            BR.item,
            R.layout.read_category_item
        ).bindExtra(BR.vm, this)
    }

    val categoryAdapter = object : BindingRecyclerViewAdapter<ReadCategory>()
    {
        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: ReadCategory?
        )
        {
            binding.root.setOnClickListener {
                curSelectedId = item!!.id
                notifyDataSetChanged()
            }
            if (curSelectedId == item!!.id)
            {
                (binding.root as TextView).textSize = 22.0F
            } else
            {
                (binding.root as TextView).textSize = 18.0F
            }
            super.onBindBinding(binding, variableId, layoutRes, position, item)
        }
    }

    val itemsAdapter by lazy {
        val items =
            Realm.getDefaultInstance().where<ReadItem>().equalTo("category.id", curSelectedId)
                .sort(ReadItem.COL_ID).findAll()
        RecyclerViewRealmAdapter<ReadItem>(
            items,
            BindingConfig(R.layout.read_item, mapOf(Pair(BR.vm, this)))
        )
    }

//    val items by lazy {
//        Realm.getDefaultInstance().where<ReadItem>().equalTo("category.id", curSelectedId)
//            .sort(ReadItem.COL_ID).findAll()
//    }

    val itemBinding by lazy {
        ItemBinding.of<ReadItem>(
            BR.item,
            R.layout.read_item
        ).bindExtra(BR.vm, this)
    }

    fun itemSelected(view: View, item: ReadItem)
    {
        view.findNavController()
            .navigate(LdContentFragmentDirections.actionLdContentFragmentToLdItemDetailFragment(item))
    }

    fun categorySelected(item: ReadCategory)
    {
        this.curSelectedId = item.id
        val items =
            Realm.getDefaultInstance().where<ReadItem>().equalTo("category.id", curSelectedId)
                .sort(ReadItem.COL_ID).findAll()
        itemsAdapter.updateData(items)
    }

    @Subscribe
    fun searchTextChanged(event: SearchTextChangedEvent)
    {
        val items = Realm.getDefaultInstance().where<ReadItem>()
            .equalTo("category.id", curSelectedId)
            .like("name", "?" + event.text + "*")
            .sort(ReadItem.COL_ID).findAll()
        itemsAdapter.updateData(items)
    }
}
