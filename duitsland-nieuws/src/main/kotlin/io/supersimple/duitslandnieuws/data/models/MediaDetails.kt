package io.supersimple.duitslandnieuws.data.models

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel
import java.util.*

@PaperParcel
data class MediaDetails(val width: Int,
                        val height: Int,
                        val file: String,
                        val sizes: Map<String, MediaItem>) : Parcelable {
    companion object {
        val empty = MediaDetails(-1, -1, "", HashMap())

        @JvmField val CREATOR = PaperParcelMediaDetails.CREATOR
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelMediaDetails.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0
}
