package com.senriot.ilangbox

import androidx.databinding.ObservableField
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.AbstractViewModel
import com.yanzhenjie.kalle.Kalle
import com.yanzhenjie.kalle.download.SimpleCallback

class InitActViewModel : AbstractViewModel()
{
    var downloadProgress: ObservableField<Int> = ObservableField(0)

    override fun onStart()
    {
        super.onStart()
    }
}