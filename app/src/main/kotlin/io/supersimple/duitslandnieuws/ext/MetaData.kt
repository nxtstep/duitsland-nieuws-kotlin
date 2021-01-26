package io.supersimple.duitslandnieuws.application

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle

inline fun <T> Context.getMetaData(func: Bundle.() -> T): T {
    return packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            .metaData
            .func()
}
