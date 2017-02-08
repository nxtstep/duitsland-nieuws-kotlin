package io.supersimple.duitslandnieuws.presentation.article

import android.databinding.ObservableArrayList
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleItemPresentation
import java.text.SimpleDateFormat
import java.util.*

class ArticleListViewModel(private val articleRepository: ArticleRepository,
                           private val mediaRepository: MediaRepository,
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
    private var page = -1
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
                articleRepository.list(page, PAGE_SIZE)
                        .switchIfEmpty(articleRepository.refresh(PAGE_SIZE).toMaybe())
                        .toObservable()
                        .flatMapIterable({ it })
                        .flatMapMaybe({ mergeWithMedia(it) })
                        .flatMapMaybe({ convertToPresentation(it) })
                        .toList()
                        .doOnSubscribe({ stateSubject.onNext(ArticleListLoadingState.LOADING) })
                        .doOnSuccess({ stateSubject.onNext(ArticleListLoadingState.FINISHED) })
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(
                                { list ->
                                    addAll(list)
                                    articleListView?.showArticleListLoaded(++page)
                                },
                                { error ->
                                    articleListView?.showError()
                                    error.printStackTrace()
                                })
        )
    }

    fun refresh() {
        page = 0
        pendingPage = page
        subscriptions!!.add(
                articleRepository.refresh(PAGE_SIZE)
                        .toObservable()
                        .flatMapIterable({ it })
                        .flatMapMaybe({ mergeWithMedia(it) })
                        .flatMapMaybe({ convertToPresentation(it) })
                        .toList()
                        .doOnSubscribe({ stateSubject.onNext(ArticleListLoadingState.LOADING) })
                        .doOnSuccess({ stateSubject.onNext(ArticleListLoadingState.FINISHED) })
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(
                                { list ->
                                    clear()
                                    addAll(list)
                                    articleListView?.showArticleListLoaded(++page)
                                },
                                { error ->
                                    articleListView?.showError()
                                    error.printStackTrace()
                                })
        )
    }

    private fun mergeWithMedia(article: Article): Maybe<Pair<Article, Media>> {
        return Maybe.just(article)
                .zipWith(mediaRepository.get(article.featured_media)
                        .onErrorComplete()
                        .defaultIfEmpty(Media.empty)
                        , BiFunction { t1, t2 -> Pair(t1, t2) })
    }

    private fun convertToPresentation(pair: Pair<Article, Media>): Maybe<ArticleItemPresentation> {
        return Maybe.just(pair)
                .map({ ArticleItemPresentation.from(it.first, it.second, dateFormatter) })
    }
}
