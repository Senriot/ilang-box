package com.android.karaoke.common.events

import com.android.karaoke.common.models.Song
import com.android.karaoke.common.models.UserData

class FavoritesChangedEvent

class PlaylistChangedEvent(val list: List<Song>)

class ProfileDataInitEvent(val data: UserData)