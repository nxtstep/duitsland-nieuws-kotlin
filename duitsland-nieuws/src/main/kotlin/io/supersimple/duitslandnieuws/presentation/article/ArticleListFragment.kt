package io.supersimple.duitslandnieuws.presentation.article

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import io.supersimple.duitslandnieuws.R
import io.supersimple.duitslandnieuws.application.getApiBaseUrl
import io.supersimple.duitslandnieuws.data.api.ArticleEndpoint
import io.supersimple.duitslandnieuws.data.repositories.article.*
import kotlinx.android.synthetic.main.fragment_article_list.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticleListFragment : Fragment(), ArticleListView {
    lateinit var articleListViewModel: ArticleListViewModel
    lateinit var articleListAdapter: ArticleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO Inject
        // Cache
        val cache = ArticleCache()
        // Disk
        val dataSource = DatabaseSource(context, Models.DEFAULT, "db-name.db", 1)
        dataSource.setTableCreationMode(TableCreationMode.DROP_CREATE)
        val entityStore = KotlinEntityDataStore<Persistable>(dataSource.configuration)
        val dataStore = KotlinReactiveEntityStore(entityStore)
        val disk = ArticleDisk(dataStore)
        // Network
        val articleService = Retrofit.Builder()
                .baseUrl(getApiBaseUrl(context))
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gsonConverter))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ArticleEndpoint::class.java)
        val cloud = ArticleCloud(articleService)
        // Article Repository
        val repo = ArticleRepository(cache, disk, cloud)

        articleListViewModel = ArticleListViewModel(repo, Schedulers.io(), AndroidSchedulers.mainThread())
        articleListAdapter = ArticleListAdapter(articleListViewModel)
    }

    // TODO Inject
    private val gsonConverter: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflater?.let {
            return it.inflate(R.layout.fragment_article_list, container, false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_article_list.layoutManager = LinearLayoutManager(view?.context)
        rv_article_list.adapter = articleListAdapter
    }

    override fun onStart() {
        super.onStart()
        articleListViewModel.bindView(this)
    }

    override fun onStop() {
        articleListViewModel.unbind()
        super.onStop()
    }

    override fun showLoadingIndicator(flag: Boolean) {
        Toast.makeText(context, "Loading $flag", Toast.LENGTH_LONG).show()
    }

    override fun showEmptyState() {
        Toast.makeText(context, "Empty", Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
    }

    override fun showArticleListLoaded(page: Int) {
        Toast.makeText(context, "Loaded page: $page", Toast.LENGTH_LONG).show()
    }
}