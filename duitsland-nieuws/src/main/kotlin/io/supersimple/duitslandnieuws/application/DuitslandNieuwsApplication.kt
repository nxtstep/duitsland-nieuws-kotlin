package io.supersimple.duitslandnieuws.application

import android.app.Activity
import android.app.Application
import android.content.Context
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilder
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.di.app.AppModule
import io.supersimple.duitslandnieuws.di.app.DaggerAppComponent
import javax.inject.Inject
import javax.inject.Provider

class DuitslandNieuwsApplication : Application(), ActivityComponentBuilderProvider {

    @Inject lateinit var activityComponentBuilders:
            Map<Class<out Activity>, @JvmSuppressWildcards Provider<ActivityComponentBuilder<*, *>>>

    override fun get(activityClass: Class<out Activity>): ActivityComponentBuilder<*, *> {
        return activityComponentBuilders[activityClass]!!.get()
    }

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
                .inject(this)
    }

    companion object {
        operator fun get(context: Context): ActivityComponentBuilderProvider =
                context.applicationContext as ActivityComponentBuilderProvider
    }
}