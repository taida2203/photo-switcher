package com.example.photoswitcher.utils

import android.content.Context
import android.content.SharedPreferences
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig

class PhotoStorages private constructor(val context: Context) {
    private var sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object : SingletonHolder<PhotoStorages, Context>(::PhotoStorages) {
        const val PREF_NAME = "photo_exact_pref"
        const val PREF_KEY_INDEX = "pref_key_index"
        const val BASE_URL = "https://test-assets-mobile.s3-us-west-2.amazonaws.com"
        const val PROGRESS_MAX = 100
        val listFileName = listOf("125%402.zip", "127%402.zip")
    }

    init {
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(context, config)
    }

    fun getAllImages(): List<String> {
        return listFileName
    }

    fun getNextImage(): String {
        if (getAllImages().isEmpty()) return ""

        val latestIndex = sharedPref.getInt(PREF_KEY_INDEX, -1)
        val newIndex: Int = if (latestIndex == -1) {
            0
        } else {
            if (latestIndex + 1 == getAllImages().size) 0 else latestIndex + 1
        }

        with(sharedPref.edit()) {
            putInt(PREF_KEY_INDEX, newIndex)
            apply()
        }
        return getAllImages()[newIndex]
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