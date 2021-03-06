package io.supersimple.duitslandnieuws.data.repositories

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

abstract class SimpleMemCache<K, V>(internal val cache: MutableMap<K, V>) {

    fun get(id: K): Maybe<V> =
            Maybe.defer { Maybe.just<V>(cache[id]) }.onErrorComplete()

    //FIXME not offset needed since all is in memory already...
    fun list(page: Int, pageSize: Int): Maybe<List<V>> =
            Maybe.defer {
                Maybe.just(cache)
                        .filter { it.isNotEmpty() && (page + 1) * pageSize <= it.size }
                        .map { it.values.toList() }
                        .map {
                            it.subList(fromIndex = page * pageSize, toIndex = (page + 1) * pageSize)
                        }
            }

    fun save(value: V): Single<V> =
            Single.defer {
                Single.just(getId(value))
                        .flatMap {
                            cache.put(it, value)
                            Single.just(value)
                        }
            }

    fun save(values: List<V>): Single<List<V>> =
            Observable.fromIterable(values)
                    .flatMapSingle { save(it) }
                    .toList()

    fun delete(id: K): Maybe<V> =
            Maybe.create { observer ->
                cache.remove(id)?.let {
                    observer.onSuccess(it)
                }
                observer.onComplete()
            }

    @JvmName("deleteObject")
    fun delete(value: V): Maybe<V> = Maybe.defer {
        Maybe.just(getId(value))
                .flatMap { delete(it) }
    }

    fun deleteAll(): Single<List<V>> =
            list(0, cache.size)
                    .flatMapObservable { Observable.fromIterable(it) }
                    .map { getId(it) }
                    .flatMapMaybe { delete(it) }
                    .toList()

    fun clear(): Unit {
        cache.clear()
    }

    abstract fun getId(value: V): K
}