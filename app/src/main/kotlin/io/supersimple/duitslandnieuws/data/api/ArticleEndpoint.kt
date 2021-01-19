package io.supersimple.duitslandnieuws.data.api

import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Article
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Date

interface ArticleEndpoint {
    @GET("posts")
    fun list(
        @Query("page") page: Int = 1,
        @Query("per_page") pageSize: Int = 10,
        @Query("after") after: Date? = null,
        @Query("before") before: Date? = null,
    ): Single<List<Article>>

    @GET("posts/{id}")
    fun get(@Path("id") id: String): Maybe<Article>
}