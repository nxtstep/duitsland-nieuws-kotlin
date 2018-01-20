package io.supersimple.duitslandnieuws.data.repositories.media

import android.os.Parcel
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.parcel.ParcelableProvider
import io.supersimple.duitslandnieuws.data.parcel.writeParcelable
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepositoryTest.Companion.testMediaId
import io.supersimple.duitslandnieuws.data.repositories.media.MediaRepositoryTest.Companion.testMediaItem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MediaDiskTest {

    @get:Rule
    val testDir = TemporaryFolder()

    private lateinit var fileDir: File
    private lateinit var parcelableProvider: ParcelableProvider
    private lateinit var mockParcel: Parcel

    @Before
    fun setup() {
        fileDir = testDir.root

        mockParcel = mock {
            on { marshall() } doReturn byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7)
            on { readList(any<List<Media>>(), any()) }.thenAnswer {
                @Suppress("UNCHECKED_CAST")
                (it.arguments[0] as MutableList<Media>).addAll(listOf(testMediaItem))
            }
        }
        parcelableProvider = mock {
            on { obtain() } doReturn mockParcel
        }
    }

    @Test
    fun testGet() {

        val mediaItem = testMediaItem
        val disk = MediaDisk(fileDir, parcelableProvider)

        disk.get(mediaItem.id)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertNoValues()

        val mediaItemFile = MediaDisk.fileForMediaItem(mediaItem, fileDir)
        assertTrue(mediaItemFile.writeParcelable(mediaItem, parcelableProvider))

        disk.get(mediaItem.id)
                .test()
                .assertResult(mediaItem)

        verify(mockParcel).readList(any(), eq(Media::class.java.classLoader))
    }

    @Test
    fun testSave() {
        val mediaItem = testMediaItem
        val mediaItemFile = MediaDisk.fileForMediaItem(mediaItem, fileDir)
        assertFalse(mediaItemFile.exists())

        val disk = MediaDisk(fileDir, parcelableProvider)
        disk.save(mediaItem)
                .test()
                .assertResult(mediaItem)

        assertTrue(mediaItemFile.exists())
        verify(mockParcel).marshall()
    }

    @Test
    fun testDeleteId() {
        val disk = MediaDisk(fileDir, parcelableProvider)

        val mediaItem = testMediaItem
        val mediaItemFile = MediaDisk.fileForMediaItem(mediaItem, fileDir)
        assertTrue(mediaItemFile.writeParcelable(mediaItem, parcelableProvider))
        assertTrue(mediaItemFile.exists())

        disk.delete(testMediaId)
                .test()
                .assertResult(mediaItem)

        assertFalse(mediaItemFile.exists())
    }

    @Test
    fun testDelete() {
        val disk = MediaDisk(fileDir, parcelableProvider)

        val mediaItem = testMediaItem
        val mediaItemFile = MediaDisk.fileForMediaItem(mediaItem, fileDir)
        assertTrue(mediaItemFile.writeParcelable(mediaItem, parcelableProvider))
        assertTrue(mediaItemFile.exists())

        disk.delete(mediaItem)
                .test()
                .assertResult(mediaItem)

        assertFalse(mediaItemFile.exists())
    }

    @Test
    fun testDeleteAll() {
        val disk = MediaDisk(fileDir, parcelableProvider)

        val mediaItem = testMediaItem
        val mediaItem2 = mediaItem.copy()
        val mediaItemFile = MediaDisk.fileForMediaItem(mediaItem, fileDir)
        val mediaItemFile2 = MediaDisk.fileForMediaItem(mediaItem2, fileDir)
        assertTrue(mediaItemFile.writeParcelable(mediaItem, parcelableProvider))
        assertTrue(mediaItemFile.writeParcelable(mediaItem2, parcelableProvider))
        assertTrue(mediaItemFile.exists())
        assertTrue(mediaItemFile2.exists())

        disk.deleteAll()
                .test()
                .assertComplete()

        assertFalse(mediaItemFile.exists())
        assertFalse(mediaItemFile2.exists())
    }

}