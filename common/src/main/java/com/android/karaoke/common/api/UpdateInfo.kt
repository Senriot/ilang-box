package com.android.karaoke.common.api

data class UpdateInfo(
    var dbVer: String = "",
    var downloadUrl: String="",
    var appVer:String="",
    var appUrl:String=""
)