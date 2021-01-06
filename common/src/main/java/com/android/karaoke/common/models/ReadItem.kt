package com.android.karaoke.common.models;

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*


/**
 * @Description: 朗读曲目
 * @Author: Allen
 * @Date:   2020-06-04
 * @Version: V1.0
 */
@Parcelize
@RealmClass(name = "ilang_ld_item")
open class ReadItem : RealmObject(), Parcelable
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
    var bgMusic: ReadBgm? = null
    var lyric: String? = null
    var content: String? = null
    var read_count: Long? = null
    var pinyin: String? = null
    var create_time: Date? = null
    var update_time: Date? = null
}
