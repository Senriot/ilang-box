/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.android.karaoke.serialport

import java.io.FileReader
import java.io.IOException
import java.io.LineNumberReader
import java.util.*


class SerialPortFinder
{

    fun getAllDevices(): Array<String>
    {
        val devices = Vector<String>()
        val itdriv: Iterator<Driver>
        try
        {
            itdriv = getDrivers().iterator()
            while (itdriv.hasNext())
            {
                val driver = itdriv.next()
                val itdev = driver.getDevices()!!.iterator()
                while (itdev.hasNext())
                {
                    val device = itdev.next().name
                    val value = String.format("%s (%s)", device, driver.name)
                    devices.add(value)
                }
            }
        } catch (e: IOException)
        {
            e.printStackTrace()
        }

        return devices.toArray(arrayOfNulls(devices.size))
    }

    fun getAllDevicesPath(): Array<String>
    {
        val devices = Vector<String>()
        // Parse each driver
        val itdriv: Iterator<Driver>
        try
        {
            itdriv = getDrivers().iterator()
            while (itdriv.hasNext())
            {
                val driver = itdriv.next()
                val itdev = driver.getDevices()!!.iterator()
                while (itdev.hasNext())
                {
                    val device = itdev.next().absolutePath
                    devices.add(device)
                }
            }
        } catch (e: IOException)
        {
            e.printStackTrace()
        }

        return devices.toArray(arrayOfNulls(devices.size))
    }

    private var mDrivers: Vector<Driver>? = null

    @Throws(IOException::class)
    fun getDrivers(): Vector<Driver>
    {
        if (mDrivers == null)
        {
            mDrivers = Vector()
            val r = LineNumberReader(FileReader("/proc/tty/drivers"))
            var l: String
            while (true)
            {
                l = r.readLine() ?: break
                val driverName = l.substring(0, 0x15).trim { it <= ' ' }
                val w = l.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (w.size >= 5 && w[w.size - 1] == "serial")
                {
                    mDrivers?.add(Driver(driverName, w[w.size - 4]))
                }
            }
            r.close()
        }
        return mDrivers!!
    }
}
