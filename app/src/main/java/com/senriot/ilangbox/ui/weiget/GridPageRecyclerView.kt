package com.senriot.ilangbox.ui.weiget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.senriot.ilangbox.R
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class GridPageRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) :
        RecyclerView(
                context,
                attrs,
                defStyleAttr
        )
{

    private var row = 3
    private var column = 6

    var onLoadMoreListener: OnLoadMoreListener? = null

    private var currentPosition by Delegates.observable(0) { _: KProperty<*>, old: Int, new: Int ->
        adapter?.let {
            val tol = it.itemCount / (row * column)
            if (tol - new == 2)
            {
                onLoadMoreListener?.onLoadMore()
            }
        }
    }

    init
    {
        val a = context.obtainStyledAttributes(attrs, R.styleable.GridPageRecyclerView, defStyleAttr, 0)
        try
        {
            row = a.getInteger(R.styleable.GridPageRecyclerView_row, 1)
            column = a.getInteger(R.styleable.GridPageRecyclerView_column, 1)

            layoutManager = GridLayoutManager(context, row, HORIZONTAL, false)
            setHasFixedSize(true)
            GridPagerSnapHelper().apply {
                setRow(row)
                setColumn(column)
            }.attachToRecyclerView(this)
            addOnScrollListener(object : OnScrollListener()
            {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)
                {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager
                    if (layoutManager is GridLayoutManager)
                    {
                        var position = layoutManager.findFirstVisibleItemPosition()
                        val row = layoutManager.spanCount
                        position /= (row * column)
                        if (currentPosition != position)
                            currentPosition = position
                    }
                }
            })
        }
        finally
        {
            a.recycle()
        }
    }
}