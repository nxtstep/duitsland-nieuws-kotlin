package io.supersimple.duitslandnieuws.data.models

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel

@PaperParcel
data class RenderableText(
        val rendered: String,
        val protected: Boolean = false) : Parcelable {
    companion object {
        val empty = RenderableText("", false)

        @JvmField val CREATOR = PaperParcelRenderableText.CREATOR
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelRenderableText.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0
}