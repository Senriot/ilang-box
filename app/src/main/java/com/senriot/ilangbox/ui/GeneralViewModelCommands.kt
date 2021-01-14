package com.senriot.ilangbox.ui

import com.android.karaoke.common.models.Artist
import com.android.karaoke.common.models.ReadItem
import com.arthurivanets.mvvm.events.Command

sealed class GeneralViewModelCommands<T>(payload: T? = null) : Command<T>(payload)
{
    class showLangDuDetail(item: ReadItem) : GeneralViewModelCommands<ReadItem>(item)

    class showArtistSongs(item: Artist) : GeneralViewModelCommands<Artist>(item)
}