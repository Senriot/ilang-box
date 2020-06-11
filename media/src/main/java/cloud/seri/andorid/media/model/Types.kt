// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package cloud.seri.andorid.media.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass(name = "ok_dict")
open class Types : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var code: String = ""
    var key: String = ""
    var value: String? = null
    var sort: String? = null
    var remark: String? = null
    var pid: Long? = null
}
