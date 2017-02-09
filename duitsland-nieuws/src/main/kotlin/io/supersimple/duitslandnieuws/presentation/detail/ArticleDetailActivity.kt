package io.supersimple.duitslandnieuws.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.squareup.picasso.Picasso
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.presentation.ComponentActivity
import kotlinx.android.synthetic.main.activity_article_detail.*
import javax.inject.Inject

open class ArticleDetailActivity : ComponentActivity(), ArticleView {

    @Inject lateinit var presenter: ArticlePresenter

    val articleId: String
        get() = intent.getStringExtra(EXTRA_ARTICLE_ID)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = null
    }

    override fun injectMembers(activityComponentBuilder: ActivityComponentBuilderProvider) {
        (activityComponentBuilder[ArticleDetailActivity::class.java] as ArticleDetailComponent.Builder) //TODO find a way to do this without casting
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
    }

    override fun showLoading(flag: Boolean) {
        pb_article_detail.visibility = if (flag === true) View.VISIBLE else View.GONE
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
    }

    companion object {
        val TAG = ArticleDetailActivity::class.java.simpleName
        val EXTRA_ARTICLE_ID = TAG + ".article_id"

        fun createIntent(context: Context, articleId: String): Intent {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(EXTRA_ARTICLE_ID, articleId)
            return intent
        }
    }
}
