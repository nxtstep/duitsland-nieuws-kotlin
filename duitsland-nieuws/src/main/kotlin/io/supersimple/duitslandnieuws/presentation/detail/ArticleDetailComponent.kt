package io.supersimple.duitslandnieuws.presentation.detail

import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import io.reactivex.Scheduler
import io.supersimple.duitslandnieuws.di.activity.ActivityComponent
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilder
import io.supersimple.duitslandnieuws.di.activity.ActivityModule
import io.supersimple.duitslandnieuws.di.activity.ActivityScope
import io.supersimple.duitslandnieuws.di.fragment.FragmentBinder
import io.supersimple.duitslandnieuws.di.qualifier.IOScheduler
import io.supersimple.duitslandnieuws.di.qualifier.MainScheduler
import io.supersimple.duitslandnieuws.presentation.ArticleInteractor

@ActivityScope
@Subcomponent(modules = [ArticleDetailComponent.ArticleDetailModule::class,
        FragmentBinder::class])
interface ArticleDetailComponent : ActivityComponent<ArticleDetailActivity> {
    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<ArticleDetailModule, ArticleDetailComponent>

    @Module
    class ArticleDetailModule(activity: ArticleDetailActivity) : ActivityModule<ArticleDetailActivity>(activity) {
        @ActivityScope
        @Provides
        fun provideArticlePresenter(interactor: ArticleInteractor,
                                    @IOScheduler ioScheduler: Scheduler,
                                    @MainScheduler mainScheduler: Scheduler): ArticlePresenter =
                ArticlePresenter(activity.articleId, interactor, ioScheduler, mainScheduler)
    }
}