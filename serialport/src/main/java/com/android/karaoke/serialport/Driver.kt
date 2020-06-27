package com.android.karaoke.serialport

import android.util.Log
import java.io.File
import java.util.*

class Driver(val name: String, val root: String) {

    private var mDevices: Vector<File>? = null

    private val TAG = "SerialPort"

    fun getDevices(): Vector<File>? {
        if (mDevices==null)
        {
            mDevices = Vector()
            val dev = File("/dev")
            val files = dev.listFiles()
            files.forEachIndexed { index, file ->
                if (file.absolutePath.startsWith(root))
                {
                    Log.d(TAG, "Found new device: $file")
                    mDevices?.add(file)
                }
            }
        }
        return mDevices
    }
}