package io.supersimple.duitslandnieuws.presentation.article.adapter

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_article.view.*

class ArticleItemLayout : LinearLayout {
    lateinit var titleTextView: TextView
    lateinit var excerptTextView: TextView
    lateinit var pubDateTextView: TextView
    lateinit var imageView: ImageView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        titleTextView = this.tv_article_title
        excerptTextView = this.tv_article_excerpt
        imageView = this.iv_article_image
        pubDateTextView = this.tv_article_pub_date
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setOnClickListener {
            article?.let {
                listener?.onArticleClicked(it.id, position, this)
            }
        }
    }

    var listener: ArticleListAdapter.OnArticleClickListener? = null
    var article: ArticleItemPresentation? = null
        set(value) {
            field = value

            titleTextView.text = field?.title
            excerptTextView.text = field?.excerpt
            pubDateTextView.text = field?.pubDate

            field?.imageUrl?.let {
                imageView.visibility = VISIBLE
                Picasso.with(context).load(Uri.parse(it)).into(imageView)
            }
            if (field?.imageUrl == null) {
                imageView.setImageResource(0)
                imageView.visibility = GONE
            }
        }
    var position: Int = -1
}
