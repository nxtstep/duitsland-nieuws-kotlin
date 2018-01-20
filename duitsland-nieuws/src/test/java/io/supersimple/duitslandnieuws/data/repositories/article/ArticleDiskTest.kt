package io.supersimple.duitslandnieuws.data.repositories.article

import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Arrays
import java.util.Calendar
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ArticleDiskTest {
    companion object {
        val TEST_ARTICLE_DATABASE = "test_article.db"

        fun insertTestData(store: KotlinReactiveEntityStore<Persistable>, id: String, date: Date, modified: Date) {
            store.insert(testArticleDAO(id, date, modified)).subscribe()
        }

        fun testArticleDAO(id: String,
                           date: Date = Date(),
                           modified: Date = Date(),
                           slug: String = "Super slug",
                           link: String = "http://www.link.com",
                           title: RenderableText = RenderableText("Title rendering", false),
                           content: RenderableText = RenderableText("Content rendering", true),
                           excerpt: RenderableText = RenderableText("Excerpt rendering", true),
                           author: String = "Author",
                           media: String = "media-id"): ArticleDAO {
            val article = ArticleDAOEntity()
            article.setId(id)
            article.setDate(date)
            article.setModified(modified)
            article.setSlug(slug)
            article.setLink(link)
            article.setTitle(title)
            article.setContent(content)
            article.setExcerpt(excerpt)
            article.setAuthor(author)
            article.setFeatured_media(media)

            return article
        }

        fun testArticle(id: String,
                        date: Date = Date(),
                        modified: Date = Date(),
                        slug: String = "Super slug",
                        link: String = "http://www.link.com",
                        title: RenderableText = RenderableText("Title rendering", false),
                        content: RenderableText = RenderableText("Content rendering", true),
                        excerpt: RenderableText = RenderableText("Excerpt rendering", true),
                        author: String = "Author",
                        media: String = "media-id"): Article {
            return Article(id, date, modified, slug, link, title, content, excerpt, author, media)
        }
    }

    lateinit var dataStore: KotlinReactiveEntityStore<Persistable>

    @Before
    fun setup() {
        val dataSource = DatabaseSource(RuntimeEnvironment.application, Models.DEFAULT, TEST_ARTICLE_DATABASE, 1)
        dataSource.setTableCreationMode(TableCreationMode.DROP_CREATE)
        val entityStore = KotlinEntityDataStore<Persistable>(dataSource.configuration)
        dataStore = KotlinReactiveEntityStore(entityStore)
    }

    @After
    fun tearDown() {
        resetDatabase()
    }

    private fun resetDatabase() {
        RuntimeEnvironment.application.deleteDatabase(TEST_ARTICLE_DATABASE)
    }

    @Test
    fun testGet() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        val modified = calendar.time
        val date = Date(modified.time - 1000 * 3600 * 24)
        insertTestData(dataStore, "test-id-1", date, modified)

        val expectedArticle = testArticle("test-id-1", date, modified)

        val disk = ArticleDisk(dataStore)
        disk.get("test-id-1")
                .test()
                .assertValueCount(1)
                .assertResult(expectedArticle)

        // Get non-existing
        disk.get("test-id-2")
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun testSave() {
        val article = testArticle("test-id-1")
        val disk = ArticleDisk(dataStore)

        disk.save(article)
                .test()
                .assertResult(article)

        assertEquals(1, dataStore.select(ArticleDAO::class).get().count())
    }

    @Test
    fun testList() {
        val modified = Date()
        var date = Date(modified.time - 1000 * 3600 * 24)
        insertTestData(dataStore, "test-id-1", date, modified)
        date = Date(date.time + 1000 * 3600 * 24)
        insertTestData(dataStore, "test-id-2", date, modified)
        date = Date(date.time - 1000 * 3600 * 24 * 2)
        insertTestData(dataStore, "test-id-3", date, modified)

        val disk = ArticleDisk(dataStore)
        disk.list(0, 10)
                .test()
                .assertNoErrors()
                .assertValue {
                    it[0].id == "test-id-2" &&
                            it[1].id == "test-id-1" &&
                            it[2].id == "test-id-3"
                }
                .assertComplete()

        disk.list(1, 10)
                .test()
                .assertNoErrors()
                .assertNoValues()
                .assertComplete()
    }

    @Test
    fun testBulkSave() {
        val modified = Date()
        var date = Date(modified.time - 1000 * 3600 * 24)
        val object1 = testArticle("test-id-1", date, modified)
        date = Date(date.time + 1000 * 3600 * 24)
        val object2 = testArticle("test-id-2", date, modified)
        date = Date(date.time - 1000 * 3600 * 24 * 2)
        val object3 = testArticle("test-id-3", date, modified)

        val disk = ArticleDisk(dataStore)
        disk.save(Arrays.asList(object1, object2, object3))
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(1)

        assertEquals(3, dataStore.select(ArticleDAO::class).get().count())
    }

    @Test
    fun testDelete1() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        val modified = calendar.time
        val date = Date(modified.time - 1000 * 3600 * 24)
        insertTestData(dataStore, "test-id-1", date, modified)

        val expected = testArticle("test-id-1", date, modified)

        val disk = ArticleDisk(dataStore)
        disk.delete("test-id-1")
                .test()
                .assertResult(expected)

        assertEquals(0, dataStore.select(ArticleDAO::class).get().count())
    }

    @Test
    fun testDelete2() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        val modified = calendar.time
        val date = Date(modified.time - 1000 * 3600 * 24)
        insertTestData(dataStore, "test-id-1", date, modified)

        val expected = testArticle("test-id-1", date, modified)

        val disk = ArticleDisk(dataStore)
        disk.delete(expected)
                .test()
                .assertResult(expected)

        assertEquals(0, dataStore.select(ArticleDAO::class).get().count())
    }

    @Test
    fun testBulkDelete() {
        val modified = Date()
        val date = Date(modified.time - 1000 * 3600 * 24)
        insertTestData(dataStore, "test-id-1", date, modified)
        insertTestData(dataStore, "test-id-2", date, modified)
        insertTestData(dataStore, "test-id-3", date, modified)

        val disk = ArticleDisk(dataStore)
        disk.deleteAll()
                .test()
                .assertValue(3)
                .assertNoErrors()
                .assertComplete()

        assertEquals(0, dataStore.select(ArticleDAO::class).get().count())
    }
}