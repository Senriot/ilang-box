package com.android.karaoke.common.realm

import android.annotation.SuppressLint
import com.android.karaoke.common.models.*
import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule
import java.io.File

//@RealmModule(
//        classes = [Song::class, Artist::class,Dict::class],
//        allClasses = false
//)
//class MediaModule


@RealmModule(classes = [Song::class, Artist::class, UserData::class, DangZheng::class, Dict::class, Album::class, ReadItem::class, ReadBgm::class, ReadCategory::class, Record::class])
class UserModule

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
    .name("user.realm")
    .schemaVersion(11)
    .build()


//@SuppressLint("SdCardPath")
//val ldConfig: RealmConfiguration = RealmConfiguration.Builder().modules(LdModule())
//        .directory(File("/sdcard/ilang-box"))
//        .name("ld.db")
//        .schemaVersion(6)
//        .build()