package io.supersimple.duitslandnieuws.data.repositories.media

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.supersimple.duitslandnieuws.data.api.MediaEndpoint
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MediaCloudTest {

    lateinit var networkService: MediaEndpoint

    private val gsonConverter: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()

    @get:Rule
    val wireMock = WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort())

    private lateinit var baseUrl: String

    @Before
    fun setup() {
        baseUrl = "http://localhost:${wireMock.port()}"

        networkService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gsonConverter))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MediaEndpoint::class.java)
    }

    @Test
    fun testGet() {
        stubFor(
                get(urlEqualTo("/media/test-id"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                                        .withStatus(200)
                                        .withBodyFile("media_49979.json")
                        )
        )

        val cloud = MediaCloud(networkService)
        cloud.get("test-id")
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue { it.id == "49979" }
                .assertValue { it.title.rendered == "Schermafdruk 2017-01-07 16.04.53" }
                .assertValue { it.author == "78" }
                .assertValue { it.media_details.width == 1125 }
                .assertValue { it.media_details.height == 686 }
                .assertValue { it.media_details.sizes["thumbnail"]!!.file == "Schermafdruk-2017-01-07-16.04.53-150x150.png" }
                .assertValue { it.media_details.sizes["thumbnail"]!!.width == 150 }
                .assertValue { it.media_details.sizes["thumbnail"]!!.height == 150 }
                .assertValue { it.media_details.sizes["thumbnail"]!!.mime_type == "image/png" }
                .assertValue { it.media_details.sizes["thumbnail"]!!.source_url == "http://duitslandnieuws.nl/wp-content/uploads/2017/01/Schermafdruk-2017-01-07-16.04.53-150x150.png" }

    }
}