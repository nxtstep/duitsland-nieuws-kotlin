package io.supersimple.duitslandnieuws.presentation.mvp

interface Presenter<in T> {
    fun bind(view: T): Unit

    fun unbind(): Unit
}