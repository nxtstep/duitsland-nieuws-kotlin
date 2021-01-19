package io.supersimple.duitslandnieuws.ext

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import org.koin.androidx.scope.activityScope
import org.koin.androidx.scope.fragmentScope
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope

/**
 * Fragment Lifecycle aware Scope.
 *
 * The scope gets closed when the lifecycle is destroyed
 */
fun <T : Fragment> T.fragmentLifecycleScope(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED
): Lazy<Scope> = lifecycleScope(
    lazy(mode) {
        fragmentScope().also { scope ->
            (this@fragmentLifecycleScope.activity as? KoinScopeComponent)?.let { scopedActivity ->
                scope.linkTo(scopedActivity.scope)
            }
        }
    }
)

/**
 * Activity Lifecycle aware Scope.
 *
 * The scope gets closed when the lifecycle is destroyed
 */
fun <T : ComponentActivity> T.activityLifecycleScope(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED
): Lazy<Scope> = lifecycleScope(
    lazy(mode) {
        activityScope()
    }
)

fun <T : LifecycleOwner> T.lifecycleScope(
    getter: Lazy<Scope>
): Lazy<Scope> = object : Lazy<Scope> {
    init {
        this@lifecycleScope.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                onLifecycleDestroyed()
            }
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestroyed() {
        if (isInitialized()) {
            value.close()
        }
    }

    override val value: Scope
        get() = getter.value

    override fun isInitialized(): Boolean = getter.isInitialized()
}

/**
 * Load a Koin module and unload it when the lifecyle is destroyed
 *
 * @param module: the module to load
 * @param keepModule: whether to not unload the module
 */
fun <T: LifecycleOwner> T.loadKoinModules(module: Module, keepModule: Boolean = false): Unit =
    loadKoinModules(listOf(module), keepModule)

/**
 * Load the Koin modules and unload them when the lifecyle is destroyed
 *
 * @param modules: the modules to load
 * @param keepModules: whether to not unload the modules
 */
fun <T: LifecycleOwner> T.loadKoinModules(modules: List<Module>, keepModules: Boolean = false) {
    org.koin.core.context.loadKoinModules(modules).also {
        this@loadKoinModules.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                if (!keepModules) {
                    unloadKoinModules(modules)
                }
            }
        })
    }
}
