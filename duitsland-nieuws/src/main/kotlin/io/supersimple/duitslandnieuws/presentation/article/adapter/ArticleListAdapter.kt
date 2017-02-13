package io.supersimple.duitslandnieuws.presentation.article.adapter

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.presentation.article.ArticleListViewModel
import javax.inject.Inject


class ArticleListAdapter @Inject constructor(articleListViewModel: ArticleListViewModel) : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {
    private val articleListViewModel = articleListViewModel

    interface OnArticleClickListener {
        fun onArticleClicked(articleId: String, position: Int, view: ArticleItemLayout): Unit
    }

    var onArticleClickListener: OnArticleClickListener? = null

    val listener = object : ObservableList.OnListChangedCallback<ObservableArrayList<Article>>() {
        override fun onItemRangeChanged(observableList: ObservableArrayList<Article>?, positionStart: Int, itemCount: Int) =
                notifyItemRangeChanged(positionStart, itemCount)

        override fun onChanged(observableList: ObservableArrayList<Article>?) =
                notifyDataSetChanged()

        override fun onItemRangeRemoved(observableList: ObservableArrayList<Article>?, positionStart: Int, itemCount: Int) =
                notifyItemRangeRemoved(positionStart, itemCount)

        override fun onItemRangeMoved(observableList: ObservableArrayList<Article>?, i1: Int, i2: Int, i3: Int) {}

        override fun onItemRangeInserted(observableList: ObservableArrayList<Article>?, positionStart: Int, itemCount: Int) =
                notifyItemRangeInserted(positionStart, itemCount)

    }

    init {
        articleListViewModel.addOnListChangedCallback(listener)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val article = articleListViewModel[position]
        holder?.onBind(article, position, onArticleClickListener)

        // Check for load more
        if (itemCount - position < THRESHOLD_LOAD_MORE) {
            articleListViewModel.loadNextPage()
        }
    }

    override fun getItemCount(): Int = articleListViewModel.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.card_article, parent, false))

    companion object {
        const val THRESHOLD_LOAD_MORE = 2
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: ArticleItemLayout = view as ArticleItemLayout

        fun onBind(article: ArticleItemPresentation, position: Int, listener: OnArticleClickListener?) {
            card.article = article
            card.position = position
            card.listener = listener
        }
    }
}
