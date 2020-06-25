package com.senriot.ilangbox.ui.langdu

import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.android.karaoke.common.models.CategoryItem
import com.android.karaoke.common.models.ReadCategory
import com.android.karaoke.common.models.ReadItem
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import io.realm.Realm
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

class LdContentViewModel : AbstractViewModel() {

    var curSelectedId = "1"

    val categories by lazy {
        Realm.getDefaultInstance().where<ReadCategory>().sort(ReadCategory.COL_ID).findAll()
    }

    val categoryBinding by lazy {
        ItemBinding.of<ReadCategory>(
            BR.item,
            R.layout.read_category_item
        )
    }

    val categoryAdapter = object : BindingRecyclerViewAdapter<ReadCategory>() {
        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: ReadCategory?
        ) {
            binding.root.setOnClickListener {
                curSelectedId = item!!.id
                notifyDataSetChanged()
            }
            if (curSelectedId == item!!.id) {
                (binding.root as TextView).textSize = 22.0F
            } else {
                (binding.root as TextView).textSize = 18.0F
            }
            super.onBindBinding(binding, variableId, layoutRes, position, item)
        }
    }

    val items by lazy {
        Realm.getDefaultInstance().where<ReadItem>().sort(ReadItem.COL_ID).findAll()
    }

    val itemBinding by lazy {
        ItemBinding.of<ReadItem>(
            BR.item,
            R.layout.read_item
        ).bindExtra(BR.vm, this)
    }

    fun itemSelected(view: View, item: ReadItem) {
        view.findNavController()
            .navigate(LdContentFragmentDirections.actionLdContentFragmentToLdItemDetailFragment(item))
    }
}
