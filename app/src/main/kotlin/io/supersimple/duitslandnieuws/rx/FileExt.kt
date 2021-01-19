package io.supersimple.duitslandnieuws.rx

import io.reactivex.Completable
import java.io.File
import java.io.IOException

fun File.remove(): Completable =
        Completable.create { observer ->
            if (this.delete()) {
                observer.onComplete()
            } else {
                observer.onError(IOException("Could not remove: [$this]"))
            }
        }