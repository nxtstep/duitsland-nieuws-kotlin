package io.supersimple.duitslandnieuws.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.supersimple.duitslandnieuws.application.DuitslandNieuwsApplication
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilder
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilderProvider
import javax.inject.Inject
import javax.inject.Provider


abstract class ComponentActivity : AppCompatActivity(), FragmentComponentBuilderProvider {
    @Inject lateinit var fragmentComponentBuilderMap:
            Map<Class<out Fragment>, @JvmSuppressWildcards Provider<FragmentComponentBuilder<*, *>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityComponent()
    }

    protected fun setupActivityComponent() {
        injectMembers(DuitslandNieuwsApplication[this])
    }

    protected abstract fun injectMembers(activityComponentBuilder: ActivityComponentBuilderProvider)

    override fun get(fragmentClass: Class<out Fragment>): FragmentComponentBuilder<*, *> {
        return fragmentComponentBuilderMap[fragmentClass]!!.get()
    }
}
