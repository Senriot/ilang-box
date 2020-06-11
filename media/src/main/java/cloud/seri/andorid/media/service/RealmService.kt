package cloud.seri.andorid.media.service

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery

abstract class RealmService
{

    fun realm(): Realm = Realm.getDefaultInstance()

    inline fun <reified T : RealmObject> query(): RealmQuery<T>
    {
        return Realm.getDefaultInstance().where(T::class.java)
    }
}