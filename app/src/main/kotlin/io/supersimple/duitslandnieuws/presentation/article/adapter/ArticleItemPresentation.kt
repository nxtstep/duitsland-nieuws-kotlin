package io.supersimple.duitslandnieuws.presentation.article.adapter

import io.supersimple.duitslandnieuws.data.models.Article
import java.text.DateFormat

data class ArticleItemPresentation(val id: String,
                                   val pubDate: CharSequence,
                                   val title: CharSequence,
                                   val excerpt: CharSequence) {
    companion object {
        fun from(article: Article, dateFormatter: DateFormat): ArticleItemPresentation =
                ArticleItemPresentation(article.id,
                        dateFormatter.format(article.date),
                        article.title.text(),
                        article.excerpt.text()
                )
    }
}