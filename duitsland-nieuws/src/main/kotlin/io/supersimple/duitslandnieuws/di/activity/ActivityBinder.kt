package io.supersimple.duitslandnieuws.di.activity

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.supersimple.duitslandnieuws.presentation.article.ArticleListActivity
import io.supersimple.duitslandnieuws.presentation.article.ArticleListComponent

@Module(subcomponents = arrayOf(ArticleListComponent::class))
abstract class ActivityBinder {

    @Binds
    @IntoMap
    @ActivityKey(ArticleListActivity::class)
    abstract fun articleListActivtyComponentBuilder(impl: ArticleListComponent.Builder): ActivityComponentBuilder<*, *>
}