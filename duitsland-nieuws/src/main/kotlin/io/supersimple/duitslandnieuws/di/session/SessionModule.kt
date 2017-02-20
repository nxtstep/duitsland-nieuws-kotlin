package io.supersimple.duitslandnieuws.di.session

import android.content.Context
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.api.MediaEndpoint
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleCache
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleCloud
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleDisk
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
import io.supersimple.duitslandnieuws.data.repositories.media.MediaCache
import io.supersimple.duitslandnieuws.data.repositories.media.MediaCloud
import io.supersimple.duitslandnieuws.data.repositories.media.MediaDisk
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository
import io.supersimple.duitslandnieuws.di.qualifier.ApplicationContext
import javax.inject.Singleton

@Module class SessionModule {
    @Provides
    @Singleton
    fun provideArticleRepository(database: KotlinReactiveEntityStore<Persistable>,
                                 service: ArticleEndpoint): ArticleRepository {
        val cache = ArticleCache()
        val disk = ArticleDisk(database)
        val cloud = ArticleCloud(service)
        return ArticleRepository(cache, disk, cloud)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(@ApplicationContext context: Context, service: MediaEndpoint): MediaRepository {
        val cache = MediaCache()
        val disk = MediaDisk(context.filesDir)
        val cloud = MediaCloud(service)
        return MediaRepository(cache, disk, cloud)
    }
}