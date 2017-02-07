package io.supersimple.duitslandnieuws.data.repositories.article

import io.requery.*
import io.supersimple.duitslandnieuws.data.models.RenderableText
import java.util.Date

@Entity
interface ArticleDAO : Persistable {
    @get:Key
    val id: String
    val date: Date
    val modified: Date
    val slug: String
    val link: String

    @get:Convert(RenderableTextConverter::class)
    val title: RenderableText
    @get:Convert(RenderableTextConverter::class)
    val content: RenderableText
    @get:Convert(RenderableTextConverter::class)
    val excerpt: RenderableText
    val author: String
    val featured_media: String

    class RenderableTextConverter : Converter<RenderableText, String> {
        companion object {
            val SEPARATOR_SYMBOL = '|'
        }

        override fun getPersistedType(): Class<String> {
            return String::class.java
        }

        override fun convertToMapped(type: Class<out RenderableText>?, value: String?): RenderableText {
            value?.let {
                val index = it.indexOfFirst {
                    it.equals(SEPARATOR_SYMBOL)
                }
                val protected: Boolean = it.subSequence(0, index).equals(true.toString())
                val text = it.substring(index + 1)
                return RenderableText(text, protected)
            }
            throw IllegalStateException("Did not save RenderedText properly")
        }

        override fun getMappedType(): Class<RenderableText> {
            return RenderableText::class.java
        }

        override fun getPersistedSize(): Int? {
            return null
        }

        override fun convertToPersisted(value: RenderableText?): String {
            return value?.protected.toString() + SEPARATOR_SYMBOL + value?.rendered
        }
    }
}

