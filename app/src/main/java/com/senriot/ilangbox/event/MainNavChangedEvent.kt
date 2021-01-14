package com.senriot.ilangbox.event

import com.android.karaoke.common.api.Auth

class MainNavChangedEvent(val id: Int)
{
}


class ShowReadListEvent()

class SearchTextChangedEvent(val text: String)


//class StartMainActEvent : Command<String>()

class LoginEvent(val auth: Auth)