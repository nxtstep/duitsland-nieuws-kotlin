package io.supersimple.duitslandnieuws.presentation.article

import dagger.Module
import dagger.Subcomponent
import io.supersimple.duitslandnieuws.di.activity.ActivityComponent
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilder
import io.supersimple.duitslandnieuws.di.activity.ActivityModule
import io.supersimple.duitslandnieuws.di.activity.ActivityScope
import io.supersimple.duitslandnieuws.di.fragment.FragmentBinder

@ActivityScope
@Subcomponent(modules = [ArticleListComponent.ArticleListModule::class,
        FragmentBinder::class])
interface ArticleListComponent : ActivityComponent<ArticleListActivity> {

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<ArticleListModule, ArticleListComponent>

    @Module
    class ArticleListModule(activity: ArticleListActivity) : ActivityModule<ArticleListActivity>(activity)
}