package io.supersimple.duitslandnieuws.data.repositories.media

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.supersimple.duitslandnieuws.data.models.Media
import io.supersimple.duitslandnieuws.data.parcel.readParcelable
import io.supersimple.duitslandnieuws.data.parcel.writeParcelable
import java.io.File
import java.io.IOException

class MediaDisk(private val fileDir: File) {

    init {
        File(fileDir.path, MEDIA_DIR_NAME).mkdirs()
    }

    fun get(id: String): Maybe<Media> =
            Maybe.create<Media> { observer ->
                val file = fileForMediaItemId(id, fileDir)
                if (file.exists()) {
                    try {
                        val item = file.readParcelable<Media>()
                        observer.onSuccess(item)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                observer.onComplete()
            }

    fun save(media: Media): Single<Media> =
            Single.create { observer ->
                val file = fileForMediaItem(media, fileDir)
                if (file.writeParcelable(media)) {
                    observer.onSuccess(media)
                } else {
                    observer.onError(IOException("Could not save Media item"))
                }
            }

    fun delete(media: Media): Single<Media> = delete(media.id)

    fun delete(id: String): Single<Media> =
            get(id).flatMapSingle({
                realDelete(it.id)
                        .toSingleDefault(it)
            })

    private fun realDelete(id: String): Completable =
            Completable.create { observer ->
                val file = fileForMediaItemId(id, fileDir)
                if (file.delete()) {
                    observer.onComplete()
                } else {
                    observer.onError(IOException("Could not remove $file"))
                }
            }

    fun deleteAll(): Completable =
            Completable.create { observer ->
                if (fileDir.deleteRecursively()) {
                    observer.onComplete()
                } else {
                    observer.onError(IOException("Could not empty MediaDisk"))
                }
            }

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