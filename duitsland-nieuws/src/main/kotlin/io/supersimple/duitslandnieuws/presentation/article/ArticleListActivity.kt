package io.supersimple.duitslandnieuws.presentation.article

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.supersimple.duitslandnieuws.R

class ArticleListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_list)
    }

    companion object {
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, ArticleListActivity::class.java)
            return intent
        }
    }
}
