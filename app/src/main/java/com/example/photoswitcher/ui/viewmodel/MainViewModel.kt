package com.example.photoswitcher.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoswitcher.data.repository.MainRepository
import com.example.photoswitcher.data.model.Photo
import com.example.photoswitcher.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val photos = MutableLiveData<Resource<List<Photo>>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        photos.postValue(Resource.loading(null))
        compositeDisposable.add(
            mainRepository.getPhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userList ->
                    photos.postValue(Resource.success(userList))
                }, { throwable ->
                    photos.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPhotos(): LiveData<Resource<List<Photo>>> {
        return photos
    }

}