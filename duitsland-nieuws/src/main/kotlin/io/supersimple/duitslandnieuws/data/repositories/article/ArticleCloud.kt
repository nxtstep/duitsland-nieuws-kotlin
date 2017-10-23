package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.models.Article

class ArticleCloud(private val articleService: ArticleEndpoint) {
    fun list(page: Int, pageSize: Int): Single<List<Article>> =
            articleService.list((page + 1).toString(), pageSize)

    fun get(id: String): Maybe<Article> = articleService.get(id)
}