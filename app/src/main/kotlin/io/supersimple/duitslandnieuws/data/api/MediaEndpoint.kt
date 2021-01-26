package io.supersimple.duitslandnieuws.data.api

import io.reactivex.Maybe
import io.supersimple.duitslandnieuws.data.models.Media
import retrofit2.http.GET
import retrofit2.http.Path


interface MediaEndpoint {

    @GET("media/{id}")
    fun get(@Path("id") mediaId: String): Maybe<Media>
}