package io.supersimple.duitslandnieuws.presentation.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.databinding.CardArticleBinding
import io.supersimple.duitslandnieuws.databinding.FragmentArticleListBinding
import io.supersimple.duitslandnieuws.ext.fragmentLifecycleScope
import io.supersimple.duitslandnieuws.ext.loadKoinModules
import io.supersimple.duitslandnieuws.ext.viewBinding
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleListAdapter
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleLoadStateAdapter
import io.supersimple.duitslandnieuws.presentation.detail.ArticleDetailActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.dsl.module

@ExperimentalPagingApi
val articleListFragmentModule = module {
    scope<ArticleListFragment> {
        scoped {  }
    }
    viewModel { ArticleListViewModel(ArticlePagedRepository(get())) }
}

@ExperimentalPagingApi
class ArticleListFragment : Fragment(), KoinScopeComponent {
    override val scope: Scope by fragmentLifecycleScope()
    private val articleListViewModel: ArticleListViewModel by viewModel()
    private val binding by viewBinding(FragmentArticleListBinding::bind)
    @ExperimentalCoroutinesApi
    private val articleListAdapter: ArticleListAdapter by lazy {
        ArticleListAdapter(scope.get())
    }

    init {
        loadKoinModules(articleListFragmentModule)
    }

    companion object {
        val TAG = ArticleListFragment::class.java.simpleName
        fun createFragment(): ArticleListFragment = ArticleListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_article_list, container, false)
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvArticleList.setHasFixedSize(true)
        binding.rvArticleList.adapter = articleListAdapter.withLoadStateHeaderAndFooter(
            header = ArticleLoadStateAdapter(articleListAdapter),
            footer = ArticleLoadStateAdapter(articleListAdapter)
        )
        lifecycleScope.launchWhenCreated {
            articleListAdapter.loadStateFlow.collectLatest {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            articleListViewModel.articleListState.collectLatest { value ->
                        articleListAdapter.submitData(value)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener { articleListAdapter.refresh() }

        articleListAdapter.onArticleClickListener = object : ArticleListAdapter.OnArticleClickListener {
            override fun onArticleClicked(articleId: String, position: Int, view: View, binding: CardArticleBinding) {
                ArticleDetailActivity.navigate(activity as AppCompatActivity, binding, articleId)
            }
        }
    }
}