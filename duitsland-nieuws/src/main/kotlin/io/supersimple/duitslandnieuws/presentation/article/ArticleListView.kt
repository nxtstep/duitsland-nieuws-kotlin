package io.supersimple.duitslandnieuws.presentation.article

interface ArticleListView {
    fun showLoadingIndicator(flag: Boolean): Unit

    fun showEmptyState(): Unit

    fun showError(t: Throwable): Unit

    fun showArticleListLoaded(page: Int): Unit
}