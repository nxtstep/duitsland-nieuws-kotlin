package io.supersimple.duitslandnieuws.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import io.supersimple.duitslandnieuws.di.fragment.FragmentComponentBuilderProvider

abstract class ComponentFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectMembers(activity as FragmentComponentBuilderProvider)
    }

    protected abstract fun injectMembers(fragmentComponentBuilder: FragmentComponentBuilderProvider)
}