package io.supersimple.duitslandnieuws.presentation.article

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.module.ThreadingModule

@ExperimentalPagingApi
class ArticleRepositoryMediator(
    private val repository: ArticleRepository,
    private val schedulers: ThreadingModule,
) : RxRemoteMediator<Int, Article>() {
    override fun loadSingle(loadType: LoadType, state: PagingState<Int, Article>): Single<MediatorResult> =
        Single.defer<MediatorResult> {
            when (loadType) {
                LoadType.REFRESH -> return@defer repository.refresh(loadType.pageSize(state))
                    .map<MediatorResult> { MediatorResult.Success(endOfPaginationReached = it.isEmpty()) }
                    .onErrorResumeNext { error -> Single.just(MediatorResult.Error(error)) }
                    .subscribeOn(schedulers.io)
                LoadType.PREPEND -> return@defer Single.just(MediatorResult.Success(endOfPaginationReached = true))
                    .doOnSubscribe { println("Prepend") }
                LoadType.APPEND -> {
                    val beforeDate = when (loadType) {
                        LoadType.APPEND -> {
                            state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.date
                        }
                        else -> null
                    }

                    return@defer repository.load(page = 1, pageSize = loadType.pageSize(state), before = beforeDate)
                        .map<MediatorResult> { MediatorResult.Success(endOfPaginationReached = it.isEmpty()) }
                        .onErrorResumeNext { error -> Single.just(MediatorResult.Error(error)) }
                        .subscribeOn(schedulers.io)
                    .doOnSubscribe { println("Load page: 1, before: $beforeDate") }
                }
            }
        }
}

fun LoadType.pageSize(state: PagingState<Int, Article>): Int =
    when (this) {
        LoadType.REFRESH -> state.config.initialLoadSize
        else -> state.config.pageSize
    }
