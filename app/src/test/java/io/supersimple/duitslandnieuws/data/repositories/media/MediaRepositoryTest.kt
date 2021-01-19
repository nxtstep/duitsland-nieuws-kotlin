package io.supersimple.duitslandnieuws.data.repositories.media

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.models.MediaDetails
import io.supersimple.duitslandnieuws.data.models.MediaItem
import io.supersimple.duitslandnieuws.data.models.RenderableText
import org.junit.Test
import org.mockito.ArgumentMatchers
import java.util.Date
import java.util.HashMap

class MediaRepositoryTest {

    lateinit var mockCache: MediaCache
    lateinit var mockDisk: MediaDisk
    lateinit var mockCloud: MediaCloud

    companion object {
        const val testMediaId = "10"
        val testSizes: HashMap<String, MediaItem> by lazy {
            val map = HashMap<String, MediaItem>()
            map["key"] = MediaItem("thumbnail file", 2, 5, "image/png", "http://www.images.com/image-url.png")
            map
        }
        val testDetails by lazy { MediaDetails(15, 20, "filename-10.jpg", testSizes) }
        val testMediaItem by lazy { Media(testMediaId, Date(), RenderableText("Title disk"), "405", "no slug", RenderableText("Caption for disk"), testDetails) }
    }

    @Test
    fun testGet() {
        mockCache = mock {
            on { get(ArgumentMatchers.anyString()) } doReturn Maybe.empty()
            on { save(any<Media>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { get(ArgumentMatchers.anyString()) } doReturn Maybe.just(testMediaItem)
            on { save(any<Media>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {
            on { get(ArgumentMatchers.anyString()) } doReturn Maybe.error(IllegalStateException("Should not be called"))
        }

        val mediaRepo = MediaRepository(mockCache, mockDisk, mockCloud)
        mediaRepo.get("test-id")
                .test()
                .assertResult(testMediaItem)

        verify(mockCache).get(eq("test-id"))
        verify(mockCache).save(eq(testMediaItem))
        verify(mockDisk).get(eq("test-id"))
        verify(mockCloud).get(eq("test-id"))
    }

    @Test
    fun testFetch() {
        mockCache = mock {
            on { get(ArgumentMatchers.anyString()) } doReturn Maybe.empty()
            on { save(any<Media>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { get(ArgumentMatchers.anyString()) } doReturn Maybe.empty()
            on { save(any<Media>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {
            on { get(ArgumentMatchers.anyString()) } doReturn Maybe.just(testMediaItem)
        }

        val mediaRepo = MediaRepository(mockCache, mockDisk, mockCloud)
        mediaRepo.get("id-1")
                .test()
                .assertResult(testMediaItem)

        verify(mockCache).get(ArgumentMatchers.anyString())
        verify(mockCache).save(eq(testMediaItem))
        verify(mockDisk).get(ArgumentMatchers.anyString())
        verify(mockDisk).save(eq(testMediaItem))
    }

    @Test
    fun testSave() {
        mockCache = mock {
            on { save(any<Media>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { save(any<Media>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {}

        val mediaRepo = MediaRepository(mockCache, mockDisk, mockCloud)
        mediaRepo.save(testMediaItem)
                .test()
                .assertResult(testMediaItem)

        verify(mockCache).save(eq(testMediaItem))
        verify(mockDisk).save(eq(testMediaItem))
    }

    @Test
    fun testDelete() {
        mockCache = mock {
            on { delete(any<Media>()) }.thenAnswer {
                Maybe.just(it.arguments[0])
            }
        }
        mockDisk = mock {
            on { delete(any<Media>()) }.thenAnswer {
                Single.just(it.arguments[0])
            }
        }
        mockCloud = mock {}

        val mediaRepo = MediaRepository(mockCache, mockDisk, mockCloud)
        mediaRepo.delete(testMediaItem)
                .test()
                .assertResult(testMediaItem)

        verify(mockCache).delete(eq(testMediaItem))
        verify(mockDisk).delete(eq(testMediaItem))
    }

    @Test
    fun testClearCaches() {
        mockCache = mock {
            on { deleteAll() } doReturn Single.just(listOf(testMediaItem))
        }
        mockDisk = mock {
            on { deleteAll() } doReturn Completable.complete()
        }
        mockCloud = mock {}

        val mediaRepo = MediaRepository(mockCache, mockDisk, mockCloud)
        mediaRepo.clearCaches()
                .test()
                .assertComplete()
                .assertNoErrors()

        verify(mockCache).deleteAll()
        verify(mockDisk).deleteAll()
    }
}