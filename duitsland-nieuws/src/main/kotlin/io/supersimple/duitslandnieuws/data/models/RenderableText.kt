package io.supersimple.duitslandnieuws.data.models

import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import paperparcel.PaperParcel

@PaperParcel
data class RenderableText(
        val rendered: String,
        val protected: Boolean = false) : Parcelable {
    companion object {
        val empty = RenderableText("", false)

        fun fromHtml(html: String): Spanned {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    return Html.fromHtml(html)
                }
            } catch (t: Throwable) {
                return SpannableString("")
            }
        }

        @JvmField val CREATOR = PaperParcelRenderableText.CREATOR
    }

    fun text(): CharSequence = fromHtml(rendered)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelRenderableText.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0
}