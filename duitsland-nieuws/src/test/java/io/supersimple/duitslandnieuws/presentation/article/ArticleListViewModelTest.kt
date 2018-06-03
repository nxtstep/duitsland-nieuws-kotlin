package io.supersimple.duitslandnieuws.presentation.article

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepositoryTest.Companion.testMediaItem
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import java.util.Date

class ArticleListViewModelTest {

    companion object {
        val article = Article("1", Date(), Date(), "MySlug", "http://www.duitslandnieuws.de",
                RenderableText("title", true), RenderableText("Content", false),
                RenderableText("Excerpt", false), "Author", "media-id")
    }

    lateinit var mockInteractor: ArticleInteractor

    @Test
    fun testArticleListViewModel_list() {
        mockInteractor = mock {
            on { list(anyInt(), anyInt()) } doReturn Observable.just(Pair(article, testMediaItem))
            on { refresh(anyInt()) } doReturn Observable.error(IllegalStateException("Refresh should not be called in test"))
        }
        val mockView: ArticleListView = mock {}

        val testScheduler = TestScheduler()
        val viewModel = ArticleListViewModel(mockInteractor, testScheduler, testScheduler)
        viewModel.bindView(mockView)

        testScheduler.triggerActions()

        verify(mockInteractor, times(1)).list(eq(0), anyInt())

        verify(mockView, times(1)).showArticleListLoaded(eq(1))
        verify(mockView, times(1)).showLoadingIndicator(eq(true))
        verify(mockView, times(1)).showLoadingIndicator(eq(false))
        verify(mockView, never()).showEmptyState()
        verify(mockView, never()).showError(any())
    }

    @Test
    fun testArticleListViewModel_refresh() {
        mockInteractor = mock {
            on { list(anyInt(), anyInt()) } doReturn Observable.error(IllegalStateException("Refresh should not be called in test"))
            on { refresh(anyInt()) } doReturn Observable.just(Pair(article, testMediaItem))
        }
        val mockView: ArticleListView = mock {}

        val testScheduler = TestScheduler()
        val viewModel = ArticleListViewModel(mockInteractor, testScheduler, testScheduler)
        viewModel.page = 0
        viewModel.bindView(mockView)

        testScheduler.triggerActions()

        verify(mockView, never()).showArticleListLoaded(anyInt())
        verify(mockView, never()).showLoadingIndicator(eq(true))
        verify(mockView, never()).showLoadingIndicator(eq(false))
        verify(mockView, never()).showEmptyState()
        verify(mockView, never()).showError(any())

        viewModel.refresh()
        testScheduler.triggerActions()

        verify(mockInteractor, times(1)).refresh(anyInt())

        verify(mockView, times(1)).showArticleListLoaded(eq(1))
        verify(mockView, times(1)).showLoadingIndicator(eq(true))
        verify(mockView, times(1)).showLoadingIndicator(eq(false))
        verify(mockView, never()).showEmptyState()
        verify(mockView, never()).showError(any())
    }
}
