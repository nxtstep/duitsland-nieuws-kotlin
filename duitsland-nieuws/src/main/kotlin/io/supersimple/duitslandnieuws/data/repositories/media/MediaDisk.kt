package io.supersimple.duitslandnieuws.data.repositories.media

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.parcel.readParcelable
import io.supersimple.duitslandnieuws.data.parcel.writeParcelable
import io.supersimple.duitslandnieuws.rx.remove
import java.io.File
import java.io.IOException

class MediaDisk(private val fileDir: File) {

    init {
        File(fileDir.path, MEDIA_DIR_NAME).mkdirs()
    }

    fun get(id: String): Maybe<Media> =
            Maybe.defer { Maybe.just(fileForMediaItemId(id, fileDir)) }
                    .filter { it.exists() }
                    .map { file -> file.readParcelable<Media>() }

    fun save(media: Media): Single<Media> =
            Single.defer { Single.just(fileForMediaItem(media, fileDir)) }
                    .filter { it.writeParcelable(media) }
                    .map { _ -> media }
                    .switchIfEmpty { Single.error<Media>(IOException("Could not save Media item")) }
                    .toSingle()

    fun delete(media: Media): Single<Media> = delete(media.id)

    fun delete(id: String): Single<Media> =
            get(id).flatMapSingle {
                realDelete(it.id)
                        .toSingleDefault(it)
            }

    private fun realDelete(id: String): Completable =
            Single.just(id)
                    .map { fileForMediaItemId(id, fileDir) }
                    .flatMapCompletable { file ->
                        file.remove()
                    }

    fun deleteAll(): Completable = Completable.fromAction { fileDir.deleteRecursively() }

    companion object {
        const val MEDIA_DIR_NAME = "media"
        const val MEDIA_FILENAME_FORMAT = "media_%s.parcel"

        fun filenameForMediaItem(item: Media): String = filenameForMediaItemId(item.id)

        fun filenameForMediaItemId(id: String): String = String.format(MEDIA_FILENAME_FORMAT, id)

        fun fileForMediaItem(item: Media, fileDir: File): File =
                fileForMediaItemId(item.id, fileDir)

        fun fileForMediaItemId(id: String, fileDir: File): File =
                File(File(fileDir.path, MEDIA_DIR_NAME).path, filenameForMediaItemId(id))
    }
}