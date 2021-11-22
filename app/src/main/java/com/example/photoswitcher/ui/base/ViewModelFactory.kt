package com.example.photoswitcher.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photoswitcher.data.repository.MainRepository
import com.example.photoswitcher.ui.viewmodel.MainViewModel
import com.example.photoswitcher.utils.FileHelper
import com.example.photoswitcher.utils.PrefHelper

class ViewModelFactory(private val prefHelper: PrefHelper, private val fileHelper: FileHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(prefHelper), fileHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}