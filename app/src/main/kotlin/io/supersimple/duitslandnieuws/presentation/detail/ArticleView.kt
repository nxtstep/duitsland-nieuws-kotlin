package io.supersimple.duitslandnieuws.presentation.detail

interface ArticleView {
    fun showError(t: Throwable)

    fun showLoading(flag: Boolean)

    fun showArticle(article: ArticleDetailPresentation)
}