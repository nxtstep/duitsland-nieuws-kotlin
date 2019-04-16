package io.supersimple.duitslandnieuws.presentation.detail

import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor
import io.supersimple.duitslandnieuws.presentation.article.ArticleListViewModel
import io.supersimple.duitslandnieuws.presentation.mvp.Presenter
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.Locale

class ArticlePresenter(private val articleId: String,
                       private val interactor: ArticleInteractor,
                       private val ioScheduler: Scheduler,
                       private val mainScheduler: Scheduler) : Presenter<ArticleView> {

    enum class ArticleLoadingState {
        LOADING,
        FINISHED,
        ERROR
    }

    private val dateFormatter = SimpleDateFormat(ArticleListViewModel.DATE_FORMAT, Locale.getDefault())

    private val subscriptions: CompositeDisposable by lazy { CompositeDisposable() }

    private val stateSubject = PublishSubject.create<ArticleLoadingState>()

    override fun bind(view: ArticleView) {
        subscriptions.apply {

            add(
                    stateSubject.observeOn(mainScheduler)
                            .subscribe { state ->
                                view.showLoading(state == ArticleLoadingState.LOADING)
                            }
            )

            add(
                    interactor.get(articleId)
                            .doOnSubscribe { stateSubject.onNext(ArticleLoadingState.LOADING) }
                            .map(::convertToArticlePresentation)
                            .switchIfEmpty(Maybe.error(IllegalStateException("No article found")))
                            .doOnSuccess { stateSubject.onNext(ArticleLoadingState.FINISHED) }
                            .doOnError { stateSubject.onNext(ArticleLoadingState.ERROR) }
                            .subscribeOn(ioScheduler)
                            .observeOn(mainScheduler)
                            .subscribe(view::showArticle, view::showError)
            )
        }
    }

    override fun unbind() {
        subscriptions.clear()
    }

    private fun convertToArticlePresentation(pair: Pair<Article, Media>): ArticleDetailPresentation =
            ArticleDetailPresentation.from(pair.first, pair.second, dateFormatter)
}