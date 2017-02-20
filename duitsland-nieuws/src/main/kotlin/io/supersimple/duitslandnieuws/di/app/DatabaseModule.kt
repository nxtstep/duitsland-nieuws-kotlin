package io.supersimple.duitslandnieuws.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import io.supersimple.duitslandnieuws.data.repositories.article.Models
import io.supersimple.duitslandnieuws.di.qualifier.ApplicationContext
import javax.inject.Singleton

@Module class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KotlinReactiveEntityStore<Persistable> {
        val dataSource = DatabaseSource(context, Models.DEFAULT, ARTICLE_DATABASE_NAME, 1)
        dataSource.setTableCreationMode(TableCreationMode.DROP_CREATE)
        val entityStore = KotlinEntityDataStore<Persistable>(dataSource.configuration)
        return KotlinReactiveEntityStore(entityStore)
    }

    companion object {
        const val ARTICLE_DATABASE_NAME = "article.db"
    }
}