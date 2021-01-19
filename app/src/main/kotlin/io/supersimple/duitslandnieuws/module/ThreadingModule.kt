package io.supersimple.duitslandnieuws.module

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface ThreadingModule {
    companion object {
        val default: ThreadingModule = object: ThreadingModule {
            override val io: Scheduler
                get() = Schedulers.io()
            override val main: Scheduler
                get() = AndroidSchedulers.mainThread()
        }
    }

    val io: Scheduler
    val main: Scheduler
}
