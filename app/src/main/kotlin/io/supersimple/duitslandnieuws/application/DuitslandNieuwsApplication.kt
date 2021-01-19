package io.supersimple.duitslandnieuws.application

import android.app.Application
import android.util.Log
import io.reactivex.plugins.RxJavaPlugins
import io.supersimple.duitslandnieuws.BuildConfig
import io.supersimple.duitslandnieuws.module.appModule
import io.supersimple.duitslandnieuws.module.cloudModule
import io.supersimple.duitslandnieuws.module.diskModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DuitslandNieuwsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        RxJavaPlugins.setErrorHandler {
            Log.e("DuitslandNieuws", it.message ?: "Unknown")
            if (BuildConfig.DEBUG) {
                it.printStackTrace()
            }
        }

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            androidContext(this@DuitslandNieuwsApplication)

            modules(
                appModule,
                cloudModule,
                diskModule,
            )
        }
    }
}