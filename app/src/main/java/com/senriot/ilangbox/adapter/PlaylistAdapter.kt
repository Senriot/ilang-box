package com.senriot.ilangbox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android.karaoke.common.models.Song
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import io.realm.OrderedRealmCollection

class PlaylistAdapter(items: OrderedRealmCollection<Song>) :
    RealmRecyclerViewAdapter<Song, PlaylistAdapter.RealmViewHolder>(items, true, true)
{
    class RealmViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)


    init
    {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealmViewHolder
    {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.playlist_item,
                parent,
                false
            )

        return RealmViewHolder(binding)
    }

    override fun getItemId(position: Int): Long
    {
        return getItem(position)?.id?.hashCode()?.toLong() ?: 0L
    }

    override fun onBindViewHolder(holder: RealmViewHolder, position: Int)
    {
        data?.let {
            holder.binding.setVariable(BR.item, it[position])
            holder.binding.setVariable(BR.index, "${position + 1}")
        }
    }
}