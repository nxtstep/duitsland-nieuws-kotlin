package io.supersimple.duitslandnieuws.ext

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Fragment view binding
 */
fun <T> Fragment.viewBinding(viewBinder: (View) -> T): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T>, LifecycleObserver {
        private var binding: T? = null

        init {
            this@viewBinding
                .viewLifecycleOwnerLiveData
                .observe(this@viewBinding, Observer { owner ->
                    owner.lifecycle.addObserver(this)
                })
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            return binding ?: viewBinder(thisRef.requireView()).also {
                binding = it
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onViewDestroyed() {
            binding = null
        }
    }

/**
 * Activity view binding
 */
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }
