package io.supersimple.duitslandnieuws.di.fragment

import android.support.v4.app.Fragment

interface FragmentComponentBuilderProvider {
    operator fun get(fragmentClass: Class<out Fragment>): FragmentComponentBuilder<*, *>
}
