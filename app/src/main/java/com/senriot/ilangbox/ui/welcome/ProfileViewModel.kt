package com.senriot.ilangbox.ui.welcome

import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.App

class ProfileViewModel : AbstractViewModel()
{
    val user = App.wxUser
}