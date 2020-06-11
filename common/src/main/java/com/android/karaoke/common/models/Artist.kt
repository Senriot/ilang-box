// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import com.android.karaoke.common.models.Song
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField

open class Artist : RealmObject() {

    @PrimaryKey
    var id: Long? = null
    var name: String? = null
    var alias: String? = null
    @RealmField("input_code")
    var inputCode: String? = null
    var areas: String? = null
    var category: Long? = null
    var photos: String? = null
    var birthplace: String? = null
    var birthday: String? = null
    var company: String? = null
    var constellation: String? = null
    var info: String? = null
    var popularity: Long? = null
    var status: String? = null
    var weight: String? = null
    var stature: String? = null

    @LinkingObjects("artists")
    val songs: RealmResults<Song>? = null

    var genre: String? = null
}
