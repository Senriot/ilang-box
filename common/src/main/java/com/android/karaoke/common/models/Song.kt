// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass(name = "ilang_song")
open class Song : RealmObject()
{
    @PrimaryKey
    var inded: Int = 0

    var id: String = ""
    var code: String = ""

    @Index
    var name: String = ""

    @Index
    var input_code: String? = null
    var word_count: Long? = null
    var lang_id: String? = null
    var type_id: String? = null
    var artists_name: String? = null
    var artist_ids: String? = null
    var artists: RealmList<Artist> = RealmList()
    var am_track: String? = null
    var or_vol: Long? = null
    var ac_vol: Long? = null
    var file_name: String? = null
    var file_path: String? = null
    var exist: Boolean? = null
    var status: String? = null

    @Index
    var hot: Long? = null
    var cloud_url: String? = null

    var create_time: Date? = null
    var update_time: Date? = null
}
