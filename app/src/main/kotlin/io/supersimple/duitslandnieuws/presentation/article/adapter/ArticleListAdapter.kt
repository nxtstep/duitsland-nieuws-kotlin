package io.supersimple.duitslandnieuws.presentation.article.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.models.MediaItem
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepository
import io.supersimple.duitslandnieuws.databinding.CardArticleBinding
import io.supersimple.duitslandnieuws.module.ThreadingModule
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleListAdapter(
    private val mediaRepository: MediaRepository,
    private val dateFormatter: SimpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()),
) : PagingDataAdapter<Article, ArticleListAdapter.ViewHolder>(listDiffCallback) {

    interface OnArticleClickListener {
        fun onArticleClicked(articleId: String, position: Int, view: View, binding: CardArticleBinding)
    }

    var onArticleClickListener: OnArticleClickListener? = null

    companion object {
        const val DATE_FORMAT = "H:mm - d MMMM yyyy"

        private val listDiffCallback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = getItem(position)
        holder.onBind(article, dateFormatter, position, onArticleClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent, mediaRepository)

    class ViewHolder(
        private val view: View,
        private val mediaRepository: MediaRepository,
        private val schedulers: ThreadingModule = ThreadingModule.default,
    ) : RecyclerView.ViewHolder(view) {
        private val binding = CardArticleBinding.bind(view)
        private var mediaSubscription: Disposable? = null

        fun onBind(
            article: Article?,
            dateFormatter: SimpleDateFormat,
            position: Int,
            listener: OnArticleClickListener?
        ) {
            binding.article = article?.let { ArticleItemPresentation.from(article, dateFormatter) }

            binding.ivArticleImage.visibility = View.INVISIBLE
            binding.ivArticleImage.setImageResource(0)
            article?.let {
                mediaSubscription = mediaRepository.mediaFor(article)
                    .subscribeOn(schedulers.io)
                    .observeOn(schedulers.main)
                    .subscribeBy(onSuccess = { media ->
                        binding.ivArticleImage.visibility = View.VISIBLE
                        val url = media.listImage()
                        Picasso.with(view.context).load(Uri.parse(url)).into(binding.ivArticleImage)
                    }, onError = {
                        binding.ivArticleImage.visibility = View.GONE

                    })
            } ?: run {
                mediaSubscription = null
                binding.ivArticleImage.visibility = View.GONE
            }

            view.setOnClickListener {
                article?.let { article ->
                    listener?.onArticleClicked(article.id, position, it, binding)
                }
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                repository: MediaRepository,
            ): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.card_article, parent, false)
                return ViewHolder(view, repository)
            }
        }
    }
}

fun MediaRepository.mediaFor(article: Article): Maybe<Media> =
    get(article.featured_media)

fun Media.listImage() = media_details.sizes[MediaItem.IMAGE_THUMBNAIL]?.source_url