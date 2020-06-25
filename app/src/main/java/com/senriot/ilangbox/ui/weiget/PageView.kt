package com.senriot.ilangbox.ui.weiget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.senriot.ilangbox.R
import kotlinx.android.synthetic.main.page_view.view.*

class PageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(
                context,
                attrs,
                defStyleAttr
        )
{
    var count: Int = 0
        get() = field
        set(value)
        {
            field = value
            pageCount.text = "$field"
        }
    var current = 0
        get() = field
        set(value)
        {
            field = value
            currentPage.text = "$field"
        }

    var listener: PageViewListener? = null

    init
    {
        View.inflate(context, R.layout.page_view, this)

        btnPrevious.setOnClickListener {
            listener?.onPrevious()
        }

        btnNext.setOnClickListener {
            listener?.onNext()
        }

        btnBack.setOnClickListener {
            listener?.onBack()
        }
    }

    interface PageViewListener
    {
        fun onPrevious()
        fun onNext()
        fun onBack()
    }

}