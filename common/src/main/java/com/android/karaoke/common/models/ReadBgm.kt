// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ReadBgm : RealmObject() {

    @PrimaryKey
    var uuid: String = ""
    var name: String = ""
    var pic: String? = null
    var file: String = ""
    var artist: String? = null

}
