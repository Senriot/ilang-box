package com.android.karaoke.common.models

import android.os.Parcelable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.android.parcel.Parcelize
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

@Parcelize
open class Record(
    @PrimaryKey
    var id: String = "",
    var type: Int = 1, //类别 1 朗读
    var readItem: ReadItem? = null,
    var file: String = "",
    var updated: Boolean = false,
    var createDate: Date = Date()
) : RealmObject(), Serializable, Parcelable


open class SongRecord(
    @PrimaryKey
    var id: String = "",
    var song: Song? = null,
    var openId: String = "",
    var filePath: String = "",
    var updated: Boolean = false,
    var playing: Boolean = false,
    var createDate: Date = Date()
) : RealmObject(), Serializable