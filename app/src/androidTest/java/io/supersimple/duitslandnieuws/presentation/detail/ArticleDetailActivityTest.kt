package io.supersimple.duitslandnieuws.presentation.detail

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleDetailActivityTest {
    @get:Rule
    val rule = ActivityTestRule(ArticleDetailActivity::class.java, true, false)

    @Test
    @Throws(Exception::class)
    fun testActivity() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = ArticleDetailActivity.createIntent(targetContext, "test-id")
        val activity = rule.launchActivity(intent)
        assertNotNull(activity)
    }
}