package io.supersimple.duitslandnieuws.data.repositories.media

import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.repositories.SimpleMemCache
import java.util.*

class MediaCache(map: MutableMap<String, Media> = HashMap()) : SimpleMemCache<String, Media>(map) {
    override fun getId(value: Media): String = value.id
}