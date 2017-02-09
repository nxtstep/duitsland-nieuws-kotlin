package io.supersimple.duitslandnieuws.di.activity

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.supersimple.duitslandnieuws.presentation.article.ArticleListActivity
import io.supersimple.duitslandnieuws.presentation.article.ArticleListComponent
import io.supersimple.duitslandnieuws.presentation.detail.ArticleDetailActivity
import io.supersimple.duitslandnieuws.presentation.detail.ArticleDetailComponent

@Module(subcomponents = arrayOf(ArticleListComponent::class, ArticleDetailComponent::class))
abstract class ActivityBinder {

    @Binds
    @IntoMap
    @ActivityKey(ArticleListActivity::class)
    abstract fun articleListActivtyComponentBuilder(impl: ArticleListComponent.Builder): ActivityComponentBuilder<*, *>

    @Binds
    @IntoMap
    @ActivityKey(ArticleDetailActivity::class)
    abstract fun articleDetailActivtyComponentBuilder(impl: ArticleDetailComponent.Builder): ActivityComponentBuilder<*, *>
}