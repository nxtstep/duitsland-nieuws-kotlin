package io.supersimple.duitslandnieuws.data.repositories.media

import io.reactivex.Maybe
import io.supersimple.duitslandnieuws.data.api.MediaEndpoint
import io.supersimple.duitslandnieuws.data.models.Media

class MediaCloud(private val mediaService: MediaEndpoint) {
    fun get(id: String): Maybe<Media> = mediaService.get(id)
}