package cloud.seri.andorid.media.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class SelectList : RealmObject()
{
    @PrimaryKey
    @Required
    var id: String = ""

    var song: Song? = null

    @Required
    var updateTime: Date? = null
}