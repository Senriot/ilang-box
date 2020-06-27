// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

@RealmClass(name = "ok_singer")
open class Artist : RealmObject(), Serializable {

    @PrimaryKey
    var id: Long = 0
    @Index
    var name: String = ""
    @Index
    var input_code: String? = null
    var fame: Long? = null
    var sex: String? = null
    var nationality: String? = null
    var type_id: Long? = null
    var avatar: String? = null
    var length: Long? = null
    var visible: Long? = null
    var birthplace: String? = null
    var birthday: String? = null
    var company: String? = null
    var constellation: String? = null
    var info: String? = null
    @Index
    var hot: Long? = null
    var weight: String? = null
    var stature: String? = null
    var status: Long? = null

}
