package com.senriot.ilangbox.ui.karaoke

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.android.karaoke.player.events.MinorDisplayInit
import com.senriot.ilangbox.R
import kotlinx.android.synthetic.main.minor_display_fragment.*
import org.greenrobot.eventbus.EventBus

class MinorDisplayFragment : DialogFragment()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setStyle(0, R.style.AppTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.minor_display_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        minorDisplay.setOnClickListener { this.dismiss() }
        minorDisplay.holder.addCallback(object : SurfaceHolder.Callback
        {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            )
            {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?)
            {
                EventBus.getDefault().post(MinorDisplayInit(null))
            }

            override fun surfaceCreated(holder: SurfaceHolder?)
            {
                EventBus.getDefault().post(MinorDisplayInit(holder))
            }
        })
    }
}