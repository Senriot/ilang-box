// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField

@RealmClass(name = "ok_song")
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

    @Index
    var name: String = ""

    @Index
    var input_code: String? = null
    var lang_id: Long? = null
    var length: Long? = null

    @Index
    var singer_id: Long? = null

    @RealmField(name = "singer_names")
    var artist: String? = null
    var is_mtv: Boolean = false
    var type_id: Long? = null
    var track: String? = null
    var volume: String? = null
    var order: Long? = null
    var path: String? = null
    var server_name: String? = null
    var file_name: String? = null
    var exist: Boolean = false
    var cloud: Long? = null
    var is_new: Boolean = false
    var is_hd: Boolean = false
    var is_score: Boolean = false
    var month: Long? = null
    var ranking: Long? = null
    var status: Long? = null

    @Index
    var hot: Long? = null

}
