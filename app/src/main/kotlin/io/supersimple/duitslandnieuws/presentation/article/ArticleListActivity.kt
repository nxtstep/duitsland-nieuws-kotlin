package io.supersimple.duitslandnieuws.presentation.article

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.ExperimentalPagingApi
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.databinding.ActivityArticleListBinding
import io.supersimple.duitslandnieuws.ext.activityLifecycleScope
import io.supersimple.duitslandnieuws.ext.loadKoinModules
import io.supersimple.duitslandnieuws.ext.viewBinding
import org.koin.core.scope.KoinScopeComponent
import org.koin.dsl.module

val articleListActivityModule = module {
    scope<ArticleListActivity> {}
}

class ArticleListActivity : AppCompatActivity(), KoinScopeComponent {
    override val scope by activityLifecycleScope()
    private val binding by viewBinding(ActivityArticleListBinding::inflate)

    init {
        loadKoinModules(articleListActivityModule)
    }

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container_main, ArticleListFragment.createFragment(), ArticleListFragment.TAG)
                    .commit()
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
                Intent(context, ArticleListActivity::class.java)
    }
}
