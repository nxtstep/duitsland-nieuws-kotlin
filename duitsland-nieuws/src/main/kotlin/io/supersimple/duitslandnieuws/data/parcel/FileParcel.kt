package io.supersimple.duitslandnieuws.data.parcel

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Throws(IOException::class)
fun File.writeParcelable(item: Parcelable): Boolean {
    if (!exists()) {
        createNewFile()
    }
    val parcel = Parcel.obtain()
    try {
        val bundle = Bundle()
        bundle.putParcelable(PARCEL_KEY, item)
        bundle.writeToParcel(parcel, 0)
        val outputStream = FileOutputStream(this, false)
        outputStream.write(parcel.marshall())
        outputStream.flush()
        outputStream.close()
    } finally {
        parcel.recycle()
    }
    return true
}

@Throws(IOException::class)
inline fun <reified T : Parcelable> File.readParcelable(): T {
    if (exists()) {
        val reader = Parcel.obtain()
        try {
            val inputStream = FileInputStream(this)
            val array = ByteArray(inputStream.channel.size().toInt())
            inputStream.read(array, 0, array.size)
            inputStream.close()

            reader.unmarshall(array, 0, array.size)
            reader.setDataPosition(0)
            val bundle = reader.readBundle(T::class.java.classLoader)
            val item: T = bundle.getParcelable(PARCEL_KEY)
            return item
        } finally {
            reader.recycle()
        }
    }
    throw IOException("File $this does not exists")
}

const val PARCEL_KEY = "parcel"