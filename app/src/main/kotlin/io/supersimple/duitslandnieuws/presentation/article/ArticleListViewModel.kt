package io.supersimple.duitslandnieuws.presentation.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.module.ThreadingModule
import kotlinx.coroutines.flow.Flow

@ExperimentalPagingApi
class ArticleListViewModel(
    repository: ArticlePagedRepository,
    pageSize: Int = PAGE_SIZE,
    schedulers: ThreadingModule = ThreadingModule.default
) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 10
    }

    val articleListState: Flow<PagingData<Article>> =
        repository.articles(pageSize = pageSize, schedulers = schedulers)
            .cachedIn(viewModelScope)
}