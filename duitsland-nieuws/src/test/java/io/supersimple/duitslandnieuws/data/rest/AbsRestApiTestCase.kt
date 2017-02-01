package io.supersimple.duitslandnieuws.data.rest

import io.supersimple.duitslandnieuws.AbsContextTestCase
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import java.io.IOException
import java.io.InputStream


abstract class AbsRestApiTestCase : AbsContextTestCase() {
    internal lateinit var baseUrl: String
        private set

    internal lateinit var webServer: MockWebServer
        private set

    @Before
    @Throws(IOException::class)
    override fun setup() {
        super.setup()
        // Create a MockWebServer. These are lean enough that you can create a new
        // instance for every unit test.
        webServer = MockWebServer()
        //
        // Set a dispatcher for the requests
        webServer.setDispatcher(dispatcher)
        // Start the webServer.
        webServer.start()
        // Ask the webServer for its URL. You'll need this to make HTTP requests.
        val httpBaseUrl = webServer.url("")
        baseUrl = httpBaseUrl.toString()
        println("baseUrl: $baseUrl")
    }

    @After
    override fun tearDown() {
        super.tearDown()
        // Shut down the webServer. Instances cannot be reused.
        try {
            webServer.shutdown()
        } catch (e: IOException) {
            System.out.println("Cleanup of webserver for test-case: [$javaClass] FAILED")
            e.printStackTrace()
        }
    }

    abstract val dispatcher: Dispatcher

    abstract fun getInputStreamForFile(filename: String): InputStream
}