package io.supersimple.duitslandnieuws.presentation.article

import android.databinding.ObservableArrayList
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleItemPresentation
import java.text.SimpleDateFormat
import java.util.*

class ArticleListViewModel(private val articleRepository: ArticleRepository,
                           private val ioScheduler: Scheduler,
                           private val mainScheduler: Scheduler) : ObservableArrayList<ArticleItemPresentation>() {

    private var subscriptions: CompositeDisposable? = null
    private var articleListView: ArticleListView? = null

    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

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
                        .flatMap({ convertToPresentation(it) })
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

    private fun convertToPresentation(list: List<Article>): Maybe<List<ArticleItemPresentation>> {
        return Observable.fromIterable(list)
                .map({ ArticleItemPresentation.from(it, dateFormatter) })
                .toList()
                .filter { it.isNotEmpty() }
    }

    companion object {
        const val DATE_FORMAT = "H:mm - d MMMM yyyy"
    }

    enum class ArticleListLoadingState {
        LOADING,
        FINISHED,
        ERROR
    }
}
