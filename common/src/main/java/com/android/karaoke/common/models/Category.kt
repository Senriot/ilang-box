// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.PrimaryKey

open class Category : RealmObject()
{

    @PrimaryKey
    var id: String = ""
    var Name: String = ""
    var Items: RealmList<CategoryItem> = RealmList()

}
