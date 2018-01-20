package io.supersimple.duitslandnieuws.application

import android.app.Activity
import android.app.Application
import android.content.Context
import io.supersimple.duitslandnieuws.di.activity.ActivityComponent
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilder
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.di.activity.ActivityModule
import io.supersimple.duitslandnieuws.di.app.AppModule
import io.supersimple.duitslandnieuws.di.app.DaggerAppComponent
import javax.inject.Inject
import javax.inject.Provider

class DuitslandNieuwsApplication : Application(), ActivityComponentBuilderProvider {

    @Inject lateinit var activityComponentBuilders:
            Map<Class<out Activity>, @JvmSuppressWildcards Provider<ActivityComponentBuilder<*, *>>>

    override fun <A : Activity, M : ActivityModule<A>, C : ActivityComponent<A>> activityComponentBuilder(activityClass: Class<out A>): ActivityComponentBuilder<M, C>? {
        @Suppress("UNCHECKED_CAST")
        return activityComponentBuilders[activityClass]?.get() as? ActivityComponentBuilder<M, C>
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