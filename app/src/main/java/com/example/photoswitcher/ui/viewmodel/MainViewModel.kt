package com.example.photoswitcher.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoswitcher.data.repository.MainRepository
import com.example.photoswitcher.utils.FileHelper
import com.example.photoswitcher.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class MainViewModel(
    private val mainRepository: MainRepository,
    private val fileHelper: FileHelper
) : ViewModel() {

    private val photos = MutableLiveData<Resource<Bitmap>>()
    private val compositeDisposable = CompositeDisposable()

    companion object {
        const val PROGRESS_MAX = 100
    }

    fun fetchPhotos(context: Context) {
        photos.postValue(Resource.loading(null))
        compositeDisposable.add(
            mainRepository.getNextImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ nextImage ->
                    nextImage?.path?.let { photoPath ->
                        fileHelper.downloadImage(photoPath, { progress ->
                            if (progress < PROGRESS_MAX) {
                                photos.postValue(Resource.loading(null))
                            } else {
                                val unzipped =
                                    fileHelper.unzip(File(context.filesDir.absolutePath + "/" + photoPath))
                                val firstFile = unzipped.firstOrNull()
                                firstFile?.let {
                                    val bmp = BitmapFactory.decodeByteArray(
                                        it.content,
                                        0,
                                        it.content.size
                                    )
                                    photos.postValue(Resource.success(bmp))
                                } ?: run {
                                    photos.postValue(Resource.error("Something Went Wrong", null))
                                }
                            }
                        }, {
                            photos.postValue(Resource.error(it ?: "Something Went Wrong", null))
                        })
                    }
                }, { throwable ->
                    photos.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getPhotos(): LiveData<Resource<Bitmap>> {
        return photos
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}