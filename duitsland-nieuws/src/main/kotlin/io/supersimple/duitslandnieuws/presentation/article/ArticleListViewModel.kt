package io.supersimple.duitslandnieuws.presentation.article

import android.databinding.ObservableArrayList
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleItemPresentation
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleListViewModel(private val articleInteractor: ArticleInteractor,
                           private val ioScheduler: Scheduler,
                           private val mainScheduler: Scheduler) : ObservableArrayList<ArticleItemPresentation>() {

    companion object {
        const val DATE_FORMAT = "H:mm - d MMMM yyyy"

        const val PAGE_SIZE = 10
    }

    enum class ArticleListLoadingState {
        LOADING,
        FINISHED,
        ERROR
    }

    private var subscriptions: CompositeDisposable? = null
    private var articleListView: ArticleListView? = null

    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    private val stateSubject = PublishSubject.create<ArticleListLoadingState>()
    var page = -1
    private var pendingPage = -1

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

        if (page == -1) {
            loadFirstPage()
        }
    }

    fun unbind() {
        subscriptions?.dispose()
        articleListView = null
        if (pendingPage == page) {
            page--
        }
        pendingPage = -1
    }

    fun loadFirstPage() {
        page = 0
        loadNextPage()
    }

    fun loadNextPage() {
        if (pendingPage == page) {
            return
        }
        pendingPage = page
        subscriptions!!.add(
                articleInteractor.list(page, PAGE_SIZE)
                        .switchIfEmpty(articleInteractor.refresh(PAGE_SIZE))
                        .map({ convertToPresentation(it) })
                        .doOnSubscribe({ stateSubject.onNext(ArticleListLoadingState.LOADING) })
                        .doFinally({ stateSubject.onNext(ArticleListLoadingState.FINISHED) })
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(
                                { item ->
                                    add(item)
                                },
                                { error ->
                                    articleListView?.showError(error)
                                    error.printStackTrace()
                                },
                                {
                                    articleListView?.showArticleListLoaded(++page)
                                })
        )
    }

    fun refresh() {
        page = 0
        pendingPage = page

        clear()

        subscriptions!!.add(
                articleInteractor.refresh(PAGE_SIZE)
                        .map({ convertToPresentation(it) })
                        .doOnSubscribe({ stateSubject.onNext(ArticleListLoadingState.LOADING) })
                        .doFinally({ stateSubject.onNext(ArticleListLoadingState.FINISHED) })
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(
                                { item ->
                                    add(item)
                                },
                                { error ->
                                    articleListView?.showError(error)
                                    error.printStackTrace()
                                },
                                {
                                    articleListView?.showArticleListLoaded(++page)
                                })
        )
    }

    private fun convertToPresentation(pair: Pair<Article, Media>): ArticleItemPresentation =
            ArticleItemPresentation.from(pair.first, pair.second, dateFormatter)
}
