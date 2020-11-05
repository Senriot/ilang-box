// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

@RealmClass(name = "ilang_dz_xiexi")
open class DzXueXi : RealmObject(), Serializable
{

    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var pinyin: String? = null
    var type: String? = null
    var pic: String? = null
    var audio_file: String = ""
    var content_file: String = ""
    var read_count: Long? = null
    var duration: String? = null
    var status: String? = null
    var bg_pic: String? = null
    var remark: String? = null

}
