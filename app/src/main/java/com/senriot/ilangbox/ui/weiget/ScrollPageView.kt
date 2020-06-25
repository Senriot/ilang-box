package com.senriot.ilangbox.ui.weiget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.senriot.ilangbox.MainActivity
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.RealmAdapter

class ScrollPageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) :
        FrameLayout(
                context,
                attrs,
                defStyleAttr
        ), PageView.PageViewListener
{
    override fun onPrevious()
    {
        val position = layoutManager?.findFirstVisibleItemPosition() ?: 0
        recyclerView.smoothScrollToPosition(position - (column * row))
    }

    override fun onNext()
    {
        val position = layoutManager?.findLastVisibleItemPosition() ?: 0
        recyclerView.smoothScrollToPosition(position + (column * row - 2))
    }

    override fun onBack()
    {
//        (context as MainActivity).supportFragmentManager.popBackStack()
        this.findNavController().popBackStack()
    }


    var column: Int = 0
    var row: Int = 0
    var pageCount: Int = 0
        set(value)
        {
            field = value
            pageView.count = value
        }
    var pageIndex: Int = 1
        set(value)
        {
            field = value
            pageView.current = value
        }
//    private var isSinger: Boolean = false

    private var layoutManager: GridLayoutManager? = null

    private var adapter: RealmAdapter<*>? = null

    private val recyclerView: RecyclerView
    private val pageView: PageView


    fun setAdapter(adapter: RealmAdapter<*>)
    {
        this.adapter = adapter
        row = adapter.row
        column = adapter.column
        layoutManager = GridLayoutManager(context, adapter.row, RecyclerView.HORIZONTAL, false)
        GridPagerSnapHelper().apply {
            setRow(adapter.row)
            setColumn(adapter.column)
        }.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        pageCount = adapter.tolPage
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver()
        {
            override fun onChanged()
            {
                super.onChanged()
                pageCount = adapter.tolPage
            }
        })
    }

    init
    {
        View.inflate(context, R.layout.scroll_page, this)
        recyclerView = findViewById(R.id.list)
        pageView = findViewById(R.id.pageView)
        recyclerView.setHasFixedSize(true)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)
            {
                super.onScrollStateChanged(recyclerView, newState)
                var position = layoutManager?.findFirstVisibleItemPosition() ?: 0
                position /= (row * column)
//                if (currentPosition != position)
//                    currentPosition = position
                pageIndex = position + 1
            }
        })
        pageView.listener = this
    }
}