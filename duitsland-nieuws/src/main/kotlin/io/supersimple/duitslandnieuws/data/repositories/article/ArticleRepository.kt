package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Article

class ArticleRepository(private val cache: ArticleCache,
                        private val disk: ArticleDisk,
                        private val cloud: ArticleCloud) {

    /**
     * Get a Article with [id] from local resources: Cache or Disk
     *
     * @return Future for the Article
     */
    fun get(id: String): Maybe<Article> {
        return cache.get(id)
                .switchIfEmpty(disk.get(id)
                        .flatMap { cache.save(it).toMaybe() })
    }

    /**
     * Refresh/Fetch a single Article with [id] from remote Cloud
     * and update local cache and disk
     *
     * @param Future for the Article
     */
    fun fetch(id: String): Single<Article> {
        return cloud.get(id)
                .flatMap { disk.save(it) }
                .flatMap { cache.save(it) }
    }

    /**
     * Get articles from local resource: Cache or Disk
     */
    fun list(page: Int, pageSize: Int): Maybe<List<Article>> {
        return cache.list(page, pageSize)
                .switchIfEmpty(disk.list(page, pageSize)
                        .flatMap { cache.save(it).toMaybe() }
                        .switchIfEmpty(cloud.list(page, pageSize)
                                .flatMap { disk.save(it) }
                                .flatMap { cache.save(it) }
                                .toMaybe()
                        )
                )
    }

    /**
     * Get articles from remote Cloud
     * and update local cache and disk
     */
    fun refresh(pageSize: Int): Single<List<Article>> {
        return cloud.list(0, pageSize)
                .flatMap { disk.save(it) }
                .flatMap { list ->
                    cache.deleteAll()
                            .flatMap { cache.save(list) }
                }
    }

    /**
     * Save a single Article to local resources: Disk and Cache
     */
    fun save(article: Article): Single<Article> {
        return disk.save(article)
                .flatMap { cache.save(it) }
    }

    /**
     * Remove Article from local resources: Cache and Disk
     */
    fun delete(article: Article): Single<Article> {
        return disk.delete(article)
                .flatMap { cache.delete(it).toSingle() }
    }

    /**
     * Clear the mem cache
     */
    fun clearCaches(): Completable {
        return cache.deleteAll().toCompletable()
    }
}
