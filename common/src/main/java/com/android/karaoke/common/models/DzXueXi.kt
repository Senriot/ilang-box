// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class DzXueXi : RealmObject(), Serializable
{

    @PrimaryKey
    var uuid: String = ""
    var name: String = ""
    var pic: String? = null
    var audioFile: String = ""
    var contentFile: String = ""
    var duration: String? = null
    var category: Dict? = null

}
