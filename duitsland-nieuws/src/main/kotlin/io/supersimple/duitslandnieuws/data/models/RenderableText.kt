package io.supersimple.duitslandnieuws.data.models

import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import paperparcel.PaperParcel
import kotlin.reflect.KClass

data class RenderableText(
        val rendered: String,
        val protected: Boolean = false
) : Parcelable {
    companion object {
        val empty = RenderableText("", false)

        fun fromHtml(html: String): CharSequence {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    return Html.fromHtml(html)
                }
            } catch (t: Throwable) {
                return html
            }
        }

        @JvmField
        val CREATOR = object: Parcelable.Creator<RenderableText> {
            override fun newArray(size: Int): Array<RenderableText?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel?): RenderableText {
                source?.apply {
                    val protected = readInt() == 1
                    val text = readString()

                    return RenderableText(rendered = text, protected = protected)
                }
                throw IllegalStateException("No parcelable source given")
            }
        }

    }


    fun text(): CharSequence = fromHtml(rendered)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (protected) 1 else 0)
        dest.writeString(rendered)
    }

    override fun describeContents(): Int = 0
}