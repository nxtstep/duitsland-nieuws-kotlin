package io.supersimple.duitslandnieuws.data.repositories.article

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import io.supersimple.duitslandnieuws.data.models.Article

class ArticleDisk(private val store: KotlinReactiveEntityStore<Persistable>) {

    fun get(id: String): Maybe<Article> = getDAO(id).map { it.toArticle() }

    fun save(article: Article): Single<Article> = Single.just(article)
            .map { it.toDAO() }
            .flatMap { store.upsert(it) }
            .map { it.toArticle() }

    fun list(page: Int, pageSize: Int): Maybe<List<Article>> =
            store.select(ArticleDAO::class)
                    .orderBy(ArticleDAOEntity.DATE.desc())
                    .limit(pageSize)
                    .offset(page * pageSize)
                    .get()
                    .observable()
                    .map { it.toArticle() }
                    .toList()
                    .filter { it.isNotEmpty() }

    fun save(articles: List<Article>): Single<List<Article>> =
            Observable.fromIterable(articles)
                    .flatMapSingle { save(it) }
                    .toList()

    fun delete(article: Article): Single<Article> = delete(article.id)

    fun delete(id: String): Single<Article> =
            getDAO(id)
                    .flatMapSingle { deleteArticleDAO(it) }
                    .map { it.toArticle() }

    fun deleteAll(): Single<Int> =
            store.delete(ArticleDAO::class)
                    .get()
                    .single()

    private fun deleteArticleDAO(article: ArticleDAO): Single<ArticleDAO> =
            store.delete(article)
                    .toSingleDefault(article)

    private fun getDAO(id: String): Maybe<ArticleDAO> =
            store.select(ArticleDAO::class)
                    .where(ArticleDAO::id eq id)
                    .get()
                    .maybe()
}
