package io.supersimple.duitslandnieuws

import android.app.Application
import android.content.Context
import org.junit.After
import org.junit.Before

abstract class AbsContextTestCase {
    lateinit var context: Context
        private set

    @Before
    open fun setup() {
        context = app
    }

    @After
    open fun tearDown() {
    }

    abstract val app: Application
}