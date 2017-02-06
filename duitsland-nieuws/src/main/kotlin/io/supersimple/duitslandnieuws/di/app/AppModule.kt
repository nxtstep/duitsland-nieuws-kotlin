package io.supersimple.duitslandnieuws.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import io.supersimple.duitslandnieuws.application.getMetaData
import io.supersimple.duitslandnieuws.di.app.qualifier.ApplicationContext
import io.supersimple.duitslandnieuws.di.app.qualifier.BaseUrl
import javax.inject.Singleton

@Module class AppModule(val context: Context) {

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String = context.getMetaData { getString("API_BASE_URL") }

    @Provides
    @ApplicationContext
    fun provideApplicationContext(): Context = context
}