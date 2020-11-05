package com.android.karaoke.common.api

data class Result<T>(
    var code: Int = 200,
    var message: String = "",
    var result: T? = null,
    var success: Boolean = true,
    var timestamp: Long? = null
)