package com.senriot.ilangbox.ui.welcome

import androidx.databinding.ObservableField
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.App

class ProfileViewModel : AbstractViewModel()
{
    val user = App.wxUser
    val userName = ObservableField("")
    val avatar = ObservableField("")
}