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

    private fun mergeWithMedia(article: Article): Maybe<Pair<Article, Media>> {
        return Maybe.just(article)
                .zipWith(mediaRepository.get(article.featured_media)
                        .defaultIfEmpty(Media.empty)
                        , BiFunction { t1, t2 -> Pair(t1, t2) })
    }

    private fun convertToPresentation(pair: Pair<Article, Media>): Maybe<ArticleItemPresentation> {
        return Maybe.just(pair)
                .map({ ArticleItemPresentation.from(it.first, it.second, dateFormatter) })
    }
}
