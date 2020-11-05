package com.android.karaoke.common.models;

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable


/**
 * @Description: 朗读曲目
 * @Author: Allen
 * @Date:   2020-06-04
 * @Version: V1.0
 */
@RealmClass(name = "ilang_ld_item")
open class ReadItem : RealmObject(), Serializable
{

    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var author: String? = null
    var category_id: String? = null
    var recommends: String? = null
    var status: String? = null
    var cover: String? = null
    var bg_pic: String? = null
    var bg_music: String? = null
    var lyric: String? = null
    var lyric_path: String? = null
    var lyric_filename: String? = null
    var content: String? = null
    var read_count: Long? = null
    var pinyin: String? = null

}
