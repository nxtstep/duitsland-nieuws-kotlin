package io.supersimple.duitslandnieuws.data.repositories.article

import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import org.junit.Test
import java.util.Date

class ArticleCacheTest {
    @Test
    fun testArticleCache() {
        val cache = ArticleCache()
        val article = Article("art-id-1",
                Date(),
                Date(),
                "test-slug",
                "http://www.link.com/?p=some-9",
                RenderableText("Rendered Title text", false),
                RenderableText("Rendered content text", true),
                RenderableText("Rendered excerpt text", true),
                "author-id-1",
                "media-id")

        cache.save(article)
                .test()
                .assertResult(article)

        cache.delete("art-id-1")
                .test()
                .assertResult(article)

        cache.delete("art-id-1")
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()
    }
}