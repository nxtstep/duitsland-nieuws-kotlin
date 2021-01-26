package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.supersimple.duitslandnieuws.data.models.Article
import java.util.Date

class ArticleRepository(
    private val cache: ArticleCache,
    private val disk: ArticleDisk,
    private val cloud: ArticleCloud
) {
    /**
     * Subject that emits when local cache and/or disk have invalidated
     */
    val invalidationSubject = PublishSubject.create<Unit>()

    /**
     * Get a Article with [id] from local resources: Cache or Disk
     *
     * @return Maybe<Article>
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
     * @param Maybe<Article>
     */
    fun fetch(id: String): Maybe<Article> {
        return cloud.get(id)
                .flatMap { disk.save(it).toMaybe() }
                .flatMap { cache.save(it).toMaybe() }
    }

    /**
     * Get articles from local resource: Cache or Disk
     */
    fun list(page: Int, pageSize: Int): Single<List<Article>> {
        return cache.list(page, pageSize)
                .switchIfEmpty(disk.list(page, pageSize)
                        .flatMap { cache.save(it).toMaybe() }
                )
                .toSingle(emptyList())
    }

    /**
     * Load a page from cloud and store it in cache and disk
     */
    fun load(page: Int, pageSize: Int, after: Date? = null, before: Date? = null): Single<List<Article>> =
        cloud.list(page, pageSize, after = after, before = before)
            .flatMap { disk.save(it) }
            .flatMap { cache.save(it) }
            .doOnSuccess { invalidationSubject.onNext(Unit) }

    /**
     * Get articles from remote Cloud
     * and update local cache and disk
     */
    fun refresh(pageSize: Int): Single<List<Article>> {
        return cloud.list(1, pageSize)
                .flatMap { list ->
                    disk.deleteAll()
                        .flatMap { disk.save(list) }
                }
                .flatMap { list ->
                    cache.clear()
                        .andThen(cache.save(list))
                }
                .doOnSuccess {
                    invalidationSubject.onNext(Unit)
                }
    }

    /**
     * Save a single Article to local resources: Disk and Cache
     */
    fun save(article: Article): Single<Article> {
        return disk.save(article)
                .flatMap { cache.save(it) }
                .doOnSuccess { invalidationSubject.onNext(Unit) }
    }

    /**
     * Remove Article from local resources: Cache and Disk
     */
    fun delete(article: Article): Single<Article> {
        return disk.delete(article)
                .flatMap { cache.delete(it).toSingle() }
                .doOnSuccess { invalidationSubject.onNext(Unit) }
    }

    /**
     * Clear the mem cache
     */
    fun clearCaches(): Completable {
        return cache.clear()
            .doOnComplete { invalidationSubject.onNext(Unit) }
    }
}
