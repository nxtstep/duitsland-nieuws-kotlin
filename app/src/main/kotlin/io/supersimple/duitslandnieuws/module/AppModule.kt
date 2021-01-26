package io.supersimple.duitslandnieuws.module

import android.content.Context
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import io.supersimple.duitslandnieuws.application.getMetaData
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleCache
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleCloud
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleDisk
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.data.repositories.article.Models
import io.supersimple.duitslandnieuws.data.repositories.media.MediaCache
import io.supersimple.duitslandnieuws.data.repositories.media.MediaCloud
import io.supersimple.duitslandnieuws.data.repositories.media.MediaDisk
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class KoinKonstants {
    BaseUrl,
    DatabaseName,
    FilesDir
}

val appModule = module {
    single(named(KoinKonstants.BaseUrl)) { androidContext().baseUrl() }
    single { ArticleRepository(ArticleCache(), get(), get()) }
    single { MediaRepository(MediaCache(), get(), get()) }
    single(named(KoinKonstants.DatabaseName)) { androidContext().articleDatabaseName() }
}

val cloudModule = module {
    factory { ArticleCloud(NetworkModule.default.articleEndpoint(baseUrl = get(named(KoinKonstants.BaseUrl)))) }
    factory { MediaCloud(NetworkModule.default.mediaEndpoint(baseUrl = get(named(KoinKonstants.BaseUrl)))) }
}

val diskModule = module {
    single {
        val dataSource = DatabaseSource(androidContext(), Models.DEFAULT, get(named(KoinKonstants.DatabaseName)), 1)
        dataSource.setTableCreationMode(TableCreationMode.DROP_CREATE)
        val entityStore = KotlinEntityDataStore<Persistable>(dataSource.configuration)
        return@single KotlinReactiveEntityStore(entityStore)
    }

    factory { ArticleDisk(get()) }
    factory { MediaDisk(get(named(KoinKonstants.FilesDir))) }

    single(named(KoinKonstants.FilesDir)) { androidContext().filesDir }
}

fun Context.baseUrl(): String =
    getMetaData {
        getString("API_BASE_URL") ?: throw IllegalStateException("API_BASE_URL is not configured")
    }

fun Context.articleDatabaseName(): String =
    getMetaData {
        getString("ARTICLE_DATABASE_NAME")
            ?: throw IllegalStateException("ARTICLE_DATABASE_NAME is not configured")
    }