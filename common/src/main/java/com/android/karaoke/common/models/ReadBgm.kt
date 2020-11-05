// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass(name = "ilang_ld_bgm")
open class ReadBgm : RealmObject()
{

    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var pinyin: String? = null
    var url: String? = null
    var pics: String? = null
    var file_name: String? = null
    var artist: String? = null
    var filePath: String? = null
}
