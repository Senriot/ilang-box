package com.senriot.ilangbox.ui.welcome

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.arthurivanets.mvvm.AbstractViewModel

class InitViewModel : AbstractViewModel()
{
    val title = ObservableField("系统正在初始化...")
    val progress = ObservableInt()

    fun getUpdateInfo()
    {
    }
}