package com.android.karaoke.serialport

import android.util.Log
import com.apkfuns.logutils.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.*
import kotlin.experimental.xor


class SerialPort(val device: File, val baudrate: Int, val flags: Int)
{
    external fun open(path: String, baudrate: Int, flags: Int): FileDescriptor
    external fun close()


    private val TAG = "SerialPort"


    /*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
    private var mFd: FileDescriptor? = null
    var mFileInputStream: FileInputStream? = null
    var mFileOutputStream: FileOutputStream? = null

    init
    {

        System.loadLibrary("serialport")

        /* Check access permission */
        if (!device.canRead() || !device.canWrite())
        {
            try
            {
                /* Missing read/write permission, trying to chmod the file */
                val su: Process = Runtime.getRuntime().exec("/system/bin/su")
                val cmd = ("chmod 666 " + device.absolutePath + "\n"
                        + "exit\n")
                su.outputStream.write(cmd.toByteArray())
                if (su.waitFor() != 0 || !device.canRead()
                    || !device.canWrite()
                )
                {
                    throw SecurityException()
                }
            } catch (e: Exception)
            {
                e.printStackTrace()
                throw SecurityException()
            }

        }
        mFd = open(device.absolutePath, baudrate, flags)
        if (mFd == null)
        {
            Log.e(TAG, "native open returns null")
            throw IOException()
        }
        mFileInputStream = FileInputStream(mFd)
        mFileOutputStream = FileOutputStream(mFd)
    }

    fun readData(): Observable<ByteArray>
    {
        return Observable.create<ByteArray> {
            while (!it.isDisposed)
            {
                val buffer = ByteArray(mFileInputStream?.available() ?: 0)
                val size = mFileInputStream?.read(buffer) ?: 0
                if (size > 0)
                    it.onNext(buffer)
                Thread.sleep(100)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun sendData(buff: ByteArray)
    {
        mFileOutputStream?.write(buff)
    }

    fun sendString(string: String)
    {
        mFileOutputStream?.write(string.toByteArray())
    }

    fun sendControl(a: String, b: String): String
    {
        val sr = arrayListOf("04", "09", a, b)
        var chk = 0xff
        sr.forEach {
            chk = chk xor Integer.parseInt(it, 16)
        }
        sr.add(0, "55")
        sr.add(Integer.toHexString(chk))
        val result = sr.joinToString()
        sendData(result.toByteArray())
        return result
    }

}
