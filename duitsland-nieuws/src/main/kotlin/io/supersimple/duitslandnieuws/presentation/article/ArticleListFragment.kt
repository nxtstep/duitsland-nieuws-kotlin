package io.supersimple.duitslandnieuws.presentation.article

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilderProvider
import io.supersimple.duitslandnieuws.presentation.ComponentFragment
import kotlinx.android.synthetic.main.fragment_article_list.*
import javax.inject.Inject


class ArticleListFragment : ComponentFragment(), ArticleListView {
    @Inject lateinit var articleListViewModel: ArticleListViewModel
    @Inject lateinit var articleListAdapter: ArticleListAdapter

    override fun injectMembers(fragmentComponentBuilder: FragmentComponentBuilderProvider) {
        (fragmentComponentBuilder[ArticleListFragment::class.java] as ArticleListFragmentComponent.Builder) //TODO find a way to do this without casting
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
    }

    override fun onStart() {
        super.onStart()
        articleListViewModel.bindView(this)
    }

    override fun onStop() {
        articleListViewModel.unbind()
        super.onStop()
    }

    override fun showLoadingIndicator(flag: Boolean) {
        Toast.makeText(context, "Loading $flag", Toast.LENGTH_LONG).show()
    }

    override fun showEmptyState() {
        Toast.makeText(context, "Empty", Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
    }

    override fun showArticleListLoaded(page: Int) {
        Toast.makeText(context, "Loaded page: $page", Toast.LENGTH_LONG).show()
    }
}