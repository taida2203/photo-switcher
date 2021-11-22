package com.example.photoswitcher.utils

import android.content.Context
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.example.photoswitcher.ui.viewmodel.MainViewModel.Companion.PROGRESS_MAX
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream

class FileHelper private constructor(val context: Context) {

    companion object : SingletonHolder<FileHelper, Context>(::FileHelper) {
        const val BASE_URL = "https://test-assets-mobile.s3-us-west-2.amazonaws.com"
        val listFileName = listOf("125%402.zip", "127%402.zip")
    }

    init {
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(context, config)
    }

    fun unzip(file: File): List<UnzippedFile> = ZipInputStream(FileInputStream(file))
        .use { zipInputStream ->
            generateSequence { zipInputStream.nextEntry }
                .filterNot { it.isDirectory }
                .map {
                    UnzippedFile(
                        filename = it.name,
                        content = zipInputStream.readBytes()
                    )
                }.toList()
        }

    fun downloadImage(
        fileName: String,
        progressCallback: (progress: Int) -> Unit?,
        errorCallback: (error: String?) -> Unit?
    ): List<String> {
        PRDownloader.download(
            "$BASE_URL/$fileName",
            context.filesDir.absolutePath,
            fileName
        )
            .build()
            .setOnProgressListener {
                val currentPercent = try {
                    it.currentBytes.toInt() / it.totalBytes.toInt()
                } catch (e: Exception) {
                    0
                }
                progressCallback.invoke(currentPercent)
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    progressCallback.invoke(PROGRESS_MAX)
                }

                override fun onError(error: com.downloader.Error?) {
                    error?.let {
                        errorCallback.invoke(it.serverErrorMessage)
                    }
                }
            })
        return listFileName
    }
}

data class UnzippedFile(val filename: String, val content: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnzippedFile

        if (filename != other.filename) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}

