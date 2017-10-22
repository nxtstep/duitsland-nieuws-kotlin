package io.supersimple.duitslandnieuws.presentation.detail

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor
import io.supersimple.duitslandnieuws.presentation.article.ArticleListViewModel
import io.supersimple.duitslandnieuws.presentation.mvp.Presenter
import java.text.SimpleDateFormat
import java.util.Locale

class ArticlePresenter(val articleId: String,
                       private val interactor: ArticleInteractor,
                       private val ioScheduler: Scheduler,
                       private val mainScheduler: Scheduler) : Presenter<ArticleView> {

    enum class ArticleLoadingState {
        LOADING,
        FINISHED,
        ERROR
    }

    private val dateFormatter = SimpleDateFormat(ArticleListViewModel.DATE_FORMAT, Locale.getDefault())

    var subscriptions: CompositeDisposable? = null
    var articleView: ArticleView? = null

    private val stateSubject = PublishSubject.create<ArticleLoadingState>()

    override fun bind(view: ArticleView) {
        subscriptions?.dispose()
        subscriptions = CompositeDisposable()
        articleView = view

        subscriptions!!.add(stateSubject.observeOn(mainScheduler)
                .subscribe({ state ->
                    articleView?.showLoading(state == ArticleLoadingState.LOADING)
                })
        )

        subscriptions!!.add(
                interactor.get(articleId)
                        .doOnSubscribe({ stateSubject.onNext(ArticleLoadingState.LOADING) })
                        .map({ convertToArticlePresentation(it) })
                        .doOnSuccess({ stateSubject.onNext(ArticleLoadingState.FINISHED) })
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribe({ articleView?.showArticle(it) },
                                { articleView?.showError(it) })
        )
    }

    override fun unbind() {
        subscriptions?.dispose()
        articleView = null
    }

    private fun convertToArticlePresentation(pair: Pair<Article, Media>): ArticleDetailPresentation =
            ArticleDetailPresentation.from(pair.first, pair.second, dateFormatter)
}