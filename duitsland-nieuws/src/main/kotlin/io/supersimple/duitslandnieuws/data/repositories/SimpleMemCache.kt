package io.supersimple.duitslandnieuws.data.repositories

import io.reactivex.Observable

abstract class SimpleMemCache<T>(val cache: MutableMap<String, T>) {
    fun list(): Observable<List<T>> {
        return Observable.create<List<T>> { observer ->
            if (cache.isNotEmpty()) {
                observer.onNext(cache.values.toList())
            }
            observer.onComplete()
        }
    }

    fun get(id: String): Observable<T> {
        return Observable.create { observer ->
            if (cache.containsKey(id)) {
                observer.onNext(cache[id])
            }
            observer.onComplete()
        }
    }

    fun save(value: T): Observable<T> {
        return Observable.create { observer ->
            cache.put(getId(value), value)
            observer.onNext(value)
            observer.onComplete()
        }
    }

    fun save(values: List<T>): Observable<List<T>> {
        return Observable.fromArray(values)
                .flatMapIterable { it }
                .flatMap { save(it) }
                .toList()
                .filter { it.isNotEmpty() }
                .toObservable()
    }

    fun delete(id: String): Observable<T> {
        return Observable.create { observer ->
            if (cache.containsKey(id)) {
                observer.onNext(cache.remove(id))
            }
            observer.onComplete()
        }
    }

    fun delete(value: T): Observable<T> {
        return delete(getId(value))
    }

    fun deleteAll(): Observable<List<T>> {
        return list()
                .flatMapIterable { it }
                .flatMap { delete(it) }
                .toList()
                .filter { it.isNotEmpty() }
                .toObservable()
    }

    fun clear(): Unit {
        cache.clear()
    }

    abstract fun getId(value: T): String
}