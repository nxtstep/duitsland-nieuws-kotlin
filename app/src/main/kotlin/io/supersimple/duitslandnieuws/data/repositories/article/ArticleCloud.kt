package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.models.Article
import java.util.Date

class ArticleCloud(private val articleService: ArticleEndpoint) {
    fun list(page: Int, pageSize: Int, after: Date? = null, before: Date? = null): Single<List<Article>> {
        require(page > 0)
        return articleService.list(page = page, pageSize = pageSize, after = after, before = before)
    }

    fun get(id: String): Maybe<Article> = articleService.get(id)
}