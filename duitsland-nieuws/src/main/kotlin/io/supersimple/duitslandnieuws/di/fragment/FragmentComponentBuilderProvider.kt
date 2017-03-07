package io.supersimple.duitslandnieuws.di.fragment

import android.support.v4.app.Fragment

interface FragmentComponentBuilderProvider {
    fun <F: Fragment, M: FragmentModule<F>, C : FragmentComponent<F>> fragmentComponentBuilder(fragmentClass: Class<out F>): FragmentComponentBuilder<M, C>?
}
