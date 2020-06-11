package cloud.seri.andorid.media.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SystemParams(
    @PrimaryKey
    var id: String = "",
    var playlist: RealmList<Song> = RealmList(),
    var history: RealmList<Song> = RealmList(),
    var currentPlay: Song? = null
) : RealmObject()
{

}