package cloud.seri.andorid.media.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Marquee(
    @PrimaryKey
    var id: Int = 0,
    var content: String = ""
) : RealmObject()