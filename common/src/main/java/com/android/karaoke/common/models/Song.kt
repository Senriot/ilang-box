// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass(name = "RealmSong")
open class Song : RealmObject() {

    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var inputCode: String? = null
    var wordCount: Long = 0
    var singerNames: String? = null
    var typeId: Long? = null
    var langId: Long? = null
    var albumId: Long? = null
    var accompanyTrack: Long = 0
    var quality: String? = null
    var fullCode: String? = null
    var fileName: String? = null
    var filePath: String? = null
    var status: Long? = null
    var hot: Long? = null
    var isNew: Boolean? = null
    var isRecommend: Boolean? = null
    var isPublic: Boolean? = null
    var resolution: String? = null
    var isHd: Boolean? = null
    var image: String? = null
    var imageUrl: String? = null
    var playUrl: String? = null
    var lang: Dict? = null
    var album: Album? = null
    var type: Dict? = null
    var singers: RealmList<Artist> = RealmList()

}
