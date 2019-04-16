package io.supersimple.duitslandnieuws.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.transition.Explode
import android.view.View
import com.squareup.picasso.Picasso
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.presentation.ComponentActivity
import io.supersimple.duitslandnieuws.presentation.article.adapter.ArticleItemLayout
import kotlinx.android.synthetic.main.activity_article_detail.app_bar_layout
import kotlinx.android.synthetic.main.activity_article_detail.content_frame
import kotlinx.android.synthetic.main.activity_article_detail.im_article_header
import kotlinx.android.synthetic.main.activity_article_detail.pb_article_detail
import kotlinx.android.synthetic.main.activity_article_detail.toolbar
import kotlinx.android.synthetic.main.activity_article_detail.tv_article_date
import kotlinx.android.synthetic.main.activity_article_detail.tv_article_text
import kotlinx.android.synthetic.main.activity_article_detail.tv_article_title
import kotlinx.android.synthetic.main.activity_article_detail.tv_photo_caption
import javax.inject.Inject
import android.support.v4.util.Pair as AndroidPair


open class ArticleDetailActivity : ComponentActivity(), ArticleView {

    @Inject lateinit var presenter: ArticlePresenter

    val articleId: String
        get() = intent.getStringExtra(EXTRA_ARTICLE_ID)


    override fun onCreate(savedInstanceState: Bundle?) {
        initActivityTransitions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        ViewCompat.setTransitionName(app_bar_layout, EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION)
        ViewCompat.setTransitionName(tv_article_title, EXTRA_ARTICLE_TITLE_VIEW_TRANSITION)
        supportPostponeEnterTransition()

        setSupportActionBar(toolbar)
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
        Snackbar.make(content_frame, t.message as CharSequence, Snackbar.LENGTH_LONG).show()
        supportStartPostponedEnterTransition()
    }

    override fun showLoading(flag: Boolean) {
        pb_article_detail.visibility = if (flag == true) View.VISIBLE else View.GONE
    }

    override fun showArticle(article: ArticleDetailPresentation) {
        tv_article_title.text = article.title
        tv_photo_caption.text = article.caption
        tv_article_date.text = article.pubDate
        tv_article_text.text = article.text

        article.imageUrl?.let {
            tv_photo_caption.visibility = View.VISIBLE
            im_article_header.visibility = View.VISIBLE
            Picasso.with(im_article_header.context).load(it).into(im_article_header)
        }
        if (article.imageUrl == null) {
            im_article_header.visibility = View.GONE
            tv_photo_caption.visibility = View.GONE

        }
        supportStartPostponedEnterTransition()
    }

    private fun initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val transition = Explode()
            transition.excludeTarget(android.R.id.statusBarBackground, true)
            transition.duration = 200
            window.enterTransition = transition
            window.returnTransition = transition
        }
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

        fun navigate(activity: AppCompatActivity, origin: ArticleItemLayout, articleId: String) {
            val intent = createIntent(activity, articleId)

            val pair1 = AndroidPair<View, String>(origin.imageView, EXTRA_ARTICLE_IMAGE_VIEW_TRANSITION)
            val pair2 = AndroidPair<View, String>(origin.titleTextView, EXTRA_ARTICLE_TITLE_VIEW_TRANSITION)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    pair1, pair2)
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }
}
