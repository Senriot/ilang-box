package com.android.karaoke.common.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.io.Serializable
import java.util.*

open class UserData(
    @PrimaryKey
    var id: String = "",
    var playlist: RealmList<Song> = RealmList(),
    var history: RealmList<Song> = RealmList(),
    var favorites: RealmList<Song> = RealmList(),
    var records: RealmList<Record> = RealmList(),
    var currentPlay: Song? = null
) : RealmObject()
{

}

open class Record(
    @PrimaryKey
    var id: String = "",
    var type: Int = 1, //类别 1 朗读
    var readItem: ReadItem? = null,
    var file: String = "",
    var createDate: Date = Date()
) : RealmObject(), Serializable