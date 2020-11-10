package com.senriot.ilangbox.ui.weiget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.senriot.ilangbox.R

class DownloadButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)
{
    init
    {
        inflate(context, R.layout.download_button, this)
    }
}