package io.supersimple.duitslandnieuws.data.parcel

import android.os.Parcel

interface ParcelableProvider {
    fun obtain(): Parcel

    companion object {
        val default = object : ParcelableProvider {
            override fun obtain(): Parcel = Parcel.obtain()
        }
    }
}