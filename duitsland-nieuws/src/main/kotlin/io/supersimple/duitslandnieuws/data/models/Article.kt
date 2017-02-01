package io.supersimple.duitslandnieuws.data.models

import java.util.*

data class Article(
        val id: String,
        val date: Date,
        val modified: Date,
        val slug: String,
        val link: String,
        val title: RenderableText,
        val content: RenderableText,
        val excerpt: RenderableText,
        val author: String
) {
    companion object {
        val empty = Article("", Date(0), Date(0), "", "", RenderableText.empty, RenderableText.empty, RenderableText.empty, "")
    }
}

data class RenderableText(
        val rendered: String,
        val protected: Boolean
) {
    companion object {
        val empty = RenderableText("", false)
    }
}
