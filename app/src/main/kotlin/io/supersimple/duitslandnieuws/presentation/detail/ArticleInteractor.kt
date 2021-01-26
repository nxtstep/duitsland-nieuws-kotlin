package io.supersimple.duitslandnieuws.presentation.detail

import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository

class ArticleInteractor constructor(
    private val articleRepository: ArticleRepository,
    private val mediaRepository: MediaRepository
) {

    fun get(id: String): Maybe<Pair<Article, Media>> =
            articleRepository.get(id)
                    .flatMap { mergeWithMedia(it) }

    private fun mergeWithMedia(article: Article): Maybe<Pair<Article, Media>> =
            Maybe.just(article)
                    .zipWith(mediaRepository.get(article.featured_media)
                            .onErrorComplete()
                            .defaultIfEmpty(Media.empty)
                            , BiFunction { t1, t2 -> Pair(t1, t2) })
}