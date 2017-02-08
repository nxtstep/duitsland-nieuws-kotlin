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

    fun list(page: Int, pageSize: Int): Maybe<List<V>> {
        return Maybe.create<List<V>> { observer ->
            if (cache.isNotEmpty()) {
                val list = cache.values.toList()
                if ((page + 1) * pageSize < list.size) {
                    observer.onSuccess(list.subList(page * pageSize, (page + 1) * pageSize))
                }
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
        return list(0, cache.size)
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