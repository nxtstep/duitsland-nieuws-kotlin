package io.supersimple.duitslandnieuws.data.repositories.article

import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.repositories.SimpleMemCache
import java.util.LinkedHashMap

class ArticleCache(map: MutableMap<String, Article> = LinkedHashMap()) : SimpleMemCache<String, Article>(map) {
    override fun getId(value: Article): String {
        return value.id
    }
}