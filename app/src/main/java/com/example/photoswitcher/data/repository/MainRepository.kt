package com.example.photoswitcher.data.repository

import com.example.photoswitcher.data.model.Photo
import com.example.photoswitcher.utils.PrefHelper
import io.reactivex.Observable


class MainRepository(private val prefHelper : PrefHelper) {

    private fun getImages(): List<Photo> {
        return PrefHelper.listFileName.map { Photo(0, it, it) }.toList()
    }

    fun getNextImage(): Observable<Photo> {
        if (getImages().isEmpty()) return Observable.empty()

        val latestIndex = prefHelper.getLatestIndex()
        val newIndex: Int = if (latestIndex == -1) {
            0
        } else {
            if (latestIndex + 1 == getImages().size) 0 else latestIndex + 1
        }

        prefHelper.setLatestIndex(newIndex)
        return Observable.just(getImages()[newIndex])
    }

}