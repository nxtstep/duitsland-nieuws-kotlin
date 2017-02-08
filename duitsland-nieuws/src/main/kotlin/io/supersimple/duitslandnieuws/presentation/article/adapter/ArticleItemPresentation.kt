package io.supersimple.duitslandnieuws.presentation.article.adapter

import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import java.text.DateFormat

data class ArticleItemPresentation(val id: String,
                                   val pubDate: String,
                                   val title: String,
                                   val excerpt: String,
                                   val imageUrl: String? = null) {
    companion object {
        fun from(article: Article, media: Media, dateFormatter: DateFormat): ArticleItemPresentation {
            return ArticleItemPresentation(article.id,
                    dateFormatter.format(article.date),
                    article.title.rendered,
                    article.excerpt.rendered,
                    media.media_details.sizes["dn-thumb-96"]?.source_url)
        }
    }
}