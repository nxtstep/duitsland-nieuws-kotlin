package io.supersimple.duitslandnieuws.di.session

import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleCache
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleCloud
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleDisk
import io.supersimple.duitslandnieuws.data.repositories.article.ArticleRepository
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
}