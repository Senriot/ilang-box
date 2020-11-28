package com.android.karaoke.common.api

data class UploadResult(
    var domain: String = "", // http://aogevod.com:9000
    var md5: String = "", // 35f00c59c01a26af6048e81ed271afd5
    var mtime: Int = 0, // 1606470293
    var path: String = "", // /group1/default/20201127/17/44/5/千年破晓-故梦 (琵琶版).mp3
    var retcode: Int = 0, // 0
    var retmsg: String = "",
    var scene: String = "", // default
    var scenes: String = "", // default
    var size: Int = 0, // 3630196
    var src: String = "", // /group1/default/20201127/17/44/5/千年破晓-故梦 (琵琶版).mp3
    var url: String = "" // http://aogevod.com:9000/group1/default/20201127/17/44/5/千年破晓-故梦 (琵琶版).mp3
)
