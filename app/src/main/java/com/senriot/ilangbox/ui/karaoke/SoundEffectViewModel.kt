package com.senriot.ilangbox.ui.karaoke

import android.widget.SeekBar
import androidx.databinding.ObservableInt
import com.android.karaoke.common.preference.SPService
import com.android.karaoke.player.DspHelper
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.R

class SoundEffectViewModel : AbstractViewModel()
{

    val sp = SPService()

    val micVolume = ObservableInt(sp.micVolume)
    val headsetVolume = ObservableInt(sp.headsetVolume)
    val soundVolume = ObservableInt(sp.soundVolume)

    fun reset()
    {
        micVolume.set(34)
        headsetVolume.set(34)
        soundVolume.set(34)
        sp.micVolume = 34
        sp.headsetVolume = 34
        sp.soundVolume = 34
    }

    private fun changeVolume(value: Int, id: Int)
    {
        val sr = arrayListOf("48", "4D", "00", "06", "02", "00", value.toString(16))
        when (id)
        {
            R.id.headsetSeekBar ->
            {
                sr.add("03")
            }
            R.id.micSeekBar     ->
            {
                sr.add("02")
            }
            else                ->
            {
                sr.add("23")
            }
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
                    R.id.micSeekBar ->
                    {
                        changeVolume(progress, seekBar.id)
                        micVolume.set(progress)
                        sp.micVolume = progress
                    }
                    R.id.headsetSeekBar ->
                    {
                        changeVolume(progress, seekBar.id)
                        headsetVolume.set(progress)
                        sp.headsetVolume = progress
                    }
                    R.id.soundSeekBar ->
                    {
                        changeVolume(progress, seekBar.id)
                        soundVolume.set(progress)
                        sp.soundVolume = progress
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