package io.supersimple.duitslandnieuws.presentation.detail

import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.module.ThreadingModule
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleListAdapter
import io.supersimple.duitslandnieuws.presentation.mvp.Presenter
import java.text.SimpleDateFormat
import java.util.Locale

class ArticlePresenter(
    private val articleId: String,
    private val interactor: ArticleInteractor,
    private val schedulers: ThreadingModule = ThreadingModule.default,
) : Presenter<ArticleView> {

    enum class ArticleLoadingState {
        LOADING,
        FINISHED,
        ERROR
    }

    private val dateFormatter =
        SimpleDateFormat(ArticleListAdapter.DATE_FORMAT, Locale.getDefault())

    private val subscriptions: CompositeDisposable by lazy { CompositeDisposable() }

    private val stateSubject = PublishSubject.create<ArticleLoadingState>()

    override fun bind(view: ArticleView) {
        stateSubject.observeOn(schedulers.main)
            .subscribe { state ->
                view.showLoading(state == ArticleLoadingState.LOADING)
            }
            .addTo(subscriptions)

        interactor.get(articleId)
            .doOnSubscribe { stateSubject.onNext(ArticleLoadingState.LOADING) }
            .map(::convertToArticlePresentation)
            .switchIfEmpty(Maybe.error(IllegalStateException("No article found")))
            .doOnSuccess { stateSubject.onNext(ArticleLoadingState.FINISHED) }
            .doOnError { stateSubject.onNext(ArticleLoadingState.ERROR) }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.main)
            .subscribe(view::showArticle, view::showError)
            .addTo(subscriptions)
    }

    override fun unbind() {
        subscriptions.clear()
    }

    private fun convertToArticlePresentation(pair: Pair<Article, Media>): ArticleDetailPresentation =
        ArticleDetailPresentation.from(pair.first, pair.second, dateFormatter)
}