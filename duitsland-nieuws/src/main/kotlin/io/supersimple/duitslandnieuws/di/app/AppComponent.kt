package io.supersimple.duitslandnieuws.di.app

import dagger.Component
import io.supersimple.duitslandnieuws.application.DuitslandNieuwsApplication
import io.supersimple.duitslandnieuws.di.activity.ActivityBinder
import io.supersimple.duitslandnieuws.di.session.SessionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class,
        ThreadingModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        ActivityBinder::class,
        SessionModule::class))
interface AppComponent {

    fun inject(application: DuitslandNieuwsApplication): DuitslandNieuwsApplication
}