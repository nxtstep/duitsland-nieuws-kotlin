package io.supersimple.duitslandnieuws.data.repositories.media

import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.models.MediaDetails
import io.supersimple.duitslandnieuws.data.models.MediaItem
import io.supersimple.duitslandnieuws.data.models.RenderableText
import org.junit.Test
import java.util.*

class MediaCacheTest {
    @Test
    fun testMediaCache() {
        val cache = MediaCache()
        val map = HashMap<String, MediaItem>()
        val details = MediaDetails(1000, 1000, "filename", map)
        val media = Media("media-id", Date(), RenderableText("Title"), "author-id", "media-slug", RenderableText("Caption"), details)

        cache.save(media)
                .test()
                .assertResult(media)

        cache.delete("media-id")
                .test()
                .assertResult(media)

        cache.delete("media-id")
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()
    }
}