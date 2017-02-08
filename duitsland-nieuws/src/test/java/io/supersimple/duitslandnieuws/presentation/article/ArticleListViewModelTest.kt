package io.supersimple.duitslandnieuws.presentation.article

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepositoryTest.Companion.testMediaItem
import org.junit.Test
import org.mockito.ArgumentMatchers
import java.util.*

class ArticleListViewModelTest {

    companion object {
        val article = Article("1", Date(), Date(), "MySlug", "http://www.duitslandnieuws.de",
                RenderableText("title", true), RenderableText("Content", false),
                RenderableText("Excerpt", false), "Author", "media-id")

        val articleList: List<Article> = Arrays.asList(article)
    }

    lateinit var mockRepository: ArticleRepository
    lateinit var mockMediaRepository: MediaRepository

    @Test
    fun testArticleListViewModel_list() {
        mockRepository = mock {
            on { list() } doReturn Maybe.just(articleList)
            on { refresh() } doReturn Single.error(IllegalStateException("Refresh should not be called in test"))
        }
        mockMediaRepository = mock {
            on { get(ArgumentMatchers.anyString()) } doReturn Maybe.just(testMediaItem)
        }
        val mockView: ArticleListView = mock {}
        val testScheduler = TestScheduler()
        val viewModel = ArticleListViewModel(mockRepository, mockMediaRepository, testScheduler, testScheduler)
        viewModel.bindView(mockView)

        testScheduler.triggerActions()

        verify(mockRepository, times(1)).list()
        verify(mockMediaRepository, times(1)).get(eq("media-id"))

        verify(mockView, times(1)).showArticleListLoaded(eq(1))
        verify(mockView, times(1)).showLoadingIndicator(eq(true))
        verify(mockView, times(1)).showLoadingIndicator(eq(false))
        verify(mockView, never()).showEmptyState()
        verify(mockView, never()).showError()
    }
}
