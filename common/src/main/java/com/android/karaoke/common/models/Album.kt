// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass(name = "RealmAlbum")
open class Album : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var name: String = ""
    var company: String? = null
    var image: String? = null
    var info: String? = null

}
