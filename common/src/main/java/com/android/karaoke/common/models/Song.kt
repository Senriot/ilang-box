// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField

@RealmClass(name = "ilang_song")
open class Song : RealmObject()
{

//    @PrimaryKey
//    @RealmField(name = "uuid")
//    var id: String = ""
//    var code: String = ""
//    var name: String = ""
//    var pinyin: String? = null
//    var lang: String? = null
//    var wordCount: String? = null
//    var artist: String? = null
//    var type: String? = null
//    var bz: String? = null
//    var vol: String? = null
//    var playCount: Long? = null
//    var filePath: String? = null

    @PrimaryKey
    var id: String = ""
    var code: String = ""
    @Index
    var name: String = ""
    @Index
    var input_code: String? = null
    var word_count: Long? = null
    var lang_id: String? = null
    var type_id: String? = null
    var album_id: Long? = null
    @Index
    var singer_id: Long? = null
    var artists_name: String? = null
    var artist_ids: String? = null
    var is_mtv: Boolean? = null
    var am_track: String? = null
    var or_vol: Long? = null
    var ac_vol: Long? = null
    var volume: String? = null
    var order: Long? = null
    var fileName:String? = null
    var path: String? = null
    var exist: Boolean? = null
    var cloud: Long? = null
    var is_new: Boolean? = null
    var is_hd: Boolean? = null
    var is_score: Boolean? = null
    var month: Long? = null
    var ranking: Long? = null
    var status: String? = null
    @Index
    var hot: Long? = null
    var url: String? = null

}
