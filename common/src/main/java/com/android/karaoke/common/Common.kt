package com.android.karaoke.common

fun getDeviceSN(): String
{
    var serial: String = ""
    try
    {
        val c = Class.forName("android.os.SystemProperties")
        val get = c.getMethod("get", String::class.java)
        serial = get.invoke(c, "ro.boot.serialno") as String
    } catch (e: Exception)
    {
        e.printStackTrace()
    }
    return serial
}