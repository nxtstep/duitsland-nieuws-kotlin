package io.supersimple.duitslandnieuws.presentation.detail

import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.models.MediaItem.Companion.IMAGE_FULL
import java.text.DateFormat

data class ArticleDetailPresentation(val articleId: String,
                                     val pubDate: CharSequence,
                                     val title: CharSequence,
                                     val text: CharSequence,
                                     val caption: CharSequence? = null,
                                     val imageUrl: String? = null) {
    companion object {
        val empty = ArticleDetailPresentation("", "", "", "")

        fun from(article: Article, media: Media, dateFormatter: DateFormat): ArticleDetailPresentation {
            return ArticleDetailPresentation(article.id,
                    dateFormatter.format(article.date),
                    article.title.text(),
                    article.content.text(),
                    media.caption.text(),
                    media.media_details.sizes[IMAGE_FULL]?.source_url)
        }
    }
}