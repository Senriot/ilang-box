package com.android.karaoke.common.realm

import android.annotation.SuppressLint
import android.os.Environment
import com.android.karaoke.common.models.*
import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule
import java.io.File

//@RealmModule(
//        classes = [Song::class, Artist::class,Dict::class],
//        allClasses = false
//)
//class MediaModule


@RealmModule(classes = [UserData::class, Record::class, Song::class, Artist::class, ReadItem::class, ReadBgm::class])
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


@SuppressLint("SdCardPath")
val userConfig: RealmConfiguration = RealmConfiguration.Builder().modules(UserModule())
    .directory(File("/sdcard/ilang-box"))
    .name("user.db")
    .allowWritesOnUiThread(true)
    .allowQueriesOnUiThread(true)
    .build()

val songsConfig: RealmConfiguration = RealmConfiguration.Builder().modules(SongsModule())
    .directory(File(Environment.getExternalStorageDirectory().absolutePath + "/ilang-box"))
    .allowWritesOnUiThread(true)
    .allowQueriesOnUiThread(true)
    .name("db.realm")
    .build()
//@SuppressLint("SdCardPath")
//val ldConfig: RealmConfiguration = RealmConfiguration.Builder().modules(LdModule())
//        .directory(File("/sdcard/ilang-box"))
//        .name("ld.db")
//        .schemaVersion(6)
//        .build()