package io.supersimple.duitslandnieuws.presentation.article

interface ArticleListView {
    fun showLoadingIndicator(flag: Boolean)
    fun showEmptyState()
    fun showError(t: Throwable)
    fun showArticleListLoaded(page: Int)
}