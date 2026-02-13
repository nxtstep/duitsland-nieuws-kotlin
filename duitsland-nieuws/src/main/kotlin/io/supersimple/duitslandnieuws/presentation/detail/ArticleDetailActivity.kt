package io.supersimple.duitslandnieuws.presentation.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.supersimple.duitslandnieuws.databinding.ActivityArticleDetailBinding
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.presentation.ComponentActivity
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleItemLayout
import javax.inject.Inject


open class ArticleDetailActivity : ComponentActivity(), ArticleView {

    @Inject
    lateinit var presenter: ArticlePresenter

    private lateinit var binding: ActivityArticleDetailBinding
    val articleId: String
        get() = requireNotNull(intent.getStringExtra(EXTRA_ARTICLE_ID))


    override fun onCreate(savedInstanceState: Bundle?) {
        initActivityTransitions()
        super.onCreate(savedInstanceState)

        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            ViewCompat.setTransitionName(appBarLayout, EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION)
            ViewCompat.setTransitionName(tvArticleTitle, EXTRA_ARTICLE_TITLE_VIEW_TRANSITION)
            supportPostponeEnterTransition()

            setSupportActionBar(toolbar)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = null
    }

    override fun injectMembers(activityComponentBuilder: ActivityComponentBuilderProvider) {
        activityComponentBuilder.activityComponentBuilder(ArticleDetailActivity::class.java)!!
            .activityModule(ArticleDetailComponent.ArticleDetailModule(this))
            .build()
            .injectMembers(this)
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

    override fun showArticle(article: ArticleDetailPresentation) = with(binding) {
        tvArticleTitle.text = article.title
        tvPhotoCaption.text = article.caption
        tvArticleDate.text = article.pubDate
        tvArticleText.text = article.text

        article.imageUrl?.let {
            tvPhotoCaption.visibility = View.VISIBLE
            imArticleHeader.visibility = View.VISIBLE
            Picasso.with(imArticleHeader.context).load(it).into(imArticleHeader)
        }
        if (article.imageUrl == null) {
            imArticleHeader.visibility = View.GONE
            tvPhotoCaption.visibility = View.GONE

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
        val EXTRA_ARTICLE_ID = TAG + ".article_id"
        val EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION = TAG + ".media_transition"
        val EXTRA_ARTICLE_TITLE_VIEW_TRANSITION = TAG + ".title_transition"

        fun createIntent(context: Context, articleId: String): Intent {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(EXTRA_ARTICLE_ID, articleId)
            return intent
        }

        fun navigate(activity: Activity, origin: ArticleItemLayout, articleId: String) {
            val intent = createIntent(activity, articleId)

            val pair1 = Pair<View, String>(origin.imageView, EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION)
            val pair2 =
                Pair<View, String>(origin.titleTextView, EXTRA_ARTICLE_TITLE_VIEW_TRANSITION)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                pair1, pair2
            )
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }
}
