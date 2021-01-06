package com.android.karaoke.common.preference

class SPService
{
    var dbVer: String? by Preference.string("DB_VER", "0.0.1")

    var dbFilePath: String? by Preference.string("DB_FILE_PATH")

    var userFirst: Boolean by Preference.boolean("FIRST", true)
}