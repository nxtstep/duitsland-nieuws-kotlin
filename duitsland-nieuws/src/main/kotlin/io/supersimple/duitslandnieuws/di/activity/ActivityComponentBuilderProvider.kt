package io.supersimple.duitslandnieuws.di.activity

import android.app.Activity

interface ActivityComponentBuilderProvider {
    operator fun get(activityClass: Class<out Activity>): ActivityComponentBuilder<*, *>
}
