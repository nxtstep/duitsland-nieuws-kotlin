package io.supersimple.duitslandnieuws.data.rest

import org.junit.Before
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

abstract class AbsUnitTestRestApiTestCase : AbsRestApiTestCase() {
    lateinit var resourcePath: String
        private set

    @Before
    override fun setup() {
        super.setup()
        //
        // Set resource paths
        resourcePath = context.packageResourcePath + "/src/test/resources/"
    }

    /**
     * Opens a InputStream to read from. The filename should point at a file placed /src/test/resources
     *
     * @param filename The name of the file in path /src/test/resources
     *
     * @return
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun getInputStreamForFile(filename: String): InputStream {
        try {
            println("InputStream filename: $filename \n")
            val inputStream = FileInputStream(File("$resourcePath$filename"))
            return inputStream
        } catch (e: IOException) {
            println("Could not load resource: [$filename]")
            throw e
        }
    }
}

