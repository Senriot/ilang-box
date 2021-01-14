package com.senriot.ilangbox.ui.langdu

import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.android.karaoke.common.preference.SPService
import com.android.karaoke.player.DspHelper
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.R

class LangDuViewModel : AbstractViewModel()
{
    val sp = SPService()

    val micVolume = ObservableInt(sp.micVolume)
    val soundVolume = ObservableInt(sp.headsetVolume)


    fun sendEffectValue(value: Int, type: String)
    {
        val sr = arrayListOf("48", "4D", "00", "06", "02", "00", value.toString(16))
        sr.add(type)
        var sum = 0xff
        sr.forEach {
            sum = sum xor Integer.parseInt(it, 16)
        }
        sr.add(Integer.toHexString(sum))
        sr.add("AA")
        LogUtils.e(sr.joinToString(""))
        DspHelper.sendHex(sr.joinToString(""))
    }
}
