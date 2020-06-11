package com.android.karaoke.common.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SystemParams(
    @PrimaryKey
    var id: String = "",
    var playlist: RealmList<Song> = RealmList(),
    var history: RealmList<Song> = RealmList(),
    var favorites: RealmList<Song> = RealmList(),
    var currentPlay: Song? = null
) : RealmObject()
{

}