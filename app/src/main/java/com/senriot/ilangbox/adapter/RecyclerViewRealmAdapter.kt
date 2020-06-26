package com.senriot.ilangbox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android.karaoke.common.mvvm.BindingConfig
import com.senriot.ilangbox.BR
import io.realm.OrderedRealmCollection
import io.realm.RealmObject

class RecyclerViewRealmAdapter<T : RealmObject>(
    items: OrderedRealmCollection<T>,
    private val config: BindingConfig
) :
    RealmRecyclerViewAdapter<T, RecyclerViewRealmAdapter.RealmViewHolder>(items, true)
{
    class RealmViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealmViewHolder
    {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                config.layoutId,
                parent,
                false
            )
        return RealmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RealmViewHolder, position: Int)
    {
        holder.binding.setVariable(BR.item, getItem(position))
        config.bindingVariables.forEach {
            holder.binding.setVariable(it.key, it.value)
        }
    }
}