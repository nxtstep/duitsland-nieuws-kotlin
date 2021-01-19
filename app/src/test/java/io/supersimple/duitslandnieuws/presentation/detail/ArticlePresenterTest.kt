package io.supersimple.duitslandnieuws.presentation.detail

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Maybe
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.models.MediaDetails
import io.supersimple.duitslandnieuws.data.models.MediaItem
import io.supersimple.duitslandnieuws.data.models.RenderableText
import io.supersimple.duitslandnieuws.util.TestThreadingModule
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArticlePresenterTest {

    lateinit var mockIteractor: ArticleInteractor

    companion object {
        val dateFormatter: SimpleDateFormat by lazy {
            SimpleDateFormat("H:mm - d MMMM yyyy")
        }

        val testDate = Date(1486644394000L)

        val testPresentation = ArticleDetailPresentation("test-id",
                dateFormatter.format(testDate),
                "test-title",
                "text-string",
                "Caption",
                "http://image.url.com/image/1.jpg")

        val testArticle = Article("test-id", testDate, Date(), "no-slug", "http://article.link.com",
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
        val testSchedulers = TestThreadingModule()
        val presenter = ArticlePresenter("test-id", mockIteractor, testSchedulers)

        presenter.bind(mockView)

        testSchedulers.testScheduler.triggerActions()

        inOrder(mockView) {
            verify(mockView).showLoading(eq(true))
            verify(mockView).showLoading(eq(false))
        }

        verify(mockView, never()).showError(any())
        verify(mockView).showArticle(eq(testPresentation))
    }

    @Test
    fun testPresenterOnEmpty() {
        mockIteractor = mock {
            on { get(anyString()) } doReturn Maybe.empty<Pair<Article, Media>>()
        }
        val mockView: ArticleView = mock {}
        val testSchedulers = TestThreadingModule()
        val presenter = ArticlePresenter("test-id", mockIteractor, testSchedulers)

        presenter.bind(mockView)

        testSchedulers.testScheduler.triggerActions()

        val inOrder = inOrder(mockView)

        verify(mockView).showError(any())
        inOrder.verify(mockView).showLoading(eq(true))
        inOrder.verify(mockView).showLoading(eq(false))
        verify(mockView, never()).showArticle(eq(testPresentation))
    }

    @Test
    fun testPresenterOnError() {
        mockIteractor = mock {
            on { get(anyString()) } doReturn Maybe.error(IllegalStateException("Test exception"))
        }
        val mockView: ArticleView = mock {}
        val testSchedulers = TestThreadingModule()
        val presenter = ArticlePresenter("test-id", mockIteractor, testSchedulers)

        presenter.bind(mockView)

        testSchedulers.testScheduler.triggerActions()

        val inOrder = inOrder(mockView)

        verify(mockView).showError(any())
        inOrder.verify(mockView).showLoading(eq(true))
        inOrder.verify(mockView).showLoading(eq(false))
        verify(mockView, never()).showArticle(eq(testPresentation))
    }
}
