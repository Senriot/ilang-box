// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

@RealmClass(name = "RealmSinger")
open class Artist : RealmObject(), Serializable {

    @PrimaryKey
    var id: Long = 0
    var name: String = ""
    var inputCode: String? = null
    var areaId: Long? = null
    var gender: Long? = null
    var avatar: String? = null
    var birthplace: String? = null
    var birthday: String? = null
    var company: String? = null
    var constellation: String? = null
    var info: String? = null
    var hot: Long? = null
    var status: Long? = null
    var weight: String? = null
    var stature: String? = null
    var smallPicUrl: String? = null
    var bigPicUrl: String? = null
    var area: Dict? = null
    var songs: RealmList<Song> = RealmList()

}
