package io.supersimple.duitslandnieuws.presentation.article

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepositoryTest.Companion.testMediaItem
import io.supersimple.duitslandnieuws.presentation.detail.ArticleInteractor
import io.supersimple.duitslandnieuws.util.TestThreadingModule
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

        val testSchedulers = TestThreadingModule()
        val viewModel = ArticleListViewModel(mockInteractor, testSchedulers)
        viewModel.bindView(mockView)

        testSchedulers.testScheduler.triggerActions()

        verify(mockInteractor).list(eq(0), anyInt())

        verify(mockView).showArticleListLoaded(eq(1))
        verify(mockView).showLoadingIndicator(eq(true))
        verify(mockView).showLoadingIndicator(eq(false))
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

        val testSchedulers = TestThreadingModule()
        val viewModel = ArticleListViewModel(mockInteractor, testSchedulers)
        viewModel.page = 0
        viewModel.bindView(mockView)

        testSchedulers.testScheduler.triggerActions()

        verify(mockView, never()).showArticleListLoaded(anyInt())
        verify(mockView, never()).showLoadingIndicator(eq(true))
        verify(mockView, never()).showLoadingIndicator(eq(false))
        verify(mockView, never()).showEmptyState()
        verify(mockView, never()).showError(any())

        viewModel.refresh()
        testSchedulers.testScheduler.triggerActions()

        verify(mockInteractor).refresh(anyInt())

        verify(mockView).showArticleListLoaded(eq(1))
        verify(mockView).showLoadingIndicator(eq(true))
        verify(mockView).showLoadingIndicator(eq(false))
        verify(mockView, never()).showEmptyState()
        verify(mockView, never()).showError(any())
    }
}
