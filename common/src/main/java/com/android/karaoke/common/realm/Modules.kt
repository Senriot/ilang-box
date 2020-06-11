package com.android.karaoke.common.realm

import com.android.karaoke.common.models.*
import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule
import java.io.File

@RealmModule(
    classes = [Song::class, Artist::class, Category::class, CategoryItem::class],
    allClasses = false
)
class MediaModule


@RealmModule(classes = [Song::class, Artist::class, SystemParams::class,Marquee::class])
class AppModule


val mediaConfig = RealmConfiguration.Builder().modules(MediaModule())
    .directory(File("/sdcard/AudioBank"))
    .name("media.db")
    .schemaVersion(11)
    .build()


val appConfig = RealmConfiguration.Builder().modules(AppModule())
    .directory(File("/sdcard/AudioBank"))
    .name("app.db")
    .schemaVersion(0)
    .build()