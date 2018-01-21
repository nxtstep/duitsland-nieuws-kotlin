package io.supersimple.duitslandnieuws.data.parcel

import android.os.Parcelable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Throws(IOException::class)
fun File.writeParcelable(item: Parcelable, provider: ParcelableProvider): Boolean {
    if (!exists()) {
        createNewFile()
    }
    val parcel = provider.obtain()
    try {
        // Wrap inside an array since `getParcelable()` won't recognize the parcelables ClassLoader (even when passed in as argument)
        parcel.writeArray(arrayOf(item))
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
inline fun <reified T : Parcelable> File.readParcelable(provider: ParcelableProvider): T? {
    if (exists()) {
        val reader = provider.obtain()
        try {
            val inputStream = FileInputStream(this)
            val array = ByteArray(inputStream.channel.size().toInt())
            inputStream.read(array, 0, array.size)
            inputStream.close()

            reader.unmarshall(array, 0, array.size)
            reader.setDataPosition(0)
            val list = mutableListOf<T>()
            reader.readList(list, T::class.java.classLoader)
            return if (list.size > 0) list[0] else null
        } finally {
            reader.recycle()
        }
    }
    return null
}
