package com.senriot.ilangbox.ui.langdu

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.android.karaoke.common.models.*
import com.android.karaoke.common.mvvm.BindingConfig
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RecyclerViewRealmAdapter
import com.senriot.ilangbox.event.SearchTextChangedEvent
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.properties.Delegates

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

    var curSelectedId: String by Delegates.observable("1", { _, old, new ->

        val query = Realm.getDefaultInstance().where<ReadItem>()
        if (new != "0")
        {
            query.equalTo("category_id", new)
        }

        val items = query.sort("create_time", Sort.DESCENDING).findAll()
        itemsAdapter.updateData(items)
    })

    val categories by lazy {
        Realm.getDefaultInstance().where<Category>().equalTo("pid", "1287240189223268354")
            .sort("sort_no", Sort.ASCENDING).findAll()
    }

    val categoryBinding by lazy {
        ItemBinding.of<Category>(
            BR.item,
            R.layout.read_category_item
        ).bindExtra(BR.vm, this)
    }

    val categoryAdapter = object : BindingRecyclerViewAdapter<Category>()
    {

        init
        {
            setHasStableIds(true)
        }


        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: Category?
        )
        {

            super.onBindBinding(binding, variableId, layoutRes, position, item)
            binding.root.setOnClickListener {
                curSelectedId = item!!.id
                notifyDataSetChanged()
            }
            if (curSelectedId == item!!.id)
            {
                binding.root.setBackgroundResource(R.drawable.ic_ld_nav_bg)
            } else
            {
                binding.root.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    val itemsAdapter by lazy {
        val query = Realm.getDefaultInstance().where<ReadItem>()

        if (curSelectedId != "0")
        {
            query.equalTo("category_id", curSelectedId)
        }
        val items = query.sort("id").findAll()
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
            Realm.getDefaultInstance().where<ReadItem>().equalTo("category_id", curSelectedId)
                .sort("id").findAll()
        itemsAdapter.updateData(items)
    }

    @Subscribe
    fun searchTextChanged(event: SearchTextChangedEvent)
    {
        val query = Realm.getDefaultInstance().where<ReadItem>()
        if (curSelectedId != "0")
        {
            query.equalTo("category_id", curSelectedId)
        }

        val items = query.like("name", event.text + "*")
            .sort("id").findAll()
        itemsAdapter.updateData(items)
    }
}
