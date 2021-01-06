package com.android.karaoke.common.realm

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import com.android.karaoke.common.models.*
import com.android.karaoke.common.preference.SPService
import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule
import java.io.File

//@RealmModule(
//        classes = [Song::class, Artist::class,Dict::class],
//        allClasses = false
//)
//class MediaModule


@RealmModule(classes = [UserData::class, Record::class, Song::class, Artist::class, ReadItem::class, ReadBgm::class, SongRecord::class])
class UserModule


@RealmModule(classes = [Song::class, Artist::class, DzXueXi::class, Category::class, ReadBgm::class, ReadItem::class])
class SongsModule

//@RealmModule(classes = [ReadCategory::class, ReadItem::class, ReadBgm::class])
//class LdModule


//@SuppressLint("SdCardPath")
//val mediaConfig: RealmConfiguration = RealmConfiguration.Builder().modules(MediaModule())
//        .directory(File("/sdcard/ilang-box"))
//        .name("media.realm")
//        .schemaVersion(0)
//        .build()


//val snappyClient =
//    DBFactory.open(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm")

//val snappyClient by lazy {
//    DBFactory.open(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm")
//}

//val mapDB by lazy {
//    DBMaker.fileDB(File(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm/params.db"))
//        .fileMmapEnable().make()
//}
//
//val sysParams by lazy {
////    val db =
////        DBMaker.fileDB(File(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm/params.db"))
////            .fileMmapEnable().make()
//    mapDB.hashMap("params", Serializer.STRING, Serializer.STRING).createOrOpen()
//}

//fun snappyClient(context: Context): DB
//{
//    return SnappyDB.Builder(context)
//        .directory(Environment.getExternalStorageDirectory().absolutePath)
//        .name("ilang")
//        .build()
//}


@SuppressLint("SdCardPath")
val userConfig: RealmConfiguration = RealmConfiguration.Builder().modules(UserModule())
//    .directory(File("/sdcard/ilang-box/realm"))
    .name("user.db")
    .allowWritesOnUiThread(true)
    .allowQueriesOnUiThread(true)
    .inMemory()
    .build()

//val songsConfig: RealmConfiguration = RealmConfiguration.Builder().modules(SongsModule())
//    .directory(File(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm"))
//    .allowWritesOnUiThread(true)
//    .allowQueriesOnUiThread(true)
//    .encryptionKey("NjjmeL3YRYgg5ChMhDkj1jOiToFx47X8OSqfJ9BawBamdhIE1ktlcuOdL0Ip56UJ".toByteArray())
//    .name("db.realm")
//    .build()

private var sconf: RealmConfiguration? = null


val songsConfig by lazy {
    val dbVer = SPService().dbVer ?: "0.0.1"
    RealmConfiguration.Builder().modules(SongsModule())
        .directory(File(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm/" + dbVer))
        .allowWritesOnUiThread(true)
        .allowQueriesOnUiThread(true)
//        .encryptionKey("NjjmeL3YRYgg5ChMhDkj1jOiToFx47X8OSqfJ9BawBamdhIE1ktlcuOdL0Ip56UJ".toByteArray())
        .name("db.realm")
        .build()
}

//fun songsConfig(): RealmConfiguration
//{
//    try
//    {
//        if (sconf == null)
//        {
//            val dbVer = sysParams["dbVer"]
//            sconf = RealmConfiguration.Builder().modules(SongsModule())
//                .directory(File(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm/" + dbVer))
//                .allowWritesOnUiThread(true)
//                .allowQueriesOnUiThread(true)
//                //.encryptionKey("NjjmeL3YRYgg5ChMhDkj1jOiToFx47X8OSqfJ9BawBamdhIE1ktlcuOdL0Ip56UJ".toByteArray())
//                .name("db.realm")
//                .build()
//            sysParams.close()
//        }
//    }
//    catch (e: Exception)
//    {
//        sconf = RealmConfiguration.Builder().modules(SongsModule())
//            .directory(File(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box/realm/" + "temp"))
//            .allowWritesOnUiThread(true)
//            .allowQueriesOnUiThread(true)
//            .encryptionKey("NjjmeL3YRYgg5ChMhDkj1jOiToFx47X8OSqfJ9BawBamdhIE1ktlcuOdL0Ip56UJ".toByteArray())
//            .name("db.realm")
//            .build()
//    }
//    return sconf!!
//}

//@SuppressLint("SdCardPath")
//val ldConfig: RealmConfiguration = RealmConfiguration.Builder().modules(LdModule())
//        .directory(File("/sdcard/ilang-box"))
//        .name("ld.db")
//        .schemaVersion(6)
//        .build()