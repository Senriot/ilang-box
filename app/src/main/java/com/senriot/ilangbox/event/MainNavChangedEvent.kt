package com.senriot.ilangbox.event

import com.arthurivanets.mvvm.events.Command

class MainNavChangedEvent(val id: Int)
{
}


class ShowReadListEvent()

class SearchTextChangedEvent(val text: String)


class StartMainActEvent : Command<String>()