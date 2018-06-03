package io.supersimple.duitslandnieuws.data.repositories.article

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.requery.Persistable
import io.requery.kotlin.Deletion
import io.requery.kotlin.Limit
import io.requery.kotlin.Offset
import io.requery.kotlin.Selection
import io.requery.kotlin.WhereAndOr
import io.requery.query.Condition
import io.requery.query.OrderingExpression
import io.requery.query.Return
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.reactivex.ReactiveResult
import io.requery.reactivex.ReactiveScalar
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class ArticleDiskTest {
    companion object {

        private fun createTestArticle(id: String,
                                      date: Date = Date(),
                                      modified: Date = Date(),
                                      slug: String = "Super slug",
                                      link: String = "http://www.link.com",
                                      title: RenderableText = RenderableText("Title rendering", false),
                                      content: RenderableText = RenderableText("Content rendering", true),
                                      excerpt: RenderableText = RenderableText("Excerpt rendering", true),
                                      author: String = "Author",
                                      media: String = "media-id"): Article =
                Article(id, date, modified, slug, link, title, content, excerpt, author, media)
    }

    private lateinit var dataStore: KotlinReactiveEntityStore<Persistable>

    @Test
    fun testGet() {
        // Given
        val articleId = "test-id-1"
        val expectedArticle = createTestArticle(articleId)

        val result: ReactiveResult<ArticleDAO> = mock {
            on { maybe() } doReturn Maybe.just(expectedArticle.toDAO())
        }

        val where: WhereAndOr<ReactiveResult<ArticleDAO>> = mock {
            on { get() } doReturn result
        }

        val selection: Selection<ReactiveResult<ArticleDAO>> = mock {
            on { where<String>(condition = any()) }.thenReturn(where)
        }

        dataStore = mock {
            on { select<ArticleDAO>(type = any()) } doReturn selection
        }

        // Given
        val disk = ArticleDisk(dataStore)
        disk.get(articleId)
                .test()
                .assertValueCount(1)
                .assertResult(expectedArticle)

        // Then
        verify(dataStore).select(eq(ArticleDAO::class))
        val captor = argumentCaptor<Condition<String, *>>()
        verify(selection).where(captor.capture())
        assertEquals(articleId, captor.firstValue.rightOperand)
    }

    @Test
    fun testSave() {
        val article = createTestArticle("test-id-1")
        val articleDAO = article.toDAO()

        dataStore = mock {
            on { upsert(any<ArticleDAO>()) }.thenAnswer { Single.just(it.arguments[0]) }
        }

        val disk = ArticleDisk(dataStore)
        disk.save(article)
                .test()
                .assertResult(article)

        verify(dataStore).upsert(entity = eq(articleDAO))
    }

    @Test
    fun testList() {
        // Given
        val article = createTestArticle("test-list-id")
        val reactiveResult = mock<ReactiveResult<ArticleDAO>> {
            on { observable() } doReturn Observable.just(article.toDAO())
        }
        val result: Return<ReactiveResult<ArticleDAO>> = mock {
            on { get() } doReturn reactiveResult
        }
        val offsetExpression = mock<Offset<ReactiveResult<ArticleDAO>>> {
            on { offset(any()) } doReturn result
        }
        val orderByExpression = mock<Limit<ReactiveResult<ArticleDAO>>> {
            on { limit(any()) }.thenReturn(offsetExpression)
        }
        val selection = mock<Selection<ReactiveResult<ArticleDAO>>> {
            on { orderBy<Date>(any()) } doReturn orderByExpression
        }

        // When
        dataStore = mock {
            on { select<ArticleDAO>(type = any()) } doReturn selection
        }

        val disk = ArticleDisk(dataStore)
        disk.list(0, 10)
                .test()
                .assertResult(listOf(article))

        // Then
        verify(dataStore).select(type = eq(ArticleDAO::class))
        val captor = argumentCaptor<OrderingExpression<Date>>()
        verify(selection).orderBy(captor.capture())
        assertEquals(ArticleDAOEntity.DATE.desc().order, captor.firstValue.order)
        assertEquals(ArticleDAOEntity.DATE.desc().innerExpression.name, captor.firstValue.innerExpression.name)
        verify(orderByExpression).limit(eq(10))
        verify(offsetExpression).offset(eq(0))
        verify(result).get()
        verify(reactiveResult).observable()
    }

    @Test
    fun testBulkSave() {
        val object1 = createTestArticle("test-id-1")
        val object2 = createTestArticle("test-id-2")
        val object3 = createTestArticle("test-id-3")

        dataStore = mock {
            on { upsert(any<ArticleDAO>()) }.thenAnswer { Single.just(it.arguments[0]) }
        }

        val disk = ArticleDisk(dataStore)
        disk.save(listOf(object1, object2, object3))
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(1)

        verify(dataStore).upsert(entity = eq(object1.toDAO()))
        verify(dataStore).upsert(entity = eq(object2.toDAO()))
        verify(dataStore).upsert(entity = eq(object3.toDAO()))
    }

    @Test
    fun testDelete() {
        val articleId = "test-id-1"
        val expectedArticle = createTestArticle(articleId)

        val result: ReactiveResult<ArticleDAO> = mock {
            on { maybe() } doReturn Maybe.just(expectedArticle.toDAO())
        }

        val where: WhereAndOr<ReactiveResult<ArticleDAO>> = mock {
            on { get() } doReturn result
        }

        val selection: Selection<ReactiveResult<ArticleDAO>> = mock {
            on { where<String>(condition = any()) }.thenReturn(where)
        }

        dataStore = mock {
            on { delete<ArticleDAO>(entity = any()) } doReturn Completable.complete()
            on { select<ArticleDAO>(any()) } doReturn selection
        }

        val disk = ArticleDisk(dataStore)
        disk.delete(articleId)
                .test()
                .assertResult(expectedArticle)

        verify(dataStore).select(eq(ArticleDAO::class))
        val captor = argumentCaptor<Condition<String, *>>()
        verify(selection).where(captor.capture())
        assertEquals(articleId, captor.firstValue.rightOperand)
        verify(dataStore).delete(eq(expectedArticle.toDAO()))
    }

    @Test
    fun testDeleteArticle() {
        val articleId = "test-id-1"
        val article = createTestArticle(articleId)

        val result: ReactiveResult<ArticleDAO> = mock {
            on { maybe() } doReturn Maybe.just(article.toDAO())
        }

        val where: WhereAndOr<ReactiveResult<ArticleDAO>> = mock {
            on { get() } doReturn result
        }

        val selection: Selection<ReactiveResult<ArticleDAO>> = mock {
            on { where<String>(condition = any()) }.thenReturn(where)
        }

        dataStore = mock {
            on { delete<ArticleDAO>(entity = any()) } doReturn Completable.complete()
            on { select<ArticleDAO>(any()) } doReturn selection
        }

        val disk = ArticleDisk(dataStore)
        disk.delete(article)
                .test()
                .assertResult(article)

        verify(dataStore).select(eq(ArticleDAO::class))
        val captor = argumentCaptor<Condition<String, *>>()
        verify(selection).where(captor.capture())
        assertEquals(articleId, captor.firstValue.rightOperand)
        verify(dataStore).delete(eq(article.toDAO()))
    }

    @Test
    fun testDeleteAll() {
        val scalar = mock<ReactiveScalar<Int>> {
            on { single() } doReturn Single.just(3)
        }

        val deletion = mock<Deletion<ReactiveScalar<Int>>> {
            on { get() } doReturn scalar
        }

        dataStore = mock {
            on { delete<ArticleDAO>(type = any()) } doReturn deletion
        }

        val disk = ArticleDisk(dataStore)
        disk.deleteAll()
                .test()
                .assertResult(3)

        verify(dataStore).delete(eq(ArticleDAO::class))
    }

    @Test
    fun articleToDAO() {
        val article = createTestArticle("test-1")
        assertEquals(article, article.toDAO().toArticle())
    }
}