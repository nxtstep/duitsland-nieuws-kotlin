package io.supersimple.duitslandnieuws.presentation.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.databinding.FragmentArticleListBinding
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilderProvider
import io.supersimple.duitslandnieuws.presentation.ComponentFragment
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleItemLayout
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleListAdapter
import io.supersimple.duitslandnieuws.presentation.detail.ArticleDetailActivity
import javax.inject.Inject


class ArticleListFragment : ComponentFragment(), ArticleListView {
    @Inject lateinit var articleListViewModel: ArticleListViewModel
    @Inject lateinit var articleListAdapter: ArticleListAdapter

    private lateinit var binding: FragmentArticleListBinding

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentArticleListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            rvArticleList.layoutManager = LinearLayoutManager(view.context)
            rvArticleList.adapter = articleListAdapter

            swipeRefreshLayout.setOnRefreshListener { articleListViewModel.refresh() }
        }
        articleListAdapter.onArticleClickListener = object : ArticleListAdapter.OnArticleClickListener {
            override fun onArticleClicked(articleId: String, position: Int, view: ArticleItemLayout) {
                ArticleDetailActivity.navigate(requireNotNull(activity), view, articleId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        articleListViewModel.bindView(this)
    }

    override fun onStop() {
        articleListViewModel.unbind()
        binding.swipeRefreshLayout.isRefreshing = false
        super.onStop()
    }

    override fun showLoadingIndicator(flag: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = flag
    }

    override fun showEmptyState() {
        Snackbar.make(
            binding.rvArticleList,
            R.string.list_empty,
            BaseTransientBottomBar.LENGTH_LONG
        ).show()
    }

    override fun showError(t: Throwable) = with(binding) {
        swipeRefreshLayout.isRefreshing = false
        Snackbar.make(rvArticleList, t.message as CharSequence, Snackbar.LENGTH_LONG).show()
    }

    override fun showArticleListLoaded(page: Int) {
        Snackbar.make(
            binding.rvArticleList, resources.getString(R.string.list_loaded_page, page),
                Snackbar.LENGTH_SHORT).show()
    }
}