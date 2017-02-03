package io.supersimple.duitslandnieuws.presentation.article

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleListActivityTest {
    @get:Rule
    val rule = ActivityTestRule(ArticleListActivity::class.java, true, false)

    @Test
    @Throws(Exception::class)
    fun testActivity() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = ArticleListActivity.createIntent(targetContext)
        val activity = rule.launchActivity(intent)
        assertNotNull(activity)
    }
}