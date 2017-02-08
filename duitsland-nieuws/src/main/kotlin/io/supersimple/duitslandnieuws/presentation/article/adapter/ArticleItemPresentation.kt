package io.supersimple.duitslandnieuws.presentation.article.adapter

import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import java.text.DateFormat

data class ArticleItemPresentation(val id: String,
                                   val pubDate: CharSequence,
                                   val title: CharSequence,
                                   val excerpt: CharSequence,
                                   val imageUrl: String? = null) {
    companion object {
        const val IMAGE_THUMBNAIL = "medium"

        fun from(article: Article, media: Media, dateFormatter: DateFormat): ArticleItemPresentation {
            return ArticleItemPresentation(article.id,
                    dateFormatter.format(article.date),
                    article.title.text(),
                    article.excerpt.text(),
                    media.media_details.sizes[IMAGE_THUMBNAIL]?.source_url)
        }
    }
}