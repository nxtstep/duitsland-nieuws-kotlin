package io.supersimple.duitslandnieuws.di.activity

import android.app.Activity
import dagger.Module
import dagger.Provides

@Module
abstract class ActivityModule<out A: Activity>(protected val activity: A) {

    @Provides
    @ActivityScope
    fun provideActivity(): A {
        return activity
    }
}
