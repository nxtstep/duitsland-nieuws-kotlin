package io.supersimple.duitslandnieuws.di.app

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.supersimple.duitslandnieuws.di.app.qualifier.IOScheduler
import io.supersimple.duitslandnieuws.di.app.qualifier.MainScheduler
import javax.inject.Singleton

@Module
class ThreadingModule {
    @Provides
    @Singleton
    @IOScheduler
    fun provideIOScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    @MainScheduler
    fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()
}
