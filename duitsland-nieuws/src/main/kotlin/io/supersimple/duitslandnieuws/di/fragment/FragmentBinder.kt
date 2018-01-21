package io.supersimple.duitslandnieuws.di.fragment

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.supersimple.duitslandnieuws.presentation.article.ArticleListFragment
import io.supersimple.duitslandnieuws.presentation.article.ArticleListFragmentComponent

@Module(subcomponents = [ArticleListFragmentComponent::class])
abstract class FragmentBinder {
    @Binds
    @IntoMap
    @FragmentKey(ArticleListFragment::class)
    abstract fun articleListFragmentBuilder(impl: ArticleListFragmentComponent.Builder): FragmentComponentBuilder<*, *>
}
