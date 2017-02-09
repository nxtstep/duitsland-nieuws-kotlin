package io.supersimple.duitslandnieuws.presentation.detail

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import io.reactivex.schedulers.TestScheduler
import io.supersimple.duitslandnieuws.data.models.*
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.util.*

class ArticlePresenterTest {

    lateinit var mockIteractor: ArticleInteractor

    companion object {
        val testPresentation = ArticleDetailPresentation("test-id",
                "13:46 - 9 February 2017",
                "test-title",
                "text-string",
                "Caption",
                "http://image.url.com/image/1.jpg")

        val testArticle = Article("test-id", Date(1486644394000L), Date(), "no-slug", "http://article.link.com",
                RenderableText("test-title"),
                RenderableText("text-string"),
                RenderableText("Excerpt"),
                "author-id",
                "media-id")

        val testMedia = Media("media-id",
                Date(),
                RenderableText("media-title"),
                "media-author-id",
                "no-image-slug",
                RenderableText("Caption"),
                MediaDetails(100, 200, "image-name.jpg", hashMapOf("full" to MediaItem("filename", 101, 202, "image/jpeg", "http://image.url.com/image/1.jpg"))))
    }

    @Before
    fun setup() {
        Locale.setDefault(Locale.ENGLISH)
    }

    @Test
    fun testPresenter() {
        mockIteractor = mock {
            on { get(anyString()) } doReturn Maybe.just(Pair(testArticle, testMedia))
        }
        val mockView: ArticleView = mock {}
        val testScheduler = TestScheduler()
        val presenter = ArticlePresenter("test-id", mockIteractor, testScheduler, testScheduler)

        presenter.bind(mockView)

        testScheduler.triggerActions()

        verify(mockView, never()).showError(any())
        verify(mockView, times(1)).showLoading(eq(true))
        verify(mockView, times(1)).showArticle(eq(testPresentation))
        verify(mockView, times(1)).showLoading(eq(false))
    }
}
