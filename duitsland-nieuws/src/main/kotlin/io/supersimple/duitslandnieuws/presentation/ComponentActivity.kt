package io.supersimple.duitslandnieuws.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.supersimple.duitslandnieuws.application.DuitslandNieuwsApplication
import io.supersimple.duitslandnieuws.di.activity.ActivityComponentBuilderProvider
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponent
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilder
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilderProvider
import io.supersimple.duitslandnieuws.di.fragment.FragmentModule
import javax.inject.Inject
import javax.inject.Provider


/**
 * Dagger component activity -> Migrate to Hilt
 */
abstract class ComponentActivity : AppCompatActivity(), FragmentComponentBuilderProvider {
    @Inject lateinit var fragmentComponentBuilderMap:
            Map<Class<out Fragment>, @JvmSuppressWildcards Provider<FragmentComponentBuilder<*, *>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        setupActivityComponent()
        super.onCreate(savedInstanceState)
    }

    protected fun setupActivityComponent() {
        injectMembers(DuitslandNieuwsApplication[this])
    }

    protected abstract fun injectMembers(activityComponentBuilder: ActivityComponentBuilderProvider)

    override fun <F : Fragment, M : FragmentModule<F>, C : FragmentComponent<F>> fragmentComponentBuilder(fragmentClass: Class<out F>): FragmentComponentBuilder<M, C>? {
        @Suppress("UNCHECKED_CAST")
        return fragmentComponentBuilderMap[fragmentClass]?.get() as? FragmentComponentBuilder<M, C>
    }
}
