package io.supersimple.duitslandnieuws.presentation.article

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.presentation.ComponentActivity

class ArticleListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_list)
    }

    override fun injectMembers(activityComponentBuilder: ActivityComponentBuilderProvider) {
        (activityComponentBuilder[ArticleListActivity::class.java] as ArticleListComponent.Builder) //TODO find a way to do this without casting
                .activityModule(ArticleListComponent.ArticleListModule(this))
                .build()
                .injectMembers(this)
    }

    companion object {
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, ArticleListActivity::class.java)
            return intent
        }
    }
}