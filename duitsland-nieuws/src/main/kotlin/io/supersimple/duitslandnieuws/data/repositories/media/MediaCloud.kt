package io.supersimple.duitslandnieuws.data.repositories.media

import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.api.MediaEndpoint
import io.supersimple.duitslandnieuws.data.models.Media

class MediaCloud(private val mediaService: MediaEndpoint) {
    fun get(id: String): Single<Media> {
        return mediaService.get(id)
    }
}