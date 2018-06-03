package io.supersimple.duitslandnieuws.presentation.detail

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Maybe
import io.reactivex.schedulers.TestScheduler
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.models.MediaDetails
import io.supersimple.duitslandnieuws.data.models.MediaItem
import io.supersimple.duitslandnieuws.data.models.RenderableText
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor
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
