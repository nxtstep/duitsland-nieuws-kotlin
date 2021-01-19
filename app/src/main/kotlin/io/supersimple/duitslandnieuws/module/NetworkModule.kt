package io.supersimple.duitslandnieuws.module

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.supersimple.duitslandnieuws.BuildConfig
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.api.MediaEndpoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


interface NetworkModule {

    companion object {
        const val ARTICLE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

        val default: NetworkModule = object : NetworkModule {}

        private val gsonConverter: Gson =
            GsonBuilder()
                .setDateFormat(ARTICLE_DATE_FORMAT)
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create()

        private val httpClient: OkHttpClient
            get() {
                val builder = OkHttpClient.Builder()
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BASIC
                    builder.addInterceptor(logging)
                }
                return builder.build()
            }

        fun retrofit(
            baseUrl: String,
            gsonConverter: Gson = Companion.gsonConverter,
            httpClient: OkHttpClient = Companion.httpClient
        ): Retrofit =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(DateConverterFactory)
                .addConverterFactory(GsonConverterFactory.create(gsonConverter))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun articleEndpoint(baseUrl: String, retrofit: Retrofit = retrofit(baseUrl)): ArticleEndpoint =
        retrofit.create(ArticleEndpoint::class.java)

    fun mediaEndpoint(baseUrl: String, retrofit: Retrofit = retrofit(baseUrl)): MediaEndpoint =
        retrofit.create(MediaEndpoint::class.java)
}

object DateConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        return if (type == Date::class.java) {
            DateConverter(formatter = SimpleDateFormat(NetworkModule.ARTICLE_DATE_FORMAT, Locale.ROOT))
        } else {
            null
        }
    }
}

private class DateConverter(
    private val formatter: SimpleDateFormat
) : Converter<Date, String> {
    override fun convert(value: Date): String? =
        formatter.format(value)
}