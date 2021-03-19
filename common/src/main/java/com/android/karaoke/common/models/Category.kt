// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize

@RealmClass(name = "sys_category")
@Parcelize
open class Category : RealmObject(), Parcelable
{

    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var sort_no: Long? = null
    var remark: String? = null
    var pid: String? = null
    var has_child: Boolean? = null
    var code: String? = null

}
