package io.supersimple.duitslandnieuws.presentation.article

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.data.models.Article
import kotlinx.android.synthetic.main.item_article.view.*


class ArticleListAdapter(private val articleListViewModel: ArticleListViewModel) : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {

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
        val article = articleListViewModel.get(position)
        holder?.onBind(article, position)
    }

    override fun getItemCount(): Int = articleListViewModel.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_article, parent, false))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.tv_article_title
        val excerptTextView: TextView = view.tv_article_excerpt

        fun onBind(article: Article, position: Int) {
            titleTextView.text = article.title.rendered
            excerptTextView.text = article.excerpt.rendered
        }
    }
}
