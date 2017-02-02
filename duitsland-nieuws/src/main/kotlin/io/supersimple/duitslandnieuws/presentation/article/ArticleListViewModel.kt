package io.supersimple.duitslandnieuws.presentation.article

import android.databinding.ObservableArrayList
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository

class ArticleListViewModel(private val articleRepository: ArticleRepository,
                           private val ioScheduler: Scheduler,
                           private val mainScheduler: Scheduler) : ObservableArrayList<Article>() {
    private var subscriptions: CompositeDisposable? = null
    private var articleListView: ArticleListView? = null

    private val stateSubject = PublishSubject.create<ArticleListLoadingState>()

    fun bindView(view: ArticleListView) {
        subscriptions?.dispose()
        subscriptions = CompositeDisposable()

        articleListView = view

        subscriptions!!.add(
                stateSubject.observeOn(mainScheduler)
                        .subscribe({ state ->
                            articleListView?.showLoadingIndicator(state == ArticleListLoadingState.LOADING)
                        })
        )

        subscriptions!!.add(
                articleRepository.list()
                        .switchIfEmpty(articleRepository.refresh().toMaybe())
                        .doOnSubscribe({ stateSubject.onNext(ArticleListLoadingState.LOADING) })
                        .doOnSuccess({ stateSubject.onNext(ArticleListLoadingState.FINISHED) })
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(
                                { list ->
                                    addAll(list)
                                    articleListView?.showArticleListLoaded(1)
                                },
                                { error ->
                                    articleListView?.showError()
                                    error.printStackTrace()
                                },
                                {
                                    println("Finished")
                                })
        )
    }

    fun unbind() {
        subscriptions?.dispose()
        articleListView = null
    }

    fun loadNextPage() {
        //TODO
    }

    fun refresh() {
        //TODO
    }

    enum class ArticleListLoadingState {
        LOADING,
        FINISHED,
        ERROR
    }
}
