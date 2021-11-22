package com.example.photoswitcher.data.repository

import com.example.photoswitcher.data.model.Photo
import io.reactivex.Single


class MainRepository() {

    fun getPhotos(): Single<List<Photo>> {
        return Single.fromObservable {  };
    }

}