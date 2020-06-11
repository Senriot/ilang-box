// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package cloud.seri.andorid.media.model

import cloud.seri.andorid.media.model.Artist
import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField

open class Song : RealmObject()
{

    @PrimaryKey
    var id: String? = null
    var name: String? = null

    @RealmField("input_code")
    var inputCode: String? = null

    @RealmField("word_count")
    var wordCount: Long? = null
    var language: String? = null
    var category: String? = null
    var album: String? = null
    var singers: String? = null

    @RealmField("artist_names")
    var artistNames: String? = null

    @RealmField("ac_vol")
    var acVol: Long? = null

    @RealmField("or_vol")
    var orVol: Long? = null

    @RealmField("am_track")
    var amTrack: String? = null
    var quality: String? = null

    @RealmField("full_code")
    var fullCode: String? = null

    @RealmField("file_name")
    var fileName: String? = null

    @RealmField("file_path")
    var filePath: String? = null
    var status: String? = null
    var hot: Long? = null

    @RealmField("pic_url")
    var picUrl: String? = null
    var resolution: String? = null

    @RealmField("Artists")
    var artists: RealmList<Artist> = RealmList()

    var isCurrentPlay: Boolean? = false

    var favorite: Boolean? = false

    var edition: String? = null
}
