package io.supersimple.duitslandnieuws.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.supersimple.duitslandnieuws.databinding.ActivityArticleDetailBinding
import io.supersimple.duitslandnieuws.databinding.CardArticleBinding
import io.supersimple.duitslandnieuws.ext.activityLifecycleScope
import io.supersimple.duitslandnieuws.ext.loadKoinModules
import io.supersimple.duitslandnieuws.ext.viewBinding
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.core.scope.inject
import org.koin.dsl.module


val articleDetailActivityModule = module {
    scope<ArticleDetailActivity> {
        scoped { (articleId: String) -> ArticlePresenter(articleId, get()) }
        scoped { ArticleInteractor(get(), get()) }
    }
}

open class ArticleDetailActivity : AppCompatActivity(), ArticleView, KoinScopeComponent {
    override val scope: Scope by activityLifecycleScope()
    private val presenter: ArticlePresenter by inject(parameters = { parametersOf(articleId) })
    private val binding by viewBinding(ActivityArticleDetailBinding::inflate)

    private val articleId: String
        get() = intent.getStringExtra(EXTRA_ARTICLE_ID)!!

    init {
        loadKoinModules(articleDetailActivityModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initActivityTransitions()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewCompat.setTransitionName(binding.appBarLayout, EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION)
        ViewCompat.setTransitionName(binding.tvArticleTitle, EXTRA_ARTICLE_TITLE_VIEW_TRANSITION)

        // Fix a UI-glitch that would add bottom padding to the collapsable CoordinatorLayout
        // https://github.com/material-components/material-components-android/issues/885
        // https://issuetracker.google.com/issues/119052103
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, insets ->
            insets?.consumeSystemWindowInsets() ?: throw IllegalStateException()
        }

        supportPostponeEnterTransition()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = null
    }

    override fun onStart() {
        super.onStart()
        presenter.bind(this)
    }

    override fun onStop() {
        presenter.unbind()
        super.onStop()
    }

    override fun showError(t: Throwable) {
        Snackbar.make(binding.contentFrame, t.message as CharSequence, Snackbar.LENGTH_LONG).show()
        supportStartPostponedEnterTransition()
    }

    override fun showLoading(flag: Boolean) {
        binding.pbArticleDetail.visibility = if (flag) View.VISIBLE else View.GONE
    }

    override fun showArticle(article: ArticleDetailPresentation) {
        binding.tvArticleTitle.text = article.title
        binding.tvPhotoCaption.text = article.caption
        binding.tvArticleDate.text = article.pubDate
        binding.tvArticleText.text = article.text

        article.imageUrl?.let {
            binding.tvPhotoCaption.visibility = View.VISIBLE
            binding.imArticleHeader.visibility = View.VISIBLE
            Picasso.with(binding.imArticleHeader.context).load(it).into(binding.imArticleHeader)
        }
        if (article.imageUrl == null) {
            binding.imArticleHeader.visibility = View.GONE
            binding.tvPhotoCaption.visibility = View.GONE

        }
        supportStartPostponedEnterTransition()
    }

    private fun initActivityTransitions() {
        val transition = Explode()
        transition.excludeTarget(android.R.id.statusBarBackground, true)
        transition.duration = 200
        window.enterTransition = transition
        window.returnTransition = transition
    }

    companion object {
        val TAG = ArticleDetailActivity::class.java.simpleName
        val EXTRA_ARTICLE_ID = "$TAG.article_id"
        val EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION = "$TAG.media_transition"
        val EXTRA_ARTICLE_TITLE_VIEW_TRANSITION = "$TAG.title_transition"

        fun createIntent(context: Context, articleId: String): Intent {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(EXTRA_ARTICLE_ID, articleId)
            return intent
        }

        fun navigate(activity: AppCompatActivity, binding: CardArticleBinding, articleId: String) {
            val intent = createIntent(activity, articleId)

            val pair1 = androidx.core.util.Pair<View, String>(binding.ivArticleImage, EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION)
            val pair2 = androidx.core.util.Pair<View, String>(binding.tvArticleTitle, EXTRA_ARTICLE_TITLE_VIEW_TRANSITION)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    pair1, pair2)
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }
}
