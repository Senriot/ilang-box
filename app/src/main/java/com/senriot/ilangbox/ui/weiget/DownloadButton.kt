package com.senriot.ilangbox.ui.weiget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import com.brkckr.circularprogressbar.CircularProgressBar
import com.senriot.ilangbox.R
import kotlin.properties.Delegates

class DownloadButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)
{
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var icon: ImageView

    var progress by Delegates.observable(0f, { _, oldValue, newValue ->
        circularProgressBar.progressValue = newValue
    })

    var status by Delegates.observable(-1, { _, oldValue, newValue ->
        when (newValue)
        {
            1    ->
            {
                icon.setImageResource(R.drawable.icon_pause)
            }
            2    ->
            {
                icon.setImageResource(R.drawable.ic_resume)
            }
            3    ->
            {
                icon.setImageResource(R.drawable.ic_retry)
            }
            else ->
            {
                icon.setImageResource(R.drawable.ic_cloud_download)
            }

        }
    })

    init
    {
        inflate(context, R.layout.download_button, this)
        circularProgressBar = findViewById(R.id.progressBar)
        icon = findViewById(R.id.icon)
        val a = context.obtainStyledAttributes(attrs, R.styleable.DownloadButton, defStyleAttr, 0)
        this.progress = a.getFloat(R.styleable.DownloadButton_dbProgress, 0f)
    }


}