package com.example.photoswitcher.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoswitcher.data.repository.MainRepository
import com.example.photoswitcher.utils.FileHelper
import com.example.photoswitcher.utils.Resource
import io.reactivex.Observable
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
                .map { it.path }
                .filter { !TextUtils.isEmpty(it) }
                .concatMap { photoPath ->
                    Observable.create<String> { emitter ->

                        fileHelper.downloadImage(photoPath, { progress ->
                            if (progress >= PROGRESS_MAX) {
                                emitter.onNext(context.filesDir.absolutePath + "/" + photoPath)
                            }
                        }, {
                            emitter.onError(Error("Download file failed"))
                        })
                    }
                }
                .concatMap { photoPath ->
                    Observable.create<Bitmap> { emitter ->
                        val unzipped =
                            fileHelper.unzip(File(photoPath))
                        val firstFile = unzipped.firstOrNull()
                        firstFile?.let {
                            val bmp = BitmapFactory.decodeByteArray(
                                it.content,
                                0,
                                it.content.size
                            )
                            emitter.onNext(bmp)
                        } ?: run {
                            emitter.onError(Error("Un zip file failed"))
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ nextImageBitmap ->
                    photos.postValue(Resource.success(nextImageBitmap))
                }, { throwable ->
                    photos.postValue(
                        Resource.error(
                            throwable?.localizedMessage ?: "Something Went Wrong", null
                        )
                    )
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