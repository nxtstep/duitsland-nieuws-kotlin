package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Maybe
import io.supersimple.duitslandnieuws.data.models.Article

class ArticleCloud {
    fun list(): Maybe<List<Article>> {
        return Maybe.empty()
    }
}