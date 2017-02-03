package io.supersimple.duitslandnieuws.presentation.article

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import org.junit.Test
import java.util.*

class ArticleListViewModelTest {

    lateinit var mockRepository: ArticleRepository

    val article = Article("1", Date(), Date(), "MySlug", "http://www.duitslandnieuws.de",
            RenderableText("title", true), RenderableText("Content", false),
            RenderableText("Excerpt", false), "Author")

    val articleList: List<Article> = Arrays.asList(article)

    @Test
    fun testArticleListViewModel_list() {
        mockRepository = mock {
            on { list() } doReturn Maybe.just(articleList)
            on { refresh() } doReturn Single.error(IllegalStateException("Refresh should not be called in test"))
        }
        val mockView: ArticleListView = mock {}
        val testScheduler = TestScheduler()
        val viewModel = ArticleListViewModel(mockRepository, testScheduler, testScheduler)
        viewModel.bindView(mockView)

        testScheduler.triggerActions()

        verify(mockRepository, times(1)).list()

        verify(mockView, times(1)).showArticleListLoaded(eq(1))
        verify(mockView, times(1)).showLoadingIndicator(eq(true))
        verify(mockView, times(1)).showLoadingIndicator(eq(false))
        verify(mockView, never()).showEmptyState()
        verify(mockView, never()).showError()
    }
}
