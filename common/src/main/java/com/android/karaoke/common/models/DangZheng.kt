// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

open class DangZheng : RealmObject() {

    @PrimaryKey
    var uuid: String = ""
    var code: String = ""
    var name: String? = null
    var pinyin: String? = null
    var lang: String = ""
    var wordCount: Long? = null
    var artist: String? = null
    var isMtv: Boolean? = null
    var type: String? = null
    var bz: String? = null
    var vol: String? = null

}
