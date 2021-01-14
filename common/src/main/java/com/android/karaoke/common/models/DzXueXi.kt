// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField
import kotlinx.android.parcel.Parcelize
import java.util.*

@RealmClass(name = "ilang_dz_xiexi")
@Parcelize
open class DzXueXi : RealmObject(), Parcelable
{

    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var pinyin: String? = null
    var type: String? = null
    var pic: String? = null
    var content_file: String? = null
    var subtitle: String? = null
    var read_count: Long? = null
    var duration: String? = null
    var status: String? = null
    var bg_pic: String? = null

    var remark: String? = null

    @RealmField(name = "audio_path")
    var audioPath: String? = null

    @RealmField(name = "audio_file_name")
    var audioFileName: String? = null

    @RealmField(name = "audio_url")
    var audioUrl: String? = null

    var exist: Boolean? = null

    var create_time: Date? = null
    var update_time: Date? = null
}
