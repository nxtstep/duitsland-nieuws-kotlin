package io.supersimple.duitslandnieuws.presentation.article

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilderProvider
import io.supersimple.duitslandnieuws.presentation.ComponentFragment
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleItemLayout
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleListAdapter
import io.supersimple.duitslandnieuws.presentation.detail.ArticleDetailActivity
import kotlinx.android.synthetic.main.fragment_article_list.*
import javax.inject.Inject


class ArticleListFragment : ComponentFragment(), ArticleListView {
    @Inject lateinit var articleListViewModel: ArticleListViewModel
    @Inject lateinit var articleListAdapter: ArticleListAdapter

    companion object {
        val TAG = ArticleListFragment::class.java.simpleName
        fun createFragment(): ArticleListFragment = ArticleListFragment()
    }

    override fun injectMembers(fragmentComponentBuilder: FragmentComponentBuilderProvider) {
        fragmentComponentBuilder.fragmentComponentBuilder(ArticleListFragment::class.java)!!
                .fragmentModule(ArticleListFragmentComponent.ArticleListModule(this))
                .build()
                .injectMembers(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflater?.let {
            return it.inflate(R.layout.fragment_article_list, container, false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_article_list.layoutManager = LinearLayoutManager(view?.context)
        rv_article_list.adapter = articleListAdapter

        swipe_refresh_layout.setOnRefreshListener { articleListViewModel.refresh() }

        articleListAdapter.onArticleClickListener = object : ArticleListAdapter.OnArticleClickListener {
            override fun onArticleClicked(articleId: String, position: Int, view: ArticleItemLayout) {
                ArticleDetailActivity.navigate(activity as AppCompatActivity, view, articleId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        articleListViewModel.bindView(this)
    }

    override fun onStop() {
        articleListViewModel.unbind()
        swipe_refresh_layout.isRefreshing = false
        super.onStop()
    }

    override fun showLoadingIndicator(flag: Boolean) {
        swipe_refresh_layout.isRefreshing = flag
    }

    override fun showEmptyState() {
        Snackbar.make(rv_article_list, R.string.list_empty, Toast.LENGTH_LONG).show()
    }

    override fun showError(t: Throwable) {
        swipe_refresh_layout.isRefreshing = false
        Snackbar.make(rv_article_list, t.message as CharSequence, Snackbar.LENGTH_LONG).show()
    }

    override fun showArticleListLoaded(page: Int) {
        Snackbar.make(rv_article_list, resources.getString(R.string.list_loaded_page, page),
                Snackbar.LENGTH_SHORT).show()
    }
}