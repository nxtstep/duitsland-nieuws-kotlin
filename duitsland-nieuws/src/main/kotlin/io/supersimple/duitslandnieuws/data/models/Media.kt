package io.supersimple.duitslandnieuws.data.models

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel
import java.util.*

@PaperParcel
data class Media(val id: String,
                 val date: Date,
                 val title: RenderableText,
                 val author: String,
                 val slug: String,
                 val caption: RenderableText,
                 val media_details: MediaDetails) : Parcelable {
    companion object {
        val empty = Media("", Date(0), RenderableText.empty, "", "", RenderableText.empty, MediaDetails.empty)

        @JvmField val CREATOR = PaperParcelMedia.CREATOR
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelMedia.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0
}