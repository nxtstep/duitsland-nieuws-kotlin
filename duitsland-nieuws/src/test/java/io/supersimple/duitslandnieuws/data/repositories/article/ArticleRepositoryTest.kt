package io.supersimple.duitslandnieuws.data.repositories.article

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Article
import io.supersimple.duitslandnieuws.data.models.RenderableText
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import java.util.*

class ArticleRepositoryTest {

    lateinit var mockCache: ArticleCache
    lateinit var mockDisk: ArticleDisk
    lateinit var mockCloud: ArticleCloud

    companion object {
        val testArticle = Article("1", Date(), Date(), "MySlug", "http://www.duitslandnieuws.de",
                RenderableText("title", true), RenderableText("Content", false),
                RenderableText("Excerpt", false), "Author", "media-id")
    }

    @Test
    fun testGet() {
        mockCache = mock {
            on { get(anyString()) } doReturn Maybe.empty()
            on { save(any<Article>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { get(anyString()) } doReturn Maybe.just(testArticle)
        }
        mockCloud = mock {}

        val articleRepo = ArticleRepository(mockCache, mockDisk, mockCloud)
        articleRepo.get("test-id")
                .test()
                .assertResult(testArticle)

        verify(mockCache, times(1)).get(eq("test-id"))
        verify(mockCache, times(1)).save(eq(testArticle))
        verify(mockDisk, times(1)).get(eq("test-id"))
        verify(mockCloud, never()).get(anyString())
    }

    @Test
    fun testFetch() {
        mockCache = mock {
            on { get(anyString()) } doReturn Maybe.empty()
            on { save(any<Article>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { get(anyString()) } doReturn Maybe.empty()
            on { save(any<Article>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {
            on { get(anyString()) } doReturn Single.just(testArticle)
        }

        val articleRepo = ArticleRepository(mockCache, mockDisk, mockCloud)
        articleRepo.fetch("id-1")
                .test()
                .assertResult(testArticle)

        verify(mockCache, never()).get(anyString())
        verify(mockCache, times(1)).save(eq(testArticle))
        verify(mockDisk, never()).get(anyString())
        verify(mockDisk, times(1)).save(eq(testArticle))
    }

    @Test
    fun testList() {
        mockCache = mock {
            on { list() } doReturn Maybe.empty()
            on { save(any<List<Article>>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { list(anyInt()) } doReturn Maybe.just(Arrays.asList(testArticle))
        }
        mockCloud = mock {
        }

        val articleRepo = ArticleRepository(mockCache, mockDisk, mockCloud)
        articleRepo.list()
                .test()
                .assertResult(Arrays.asList(testArticle))

        verify(mockCache, times(1)).list()
        verify(mockCache, times(1)).save(any<List<Article>>())
        verify(mockDisk, times(1)).list(anyInt())
    }

    @Test
    fun testRefresh() {
        mockCache = mock {
            on { list() } doReturn Maybe.empty()
            on { deleteAll() } doReturn Single.just(Collections.emptyList())
            on { save(any<List<Article>>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { list(anyInt()) } doReturn Maybe.empty()
            on { save(any<List<Article>>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {
            on { list() } doReturn Single.just(Arrays.asList(testArticle))
        }

        val articleRepo = ArticleRepository(mockCache, mockDisk, mockCloud)
        articleRepo.refresh()
                .test()
                .assertResult(Arrays.asList(testArticle))

        verify(mockCache, never()).list()
        verify(mockCache, times(1)).save(any<List<Article>>())
        verify(mockCache, times(1)).deleteAll()
        verify(mockDisk, never()).list(anyInt())
        verify(mockDisk, times(1)).save(any<List<Article>>())
    }

    @Test
    fun testSave() {
        mockCache = mock {
            on { save(any<Article>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { save(any<Article>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {}

        val articleRepo = ArticleRepository(mockCache, mockDisk, mockCloud)
        articleRepo.save(testArticle)
                .test()
                .assertResult(testArticle)

        verify(mockCache, times(1)).save(eq(testArticle))
        verify(mockDisk, times(1)).save(eq(testArticle))
    }

    @Test
    fun testDelete() {
        mockCache = mock {
            on { delete(any<Article>()) }.thenAnswer {
                Maybe.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { delete(any<Article>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {}

        val articleRepo = ArticleRepository(mockCache, mockDisk, mockCloud)
        articleRepo.delete(testArticle)
                .test()
                .assertResult(testArticle)

        verify(mockCache, times(1)).delete(eq(testArticle))
        verify(mockDisk, times(1)).delete(eq(testArticle))
    }

    @Test
    fun testClearCaches() {
        mockCache = mock {
            on { deleteAll() } doReturn Single.just(Arrays.asList(testArticle))
        }
        mockDisk = mock {}
        mockCloud = mock {}

        val articleRepo = ArticleRepository(mockCache, mockDisk, mockCloud)
        articleRepo.clearCaches()
                .test()
                .assertComplete()
                .assertNoErrors()

        verify(mockCache, times(1)).deleteAll()
    }
}
