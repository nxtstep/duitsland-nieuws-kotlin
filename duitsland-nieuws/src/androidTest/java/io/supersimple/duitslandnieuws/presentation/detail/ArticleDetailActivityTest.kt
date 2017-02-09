package io.supersimple.duitslandnieuws.presentation.detail

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert
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
        Assert.assertNotNull(activity)
    }
}