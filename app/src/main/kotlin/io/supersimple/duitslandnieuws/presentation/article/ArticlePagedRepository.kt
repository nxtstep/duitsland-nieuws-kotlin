package io.supersimple.duitslandnieuws.presentation.article

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.rxjava2.RxPagingSource
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.module.ThreadingModule

class ArticlePagedRepository(
    private val repository: ArticleRepository,
) {
    @ExperimentalPagingApi
    fun articles(pageSize: Int, schedulers: ThreadingModule) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = ArticleRepositoryMediator(repository = repository, schedulers = schedulers)
    ) {
        repository.pagingSource(pageSize, schedulers)
    }.flow
}

fun ArticleRepository.pagingSource(fixedPageSize: Int, schedulers: ThreadingModule): PagingSource<Int, Article> =
    object : RxPagingSource<Int, Article>() {
        private var loadedItems = 0
        private val subscription =
            invalidationSubject.observeOn(schedulers.main)
                .subscribeBy(onNext = {
                    invalidate()
                })

        override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Article>> {
            val page = params.key ?: 1
            val prevKey = if (page > 1) page - 1 else null
            return list(
                page = page,
                pageSize = fixedPageSize,
            )
                .doOnSuccess { loadedItems += it.size }
                .map<LoadResult<Int, Article>> { list ->
                    LoadResult.Page(
                        data = list,
                        prevKey = prevKey,
                        nextKey = if (list.isEmpty()) null else page + 1,
                    )
                }
                .onErrorResumeNext { error -> Single.just(LoadResult.Error(error)) }
        }
    }
