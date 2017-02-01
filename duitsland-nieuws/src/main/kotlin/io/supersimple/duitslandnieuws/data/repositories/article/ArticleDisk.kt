package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import io.supersimple.duitslandnieuws.data.models.Article

class ArticleDisk(private val store: KotlinReactiveEntityStore<Persistable>) {

    fun get(id: String): Maybe<Article> = getDAO(id).map { convertFromDb(it) }

    fun save(article: Article): Single<Article> = Single.just(article)
            .map { convertToDb(it) }
            .flatMap { store.upsert(it) }
            .map { convertFromDb(it) }

    fun list(limit: Int = 100): Maybe<List<Article>> {
        return store.select(ArticleDAO::class)
                .orderBy(ArticleDAOEntity.DATE.desc()).limit(limit)
                .get()
                .observable()
                .map { convertFromDb(it) }
                .toList()
                .filter { it.isNotEmpty() }
    }

    fun save(articles: List<Article>): Single<List<Article>> {
        return Observable.fromIterable(articles)
                .flatMapSingle { save(it) }
                .toList()
    }

    fun delete(article: Article): Single<Article> = delete(article.id)

    fun delete(id: String): Single<Article> {
        return getDAO(id)
                .toSingle()
                .flatMap { deleteArticleDAO(it) }
                .map { convertFromDb(it) }
    }

    fun deleteAll(): Single<Int> {
        return store.delete(ArticleDAO::class)
                .get()
                .single()
    }

    private fun deleteArticleDAO(article: ArticleDAO): Single<ArticleDAO> {
        return store.delete(article)
                .toSingle<ArticleDAO> { article }
                .onErrorResumeNext { t: Throwable -> Single.just(article) } // TODO: remove this line when Requery (1.1.3) merged the Void -> Void? PR
    }

    private fun getDAO(id: String): Maybe<ArticleDAO> {
        return store.select(ArticleDAO::class)
                .where(ArticleDAO::id eq id)
                .get()
                .maybe()
    }

    companion object {

        fun convertFromDb(dbArticle: ArticleDAO): Article {
            return Article(dbArticle.id,
                    dbArticle.date,
                    dbArticle.modified,
                    dbArticle.slug,
                    dbArticle.link,
                    dbArticle.title,
                    dbArticle.content,
                    dbArticle.excerpt,
                    dbArticle.author)
        }

        fun convertToDb(article: Article): ArticleDAO {
            val o = ArticleDAOEntity()
            o.setId(article.id)
            o.setDate(article.date)
            o.setModified(article.modified)
            o.setSlug(article.slug)
            o.setLink(article.link)
            o.setTitle(article.title)
            o.setContent(article.content)
            o.setExcerpt(article.excerpt)
            o.setAuthor(article.author)
            return o
        }
    }
}