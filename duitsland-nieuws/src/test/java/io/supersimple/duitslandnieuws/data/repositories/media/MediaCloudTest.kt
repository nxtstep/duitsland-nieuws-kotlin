package io.supersimple.duitslandnieuws.data.repositories.media

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.supersimple.duitslandnieuws.BuildConfig
import io.supersimple.duitslandnieuws.data.api.MediaEndpoint
import io.supersimple.duitslandnieuws.data.rest.AbsUnitTestRestApiTestCase
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class MediaCloudTest : AbsUnitTestRestApiTestCase() {

    lateinit var networkService: MediaEndpoint

    override val dispatcher: Dispatcher
        get() = dispatch
    override val app: Application
        get() = RuntimeEnvironment.application

    private val gsonConverter: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()

    @Before
    override fun setup() {
        super.setup()

        networkService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gsonConverter))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MediaEndpoint::class.java)
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testGet() {
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

    private val dispatch = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val response = MockResponse()
            var fileName: String? = null
            val requestPath = request.path
            println("Dispatcher requestPath: $requestPath\n")
            if (requestPath == "/media/test-id") {
                response.setHeader("Content-Type", "application/json")
                response.setResponseCode(200)
                fileName = "media_49979.json"
            } else {
                // Unhandled request
                response.setResponseCode(500)
            }

            fileName?.let {
                try {
                    val inputStream = getInputStreamForFile(it)
                    val buffer = Buffer()
                    buffer.readFrom(inputStream)
                    response.body = buffer
                    inputStream.close()
                } catch (e: IOException) {
                    println("Exception while preparing Response body: " + e.message)
                }
            }

            return response
        }
    }
}