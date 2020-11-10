package com.senriot.ilangbox.adapter

import android.os.Parcel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.karaoke.common.mvvm.BindingConfig
import com.apkfuns.logutils.LogUtils
import io.realm.RealmObject
import com.senriot.ilangbox.BR
import io.realm.OrderedRealmCollection

open class RealmAdapter<T : RealmObject> : RealmRecyclerViewAdapter<T, RealmAdapter.RealmViewHolder>
{
    private val config: BindingConfig
    val row: Int
    val column: Int
    var tolPage: Int = 0
    var currentPage: Int = 0

    constructor(items: OrderedRealmCollection<T>, config: BindingConfig, row: Int, column: Int) : super(items, true,true)
    {
        this.config = config
        this.row = row
        this.column = column
        setHasStableIds(true)
        if (items.isNotEmpty())
        {
            val size = items.size
            LogUtils.e(size)
            val pageCount = row * column
            tolPage = size / pageCount
            if (size % pageCount > 0)
            {
                tolPage += 1
            }
            else
            {
                tolPage = 1
            }
        }
    }

    override fun updateData(data: OrderedRealmCollection<T>?)
    {
        super.updateData(data)
        if (!data.isNullOrEmpty())
        {
            val size = data.size
            val pageCount = row * column
            tolPage = size / pageCount
            if (size % pageCount > 0)
            {
                tolPage += 1
            }
            else
            {
                tolPage = 1
            }
        }
    }


//    fun setItems(data: List<T>) {
//        view?.layoutManager?.scrollToPosition(0)
//        items = data
//        notifyDataSetChanged()
//
//        if (data.isNotEmpty()) {
//            val size = data.size
//            val pageCount = row * column
//            tolPage = size / pageCount
//            if (size % pageCount > 0) {
//                tolPage += 1
//            }
//        } else {
//            tolPage = 1
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealmViewHolder
    {
        val binding: ViewDataBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        config.layoutId,
                        parent,
                        false
                )
        view?.let {
            binding.root.layoutParams.width = (it.width - 8 * (column)) / column
        }
        return RealmViewHolder(binding)
    }

    override fun getItemCount(): Int
    {
        return row * column * tolPage
    }

    override fun getItemId(position: Int): Long
    {
        return if (position > data!!.size - 1)
        {
            0
        }
        else
            data!![position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: RealmViewHolder, position: Int)
    {
        val index = transformIndex(position)
        if (index > data!!.size - 1)
        {
            holder.binding.setVariable(BR.item, null)
        }
        else
        {
            holder.binding.setVariable(BR.item, data!![index])
            config.bindingVariables.forEach {
                holder.binding.setVariable(it.key, it.value)
            }
        }
    }

    class RealmViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    private var view: RecyclerView? = null

//    constructor(parcel: Parcel) : this(
//        TODO("config"),
//        parcel.readInt(),
//        parcel.readInt()
//    ) {
//        tolPage = parcel.readInt()
//        currentPage = parcel.readInt()
//    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView)
        view = recyclerView
    }


    fun nextPage()
    {
        val position = (view?.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
        view?.smoothScrollToPosition(position + (column * row - 2))
    }


    private fun transformIndex(index: Int): Int
    {
        val pageCount = row * column
        val curPageIndex = index / pageCount
        val divisor = index % pageCount
        val pre = divisor % row
        var transformIndex = if (pre == 0)
        {
            divisor / row
        }
        else
        {
            column * pre + divisor / row
        }
        transformIndex += curPageIndex * pageCount
        return transformIndex
    }
}