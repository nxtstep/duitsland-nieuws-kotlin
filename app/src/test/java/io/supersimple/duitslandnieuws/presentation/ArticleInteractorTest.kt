package io.supersimple.duitslandnieuws.presentation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepositoryTest.Companion.testArticle
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepositoryTest.Companion.testMediaItem
import io.supersimple.duitslandnieuws.presentation.detail.ArticleInteractor
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

class ArticleInteractorTest {

    lateinit var mockArticleRepo: ArticleRepository
    lateinit var mockMediaRepo: MediaRepository

    @Test
    fun testGet() {
        mockArticleRepo = mock {
            on { get(anyString()) } doReturn Maybe.just(testArticle)
        }
        mockMediaRepo = mock {
            on { get(anyString()) } doReturn Maybe.just(testMediaItem)
        }

        val interactor = ArticleInteractor(mockArticleRepo, mockMediaRepo)
        interactor.get("test-id")
                .test()
                .assertResult(Pair(testArticle, testMediaItem))
    }

    @Test
    fun testList() {
        val article2 = testArticle.copy("2")
        mockArticleRepo = mock {
            on { list(anyInt(), anyInt()) } doReturn Maybe.just(listOf(testArticle, article2))
            on { refresh(anyInt()) } doReturn Single.error(IllegalStateException("Should not be called in this test-case"))
        }
        mockMediaRepo = mock {
            on { get(anyString()) } doReturn Maybe.just(testMediaItem)
        }

        val interactor = ArticleInteractor(mockArticleRepo, mockMediaRepo)
        interactor.list(0, 10)
                .test()
                .assertResult(Pair(testArticle, testMediaItem), Pair(article2, testMediaItem))
    }

    @Test
    fun testRefresh() {
        val article2 = testArticle.copy("2")
        mockArticleRepo = mock {
            on { refresh(anyInt()) } doReturn Single.just(listOf(testArticle, article2))
        }
        mockMediaRepo = mock {
            on { get(anyString()) } doReturn Maybe.just(testMediaItem)
        }

        val interactor = ArticleInteractor(mockArticleRepo, mockMediaRepo)
        interactor.refresh(10)
                .test()
                .assertResult(Pair(testArticle, testMediaItem), Pair(article2, testMediaItem))
    }
}