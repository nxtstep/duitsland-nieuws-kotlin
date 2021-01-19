package io.supersimple.duitslandnieuws.util

import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import io.supersimple.duitslandnieuws.module.ThreadingModule

class TestThreadingModule(val testScheduler: TestScheduler = TestScheduler()): ThreadingModule {
    override val io: Scheduler
        get() = testScheduler
    override val main: Scheduler
        get() = testScheduler
}