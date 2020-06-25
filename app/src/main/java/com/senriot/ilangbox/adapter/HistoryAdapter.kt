package com.senriot.ilangbox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.android.karaoke.common.models.Song
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import io.realm.OrderedRealmCollection

class HistoryAdapter(items: OrderedRealmCollection<Song>) : RealmRecyclerViewAdapter<Song, PlaylistAdapter.RealmViewHolder>(items, true, true)
{

    init
    {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long
    {
        return getItem(position)?.id?.toLong()?:0L
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistAdapter.RealmViewHolder
    {
        val binding: ViewDataBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.history_item,
                        parent,
                        false
                )

        return PlaylistAdapter.RealmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.RealmViewHolder, position: Int)
    {
        data?.let {
            holder.binding.setVariable(BR.item, it[position])
        }
    }
}