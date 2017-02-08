package io.supersimple.duitslandnieuws.di.app

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import io.supersimple.duitslandnieuws.BuildConfig
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.api.MediaEndpoint
import io.supersimple.duitslandnieuws.di.app.qualifier.BaseUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule {

    companion object {
        const val ARTICLE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    }

    @Provides
    @Singleton
    fun provideGsonConverter(): Gson =
            GsonBuilder()
                    .setDateFormat(ARTICLE_DATE_FORMAT)
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                    .create()

    @Provides
    fun provideHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            builder.addInterceptor(logging)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(@BaseUrl baseUrl: String, gsonConverter: Gson, httpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gsonConverter))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()


    @Provides
    @Singleton
    fun provideArticleEndpoint(retrofit: Retrofit): ArticleEndpoint =
            retrofit.create(ArticleEndpoint::class.java)

    @Provides
    @Singleton
    fun provideMediaEndpoint(retrofit: Retrofit): MediaEndpoint =
            retrofit.create(MediaEndpoint::class.java)
}