package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.models.Article

class ArticleCloud(private val articleService: ArticleEndpoint) {
    fun list(): Single<List<Article>> {
        return articleService.list()
    }

    fun get(id: String): Single<Article> {
        return articleService.get(id)
    }
}