package io.supersimple.duitslandnieuws.data.repositories.article

import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.repositories.SimpleMemCache
import java.util.HashMap

class ArticleCache(map: MutableMap<String, Article> = HashMap<String, Article>()) : SimpleMemCache<Article>(map) {
    override fun getId(value: Article): String {
        return value.id
    }
}