package io.supersimple.duitslandnieuws.di.activity

import android.app.Activity

interface ActivityComponentBuilderProvider {
    fun <A : Activity, M : ActivityModule<A>, C : ActivityComponent<A>> activityComponentBuilder(activityClass: Class<out A>): ActivityComponentBuilder<M, C>?
}
