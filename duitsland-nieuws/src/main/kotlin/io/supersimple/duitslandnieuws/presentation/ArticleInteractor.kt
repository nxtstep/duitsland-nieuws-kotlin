package io.supersimple.duitslandnieuws.presentation

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository
import javax.inject.Inject

class ArticleInteractor @Inject constructor(val articleRepository: ArticleRepository,
                                            val mediaRepository: MediaRepository) {

    fun get(id: String): Maybe<Pair<Article, Media>> =
            articleRepository.get(id)
                    .flatMap({ mergeWithMedia(it) })

    fun list(page: Int, pageSize: Int): Observable<Pair<Article, Media>> =
            articleRepository.list(page, pageSize).toObservable()
                    .flatMapIterable({ it })
                    .flatMap({ mergeWithMedia(it).toObservable() })

    fun refresh(pageSize: Int): Observable<Pair<Article, Media>> =
            articleRepository.refresh(pageSize).toObservable()
                    .flatMapIterable { it }
                    .flatMap({ mergeWithMedia(it).toObservable() })

    private fun mergeWithMedia(article: Article): Maybe<Pair<Article, Media>> {
        return Maybe.just(article)
                .zipWith(mediaRepository.get(article.featured_media)
                        .onErrorComplete()
                        .defaultIfEmpty(Media.empty)
                        , BiFunction { t1, t2 -> Pair(t1, t2) })
    }
}