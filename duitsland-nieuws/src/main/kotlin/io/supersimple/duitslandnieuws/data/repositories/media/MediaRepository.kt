package io.supersimple.duitslandnieuws.data.repositories.media

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Media

class MediaRepository(private val cache: MediaCache,
                      private val disk: MediaDisk,
                      private val cloud: MediaCloud) {

    fun get(id: String): Maybe<Media> =
            cache.get(id)
                    .switchIfEmpty(disk.get(id)
                            .switchIfEmpty(
                                    cloud.get(id).flatMap {
                                        disk.save(it).toMaybe()
                                    }
                            )
                            .flatMap {
                                cache.save(it).toMaybe()
                            }
                    )

    fun save(media: Media): Single<Media> =
            disk.save(media)
                    .flatMap { cache.save(it) }

    fun delete(media: Media): Single<Media> =
            disk.delete(media)
                    .flatMap { cache.delete(it).toSingle() }

    fun clearCaches(): Completable =
            cache.deleteAll()
                    .flatMapCompletable { disk.deleteAll() }
}
