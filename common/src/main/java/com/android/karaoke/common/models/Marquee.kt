package com.android.karaoke.common.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Marquee(
    @PrimaryKey
    var id: Int = 0,
    var content: String = ""
) : RealmObject()