package io.supersimple.duitslandnieuws.data.repositories

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

abstract class SimpleMemCache<K, V>(internal val cache: MutableMap<K, V>) {

    fun get(id: K): Maybe<V> {
        return Maybe.create { observer ->
            if (cache.containsKey(id)) {
                observer.onSuccess(cache[id])
            }
            observer.onComplete()
        }
    }

    fun list(): Maybe<List<V>> {
        return Maybe.create<List<V>> { observer ->
            if (cache.isNotEmpty()) {
                observer.onSuccess(cache.values.toList())
            }
            observer.onComplete()
        }
    }

    fun save(value: V): Single<V> {
        return Single.create { observer ->
            cache.put(getId(value), value)
            observer.onSuccess(value)
        }
    }

    fun save(values: List<V>): Single<List<V>> {
        return Observable.fromIterable(values)
                .flatMapSingle { save(it) }
                .toList()
    }

    fun delete(id: K): Maybe<V> {
        return Maybe.create { observer ->
            if (cache.containsKey(id)) {
                observer.onSuccess(cache.remove(id))
            }
            observer.onComplete()
        }
    }

    @JvmName("deleteObject")
    fun delete(value: V): Maybe<V> {
        return delete(getId(value))
    }

    fun deleteAll(): Single<List<V>> {
        return list()
                .flatMapObservable { Observable.fromIterable(it) }
                .map { getId(it) }
                .flatMapMaybe { delete(it) }
                .toList()
    }

    fun clear(): Unit {
        cache.clear()
    }

    abstract fun getId(value: V): K
}