package io.supersimple.duitslandnieuws.data.models

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel

@PaperParcel
data class MediaItem(val file: String,
                     val width: Int,
                     val height: Int,
                     val mime_type: String,
                     val source_url: String) : Parcelable {
    companion object {
        val empty = MediaItem("", -1, -1, "", "")

        @JvmField val CREATOR = PaperParcelMediaItem.CREATOR

        const val IMAGE_THUMBNAIL = "medium"
        const val IMAGE_FULL = "full"
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelMediaItem.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0
}
