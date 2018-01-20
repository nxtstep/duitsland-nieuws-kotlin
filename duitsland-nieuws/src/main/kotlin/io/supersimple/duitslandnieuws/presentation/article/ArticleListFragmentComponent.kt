package io.supersimple.duitslandnieuws.presentation.article

import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import io.reactivex.Scheduler
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponent
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilder
import io.supersimple.duitslandnieuws.di.fragment.FragmentModule
import io.supersimple.duitslandnieuws.di.fragment.FragmentScope
import io.supersimple.duitslandnieuws.di.qualifier.IOScheduler
import io.supersimple.duitslandnieuws.di.qualifier.MainScheduler
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor

@FragmentScope
@Subcomponent(modules = [ArticleListFragmentComponent.ArticleListModule::class])
interface ArticleListFragmentComponent : FragmentComponent<ArticleListFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<ArticleListModule, ArticleListFragmentComponent>

    @Module
    class ArticleListModule(fragment: ArticleListFragment) : FragmentModule<ArticleListFragment>(fragment) {
        @Provides
        @FragmentScope
        fun provideArticleListViewModel(interactor: ArticleInteractor,
                                        @IOScheduler ioScheduler: Scheduler,
                                        @MainScheduler mainScheduler: Scheduler) =
                ArticleListViewModel(interactor, ioScheduler, mainScheduler)
    }
}