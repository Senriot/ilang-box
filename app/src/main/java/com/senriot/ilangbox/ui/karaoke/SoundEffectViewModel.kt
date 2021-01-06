package com.senriot.ilangbox.ui.karaoke

import android.widget.SeekBar
import androidx.databinding.ObservableInt
import com.android.karaoke.player.DspHelper
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.App
import com.senriot.ilangbox.R

class SoundEffectViewModel : AbstractViewModel()
{
    val micVolume = ObservableInt(App.micVolume)
    val headsetVolume = ObservableInt(App.headsetVolume)
    val soundVolume = ObservableInt(App.soundVolume)

    fun reset()
    {
        micVolume.set(34)
        headsetVolume.set(34)
        soundVolume.set(34)
        App.micVolume = 34
        App.headsetVolume = 34
        App.soundVolume = 34
    }

    private fun changeVolume(value: Int, id: Int)
    {
        val sr = arrayListOf("48", "4D", "00", "06", "02", "00", value.toString(16))
        if (id == R.id.headsetSeekBar)
        {
            sr.add("03")
        }
        else
        {
            sr.add("02")
        }
        var sum = 0xff
        sr.forEach {
            sum = sum xor Integer.parseInt(it, 16)
        }
        sr.add(Integer.toHexString(sum))
        sr.add("AA")
        LogUtils.e(sr.joinToString(""))
        DspHelper.sendHex(sr.joinToString(""))
    }

    val volumeChangedListener = object : SeekBar.OnSeekBarChangeListener
    {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
        {
            if (fromUser)
            {
                when (seekBar.id)
                {
                    R.id.micSeekBar     ->
                    {
                        changeVolume(progress, seekBar.id)
                        micVolume.set(progress)
                        App.micVolume = progress
                    }
                    R.id.headsetSeekBar ->
                    {
                        changeVolume(progress, seekBar.id)
                        headsetVolume.set(progress)
                        App.headsetVolume = progress
                    }
                    R.id.soundSeekBar   ->
                    {
                        soundVolume.set(progress)
                        App.soundVolume = progress
                    }
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?)
        {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?)
        {
        }

    }
}