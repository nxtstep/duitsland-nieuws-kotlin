package io.supersimple.duitslandnieuws.presentation.article

import dagger.Module
import dagger.Subcomponent
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponent
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilder
import io.supersimple.duitslandnieuws.di.fragment.FragmentModule
import io.supersimple.duitslandnieuws.di.fragment.FragmentScope

@FragmentScope
@Subcomponent(modules = arrayOf(ArticleListFragmentComponent.ArticleListModule::class))
interface ArticleListFragmentComponent : FragmentComponent<ArticleListFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<ArticleListModule, ArticleListFragmentComponent>

    @Module
    class ArticleListModule(fragment: ArticleListFragment) : FragmentModule<ArticleListFragment>(fragment)
}